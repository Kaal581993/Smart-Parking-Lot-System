#!/usr/bin/env bash

set -euo pipefail

# Usage:
#   ./initialize_privileges.sh
#   BASE_URL=http://localhost:8080 ADMIN_USER=admin ADMIN_PASS=admin123 \
#     ./initialize_privileges.sh
#
# The script is safe to run repeatedly. Existing privileges are reused and
# assigning an existing privilege to a role is idempotent in the API.

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-admin123}"
ASSIGN_ROLE="${ASSIGN_ROLE:-ADMIN}"
MAX_ATTEMPTS="${MAX_ATTEMPTS:-30}"
RETRY_DELAY_SECONDS="${RETRY_DELAY_SECONDS:-2}"
BASE_URL="${BASE_URL%/}"

die() {
    printf 'ERROR: %s\n' "$*" >&2
    exit 1
}

command -v curl >/dev/null 2>&1 || die "curl is required"
command -v grep >/dev/null 2>&1 || die "grep is required"
command -v sed >/dev/null 2>&1 || die "sed is required"

[[ -n "$ASSIGN_ROLE" ]] || die "ASSIGN_ROLE cannot be empty"
[[ "$ASSIGN_ROLE" =~ ^[A-Za-z0-9_-]+$ ]] || die "ASSIGN_ROLE must contain only letters, numbers, '_' or '-'"

# Escape values used in JSON request bodies without requiring jq.
json_escape() {
    local value="$1"
    value=${value//\\/\\\\}
    value=${value//\"/\\\"}
    value=${value//$'\r'/\\r}
    value=${value//$'\n'/\\n}
    value=${value//$'\t'/\\t}
    printf '%s' "$value"
}

is_success() {
    [[ "$1" =~ ^2[0-9][0-9]$ ]]
}

# The backend returns the response body followed by the HTTP status code.
LAST_STATUS=""
LAST_BODY=""
api_request() {
    local method="$1"
    local path="$2"
    local payload="${3:-}"
    local response
    local -a curl_args

    curl_args=(
        -sS
        -w $'\n%{http_code}'
        -X "$method"
        "$BASE_URL$path"
        -H "Authorization: Bearer $ADMIN_TOKEN"
    )
    if [[ -n "$payload" ]]; then
        curl_args+=(
            -H 'Content-Type: application/json'
            -d "$payload"
        )
    fi

    if ! response=$(curl "${curl_args[@]}" 2>/dev/null); then
        die "Unable to call $method $BASE_URL$path"
    fi

    LAST_STATUS="${response##*$'\n'}"
    LAST_BODY="${response%$'\n'*}"
}

login_response=""
login_body=""
login_status=""
ADMIN_TOKEN=""

printf 'Waiting for %s and logging in as %s...\n' "$BASE_URL" "$ADMIN_USER"
for ((attempt = 1; attempt <= MAX_ATTEMPTS; attempt++)); do
    login_payload=$(printf '{"username":"%s","password":"%s"}' \
        "$(json_escape "$ADMIN_USER")" "$(json_escape "$ADMIN_PASS")")

    if login_response=$(curl -sS -w $'\n%{http_code}' -X POST "$BASE_URL/auth/login" \
        -H 'Content-Type: application/json' \
        -d "$login_payload" 2>/dev/null); then
        login_status="${login_response##*$'\n'}"
        login_body="${login_response%$'\n'*}"
        if is_success "$login_status"; then
            ADMIN_TOKEN=$(printf '%s' "$login_body" | sed -nE 's/.*"token"[[:space:]]*:[[:space:]]*"([^"]+)".*/\1/p')
            if [[ -n "$ADMIN_TOKEN" ]]; then
                break
            fi
        fi
    fi

    if (( attempt < MAX_ATTEMPTS )); then
        printf 'Login attempt %d/%d failed; retrying in %ss...\n' \
            "$attempt" "$MAX_ATTEMPTS" "$RETRY_DELAY_SECONDS" >&2
        sleep "$RETRY_DELAY_SECONDS"
    fi
done

[[ -n "$ADMIN_TOKEN" ]] || die "Admin login failed (HTTP ${login_status:-unavailable}): ${login_body:-no response}"
printf 'Login succeeded.\n'

# These names cover the CRUD endpoints for the eleven application resources:
# address, app_user, license_no, owner_aadhaar, parking_slot, parking_ticket,
# privilege, role, vehicle_details, vehicle_owner and vehicle_type.
CRUD_RESOURCES=(
    ADDRESSES
    USERS
    LICENSES
    OWNER_AADHAAR
    PARKING_SLOTS
    PARKING_TICKETS
    PRIVILEGES
    ROLES
    VEHICLES
    OWNERS
    VEHICLE_TYPES
)

PRIVILEGES=()
for resource in "${CRUD_RESOURCES[@]}"; do
    for operation in READ CREATE UPDATE DELETE; do
        PRIVILEGES+=("${operation}_${resource}")
    done
done

# These cover routes that do not fit a simple resource CRUD operation.
SPECIAL_PRIVILEGES=(
    AUTH_LOGIN
    AUTH_LOGOUT
    AUTH_REGISTER_OWNER
    ASSIGN_ROLE_PRIVILEGES
    READ_OWNER_VEHICLES
    CREATE_OWNER_VEHICLES
    READ_ACTIVE_VEHICLE_TICKETS
    BATCH_CREATE_VEHICLES
    READ_SLOT_TICKETS
    SCAN_OVERSTAY
    CHECK_IN_TICKETS
    CHECK_OUT_TICKETS
    PAY_TICKETS
    BATCH_CHECK_IN_TICKETS
    UNDO_LAST_CHECKIN
    READ_ACTUATOR_HEALTH
    READ_ACTUATOR_METRICS
    READ_ACTUATOR_INFO
)
PRIVILEGES+=("${SPECIAL_PRIVILEGES[@]}")

# Preserve names used by the existing RBAC curl documentation.
COMPATIBILITY_PRIVILEGES=(
    READ_SLOTS
    MANAGE_SLOTS
    WRITE_VEHICLES
)
PRIVILEGES+=("${COMPATIBILITY_PRIVILEGES[@]}")

PRIVILEGE_LIST=""
ROLE_LIST=""

refresh_privileges() {
    if ! PRIVILEGE_LIST=$(curl -fsS "$BASE_URL/auth/admin/privileges" \
        -H "Authorization: Bearer $ADMIN_TOKEN"); then
        die "Unable to read existing privileges"
    fi
}

refresh_roles() {
    if ! ROLE_LIST=$(curl -fsS "$BASE_URL/auth/admin/roles" \
        -H "Authorization: Bearer $ADMIN_TOKEN"); then
        die "Unable to read existing roles"
    fi
}

json_has_name() {
    local json="$1"
    local name="$2"
    printf '%s' "$json" | grep -Eq \
        "\"name\"[[:space:]]*:[[:space:]]*\"${name}\"[[:space:]]*[,}]"
}

ensure_privilege() {
    local name="$1"
    local failed_status
    local failed_body

    if json_has_name "$PRIVILEGE_LIST" "$name"; then
        printf 'Privilege exists: %s\n' "$name"
        return
    fi

    api_request POST /auth/admin/privileges \
        "$(printf '{"name":"%s"}' "$(json_escape "$name")")"
    if is_success "$LAST_STATUS"; then
        printf 'Privilege created: %s\n' "$name"
        return
    fi

    # Handle a concurrent initializer or a privilege created manually between
    # the list request and the create request.
    failed_status="$LAST_STATUS"
    failed_body="$LAST_BODY"
    refresh_privileges
    if json_has_name "$PRIVILEGE_LIST" "$name"; then
        printf 'Privilege exists: %s\n' "$name"
        return
    fi
    die "Could not create privilege $name (HTTP $failed_status): $failed_body"
}

ensure_role() {
    local failed_status
    local failed_body

    refresh_roles
    if json_has_name "$ROLE_LIST" "$ASSIGN_ROLE"; then
        printf 'Role exists: %s\n' "$ASSIGN_ROLE"
        return
    fi

    api_request POST /auth/admin/roles \
        "$(printf '{"name":"%s"}' "$(json_escape "$ASSIGN_ROLE")")"
    if is_success "$LAST_STATUS"; then
        printf 'Role created: %s\n' "$ASSIGN_ROLE"
        return
    fi

    failed_status="$LAST_STATUS"
    failed_body="$LAST_BODY"
    refresh_roles
    if json_has_name "$ROLE_LIST" "$ASSIGN_ROLE"; then
        printf 'Role exists: %s\n' "$ASSIGN_ROLE"
        return
    fi
    die "Could not create role $ASSIGN_ROLE (HTTP $failed_status): $failed_body"
}

refresh_privileges
ensure_role

for privilege in "${PRIVILEGES[@]}"; do
    ensure_privilege "$privilege"
done

printf 'Assigning privileges to role %s...\n' "$ASSIGN_ROLE"
for privilege in "${PRIVILEGES[@]}"; do
    api_request POST "/auth/admin/roles/${ASSIGN_ROLE}/privileges/${privilege}"
    if is_success "$LAST_STATUS"; then
        printf 'Assigned %s -> %s\n' "$privilege" "$ASSIGN_ROLE"
    else
        die "Could not assign $privilege to $ASSIGN_ROLE (HTTP $LAST_STATUS): $LAST_BODY"
    fi
done

printf 'Initialized and assigned %d privileges to %s.\n' "${#PRIVILEGES[@]}" "$ASSIGN_ROLE"
