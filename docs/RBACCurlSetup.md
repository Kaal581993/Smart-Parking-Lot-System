# RBAC Initial Setup (curl)

## 1. Login as seeded admin
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"admin","password":"admin123"}'

# Save the returned token as ADMIN_TOKEN

## 2. Create privileges
curl -X POST http://localhost:8080/auth/admin/privileges \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"name":"READ_VEHICLES"}'

curl -X POST http://localhost:8080/auth/admin/privileges \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"name":"WRITE_VEHICLES"}'

curl -X POST http://localhost:8080/auth/admin/privileges \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"name":"READ_SLOTS"}'

curl -X POST http://localhost:8080/auth/admin/privileges \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"name":"MANAGE_SLOTS"}'

## 3. Create roles
curl -X POST http://localhost:8080/auth/admin/roles \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"name":"ADMIN"}'

curl -X POST http://localhost:8080/auth/admin/roles \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"name":"OWNER"}'

## 4. Assign privileges to roles
curl -X POST http://localhost:8080/auth/admin/roles/ADMIN/privileges/MANAGE_SLOTS \
-H "Authorization: Bearer {ADMIN_TOKEN}"

curl -X POST http://localhost:8080/auth/admin/roles/OWNER/privileges/READ_VEHICLES \
-H "Authorization: Bearer {ADMIN_TOKEN}"

curl -X POST http://localhost:8080/auth/admin/roles/OWNER/privileges/READ_SLOTS \
-H "Authorization: Bearer {ADMIN_TOKEN}"

## 5. Create an owner user (admin only)
curl -X POST http://localhost:8080/auth/admin/users \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"username":"owner1","password":"pass123","role":"OWNER","ownerId":"592470644664"}'

## 6. Register owner (self-registration)
curl -X POST http://localhost:8080/auth/register-owner \
-H "Content-Type: application/json" \
-d '{"username":"owner2","password":"pass456","ownerId":"592470644664"}'

## 7. Owner login
curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"owner1","password":"pass123"}'

# Save the returned token as OWNER_TOKEN

## 8. Owner accesses GET endpoints (isolated to own vehicles)
curl -X GET "http://localhost:8080/vehicles" \
-H "Authorization: Bearer {OWNER_TOKEN}"

curl -X GET "http://localhost:8080/vehicle_owners/592470644664/vehicles" \
-H "Authorization: Bearer {OWNER_TOKEN}"

curl -X GET "http://localhost:8080/parking_slots?available=true" \
-H "Authorization: Bearer {OWNER_TOKEN}"

## 9. Admin accesses all endpoints
curl -X GET "http://localhost:8080/vehicle_owners" \
-H "Authorization: Bearer {ADMIN_TOKEN}"

curl -X POST http://localhost:8080/parking_slots \
-H "Content-Type: application/json" \
-H "Authorization: Bearer {ADMIN_TOKEN}" \
-d '{"slotNumber":"P1","floorNo":1,"floorLimit":2,"floorSlotCapacity":10,"costPerHour":20.0}'

## 10. Logout
curl -X POST http://localhost:8080/auth/logout \
-H "Authorization: Bearer {OWNER_TOKEN}"
EOF
echo done
