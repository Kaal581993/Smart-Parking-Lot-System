#!/usr/bin/env bash
set -u

BASE_URL="${BASE_URL:-http://localhost:8080}"
ADMIN_USER="${ADMIN_USER:-admin}"
ADMIN_PASS="${ADMIN_PASS:-admin123}"

echo "=== Vehicle Type Setup Script ==="
echo "Base URL: $BASE_URL"
echo ""

# Login as admin
echo "Logging in as $ADMIN_USER..."
LOGIN_RESP=$(curl -s -X POST "$BASE_URL/auth/login" \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"$ADMIN_USER\",\"password\":\"$ADMIN_PASS\"}")
ADMIN_TOKEN=$(echo "$LOGIN_RESP" | sed -n 's/.*"token":"\([^"]*\)".*/\1/p')

if [[ -z "$ADMIN_TOKEN" ]]; then
    echo "ERROR: Admin login failed. Response: $LOGIN_RESP"
    echo ""
    echo "Make sure the application is running and first_initial_setup.sh has been executed."
    exit 1
fi
echo "Admin token acquired."
echo ""

# Function to create vehicle type
create_vehicle_type() {
    local type_name="$1"
    local hourly_rate="$2"
    local daily_rate="$3"
    local data="{\"typeName\":\"$type_name\",\"hourlyRate\":$hourly_rate,\"dailyRate\":$daily_rate}"
    
    local code=$(curl -s -o /tmp/vt_resp.json -w "%{http_code}" -X POST "$BASE_URL/vehicle_type/add_vehicle_type" \
        -H "Content-Type: application/json" \
        -H "Authorization: Bearer $ADMIN_TOKEN" \
        -d "$data")
    
    if [[ "$code" == "201" || "$code" == "200" ]]; then
        echo "✓ Created: $type_name (Hourly: ₹$hourly_rate, Daily: ₹$daily_rate)"
    else
        echo "✗ Failed or exists: $type_name (HTTP $code)"
        if [[ -f /tmp/vt_resp.json ]]; then
            cat /tmp/vt_resp.json
            echo ""
        fi
    fi
}

echo "Creating vehicle types..."
echo ""

# Create all vehicle types as defined in the entity
create_vehicle_type "Two Wheeler" 10.0 50.0
create_vehicle_type "Three Wheeler" 15.0 75.0
create_vehicle_type "Car Hatchback" 20.0 100.0
create_vehicle_type "Car Sedan" 25.0 120.0
create_vehicle_type "SUV" 30.0 150.0
create_vehicle_type "Mini Bus" 40.0 200.0
create_vehicle_type "Bus" 50.0 250.0
create_vehicle_type "Truck" 60.0 300.0
create_vehicle_type "Electric Two Wheeler" 8.0 40.0
create_vehicle_type "Electric Car" 18.0 90.0

echo ""
echo "=== Vehicle type setup complete ==="
echo ""
echo "To verify, run:"
echo "curl -X GET '$BASE_URL/vehicle_type/get_vehicle_type' -H 'Authorization: Bearer $ADMIN_TOKEN'"
