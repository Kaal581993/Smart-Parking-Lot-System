#!/usr/bin/env bash
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-admin123}"

# ---------- helpers ----------
api() {
    # $1=METHOD $2=PATH $3=DATA (optional) $4=TOKEN (optional)
    local method="$1" path="$2" data="${3:-}" token="${4:-}"
    if [[ -n "$data" && -n "$token" ]]; then
        curl -s -o /dev/null -w "%{http_code}" -X "$method" "$BASE_URL$path" \
            -H "Content-Type: application/json" \
            -H "Authorization: Bearer $token" \
            -d "$data"
    elif [[ -n "$data" ]]; then
        curl -s -o /dev/null -w "%{http_code}" -X "$method" "$BASE_URL$path" \
            -H "Content-Type: application/json" \
            -d "$data"
    elif [[ -n "$token" ]]; then
        curl -s -o /dev/null -w "%{http_code}" -X "$method" "$BASE_URL$path" \
            -H "Authorization: Bearer $token"
    else
        curl -s -o /dev/null -w "%{http_code}" -X "$method" "$BASE_URL$path"
    fi
}

create_role() {
    local name="$1" code
    code=$(api POST "/auth/admin/roles" "{\"name\":\"$name\"}" "$ADMIN_TOKEN")
    if [[ "$code" == "201" || "$code" == "200" ]]; then
        echo "Role created: $name"
    else
        echo "Role exists or skipped: $name (HTTP $code)"
    fi
}

create_privilege() {
    local name="$1" code
    code=$(api POST "/auth/admin/privileges" "{\"name\":\"$name\"}" "$ADMIN_TOKEN")
    if [[ "$code" == "201" || "$code" == "200" ]]; then
        echo "Privilege created: $name"
    else
        echo "Privilege exists or skipped: $name (HTTP $code)"
    fi
}

assign_privilege() {
    local role="$1" priv="$2" code
    code=$(api POST "/auth/admin/roles/$role/privileges/$priv" "" "$ADMIN_TOKEN")
    if [[ "$code" == "200" || "$code" == "201" ]]; then
        echo "Assigned $priv -> $role"
    else
        echo "Already assigned or skipped: $priv -> $role (HTTP $code)"
    fi
}

create_user_if_missing() {
    local username="$1" password="$2" role="$3" ownerId="${4:-}" code
    code=$(api POST "/auth/admin/users" "{\"username\":\"$username\",\"password\":\"$password\",\"role\":\"$role\",\"ownerId\":\"$ownerId\"}" "$ADMIN_TOKEN")
    if [[ "$code" == "201" || "$code" == "200" ]]; then
        echo "User created: $username"
    else
        echo "User exists or skipped: $username (HTTP $code)"
    fi
}

# ---------- login ----------
echo "Logging in as $ADMIN_USER..."
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}")
ADMIN_TOKEN=$(echo "$LOGIN_RESP" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

if [[ -z "$ADMIN_TOKEN" ]]; then
    echo "ERROR: Admin login failed. Response: $LOGIN_RESP"
    exit 1
fi
echo "Admin token acquired."

# ---------- roles ----------
ROLES=(
    ADMIN
    President
    "Vice President"
    "General Manager"
    "Regional Manager"
    Manager
    "Department Head"
    "Team Lead"
    "Slot Allocator"
    OWNER
)

for r in "${ROLES[@]}"; do
    create_role "$r"
done

# ---------- privileges ----------
# Per-controller CRUD privileges
CONTROLLERS=(
    VEHICLE
    VEHICLE_OWNER
    VEHICLE_TYPE
    ADDRESS
    AADHAAR
    LICENSE
    PARKING_SLOT
    PARKING_TICKET
)

for c in "${CONTROLLERS[@]}"; do
    for op in READ CREATE UPDATE DELETE; do
        create_privilege "${c}_${op}"
    done
done

# Admin-only privileges
create_privilege "USER_READ"
create_privilege "USER_CREATE"
create_privilege "USER_UPDATE"
create_privilege "USER_DELETE"
create_privilege "ROLE_READ"
create_privilege "ROLE_CREATE"
create_privilege "ROLE_UPDATE"
create_privilege "ROLE_DELETE"
create_privilege "PRIVILEGE_READ"
create_privilege "PRIVILEGE_CREATE"
create_privilege "PRIVILEGE_ASSIGN"

# ---------- assign privileges ----------
# Slot Allocator: CRUD on Vehicle, VehicleOwner, VehicleType, Address, Aadhaar, License, ParkingSlot
SLOT_ALLOCATOR_CONTROLLERS=(VEHICLE VEHICLE_OWNER VEHICLE_TYPE ADDRESS AADHAAR LICENSE PARKING_SLOT)
for c in "${SLOT_ALLOCATOR_CONTROLLERS[@]}"; do
    for op in READ CREATE UPDATE DELETE; do
        assign_privilege "Slot Allocator" "${c}_${op}"
    done
done

# All other non-admin roles: READ on all controllers
READ_ROLES=("President" "Vice President" "General Manager" "Regional Manager" "Manager" "Department Head" "Team Lead" "OWNER")
for role in "${READ_ROLES[@]}"; do
    for c in "${CONTROLLERS[@]}"; do
        assign_privilege "$role" "${c}_READ"
    done
done

# Admin: all privileges
ALL_PRIVS=()
for c in "${CONTROLLERS[@]}"; do
    for op in READ CREATE UPDATE DELETE; do
        ALL_PRIVS+=("${c}_${op}")
    done
done
ALL_PRIVS+=("USER_READ" "USER_CREATE" "USER_UPDATE" "USER_DELETE"
            "ROLE_READ" "ROLE_CREATE" "ROLE_UPDATE" "ROLE_DELETE"
            "PRIVILEGE_READ" "PRIVILEGE_CREATE" "PRIVILEGE_ASSIGN")
for p in "${ALL_PRIVS[@]}"; do
    assign_privilege "ADMIN" "$p"
done

# ---------- seed admin user (if not exists) ----------
create_user_if_missing "admin" "admin123" "ADMIN" ""

echo "Initial RBAC setup complete."
EOF
chmod +x first_initial_setup.sh
echo "created"