# Address

curl -X POST http://localhost:8080/address/add_address \
-H "Content-Type: application/json" \
-d '{"flatNoHouseNo":"A/204","societyNameAptName":"Gracy Arc co.op. hsg","areaName":"khau gali rd.","townOrCityName":"Bhayandar West","countryName":"India","pincode":"401101","stateCode":"MH"}'

curl -X GET \
  "http://localhost:8080/address/get_address"

curl -X GET \
  "http://localhost:8080/address/get_address?addressId={addressId}"

curl -X PUT http://localhost:8080/address/update_address \
-H "Content-Type: application/json" \
-d '{"addressId":"{addressId}","flatNoHouseNo":"A/204","societyNameAptName":"Gracy Arc co.op. hsg","areaName":"khau gali rd.","townOrCityName":"Bhayandar West","countryName":"India","pincode":"401101","stateCode":"MH"}'

curl -X DELETE \
  http://localhost:8080/address/delete_address/{addressId}


# Aadhaar

curl -X POST http://localhost:8080/aadhaar/add_aadhaar \
-H "Content-Type: application/json" \
-d '{"aadhaarNo":"592470644664","firstName":"Viral","lastName":"Prajapati","middleName":"Ganeshbhai","gender":"MALE","addressId":"{addressId}","dateOfBirth":"08/05/1993","email":"viral.prajapati.nmims@gmail.com","panNo":"BXHPP7990R","phone":"9967126565"}'

curl -X GET \
  "http://localhost:8080/aadhaar/get_aadhaar"

curl -X GET \
  "http://localhost:8080/aadhaar/get_aadhaar?ownerAadhaarId={ownerAadhaarId}"

curl -X PUT http://localhost:8080/aadhaar/update_aadhaar \
-H "Content-Type: application/json" \
-d '{"aadhaarNo":"592470644664","firstName":"Viral","lastName":"Prajapati","middleName":"Ganeshbhai","gender":"MALE","addressId":"{addressId}","dateOfBirth":"08/05/1993","email":"viral.prajapati.nmims@gmail.com","panNo":"BXHPP7990R","phone":"9967126565"}'

curl -X DELETE \
  http://localhost:8080/aadhaar/delete_aadhaar/{ownerAadhaarId}


# License

curl -X POST http://localhost:8080/driving_license/add_license \
-H "Content-Type: application/json" \
-d '{"stateCode":"MH","rtoDistrictCode":"04","seriesCode":"AB","serialNumber":"1234"}'

curl -X GET \
  "http://localhost:8080/driving_license/get_license"

curl -X GET \
  "http://localhost:8080/driving_license/get_license?licenseId={licenseId}"

curl -X PUT http://localhost:8080/driving_license/update_license \
-H "Content-Type: application/json" \
-d '{"licenseId":"{licenseId}","stateCode":"MH","rtoDistrictCode":"04","seriesCode":"AB","serialNumber":"1234"}'

curl -X DELETE \
  http://localhost:8080/driving_license/delete_license/{licenseId}


# VehicleType

curl -X POST http://localhost:8080/vehicle_type/add_vehicle_type \
-H "Content-Type: application/json" \
-d '{"typeName":"Car Sedan","hourlyRate":25.0,"dailyRate":120.0}'

curl -X GET \
  "http://localhost:8080/vehicle_type/get_vehicle_type"

curl -X GET \
  "http://localhost:8080/vehicle_type/get_vehicle_type?vehicleTypeId={vehicleTypeId}"

curl -X PUT http://localhost:8080/vehicle_type/update_vehicle_type/{vehicleTypeId} \
-H "Content-Type: application/json" \
-d '{"typeName":"Car Sedan","hourlyRate":30.0,"dailyRate":150.0}'

curl -X DELETE \
  http://localhost:8080/vehicle_type/delete_vehicle_type/{vehicleTypeId}


# VehicleOwner

curl -X POST http://localhost:8080/vehicle_owners \
-H "Content-Type: application/json" \
-d '{"vehicleOwnerName":"Viral Prajapati","vehicleOwnerPhone":9967126565,"vehicleOwnerEmail":"viral@example.com","aadhaarNo":"592470644664","licenseId":"{licenseId}","vehicleOwnerPanNo":"BXHPP7990R","vehicleOwnerDOB":"08/05/1993","addressId":"{addressId}","licenseExpiryDate":"08/05/2030","vehicleOwnerLedgerBalance":0.0,"vehicleOwnerDues":0.0}'

curl -X GET \
  "http://localhost:8080/vehicle_owners"

curl -X GET \
  "http://localhost:8080/vehicle_owners?aadhaarNumber=592470644664"

curl -X GET \
  "http://localhost:8080/vehicle_owners?licenseNumber={licenseId}"

curl -X GET \
  "http://localhost:8080/vehicle_owners/{aadhaarNumber}"

curl -X GET \
  "http://localhost:8080/vehicle_owners/{aadhaarNumber}/vehicles"

curl -X POST http://localhost:8080/vehicle_owners/{aadhaarNumber}/vehicles \
-H "Content-Type: application/json" \
-d '{"numberPlate":"MH04AB1234","vehicleType":"{vehicleTypeId}","vehicleBrand":"Honda","vehicleModel":"City"}'

curl -X PUT http://localhost:8080/vehicle_owners/{aadhaarNumber} \
-H "Content-Type: application/json" \
-d '{"vehicleOwnerName":"Viral Prajapati","vehicleOwnerPhone":9967126565,"vehicleOwnerEmail":"viral@example.com","aadhaarNo":"592470644664","licenseId":"{licenseId}","vehicleOwnerPanNo":"BXHPP7990R","vehicleOwnerDOB":"08/05/1993","addressId":"{addressId}","licenseExpiryDate":"08/05/2030","vehicleOwnerLedgerBalance":0.0,"vehicleOwnerDues":0.0}'

curl -X DELETE \
  http://localhost:8080/vehicle_owners/{aadhaarNumber}


# Vehicles

curl -X POST http://localhost:8080/vehicles \
-H "Content-Type: application/json" \
-d '{"numberPlate":"MH04AB1234","vehicleType":"{vehicleTypeId}","vehicleBrand":"Honda","vehicleModel":"City"}'

curl -X GET \
  "http://localhost:8080/vehicles"

curl -X GET \
  "http://localhost:8080/vehicles?vehicleDetailsId={vehicleId}"

curl -X GET \
  "http://localhost:8080/vehicles/{vehicleId}"

curl -X GET \
  "http://localhost:8080/vehicles/{vehicleId}/active-ticket"

curl -X PUT http://localhost:8080/vehicles/{vehicleId} \
-H "Content-Type: application/json" \
-d '{"vehicleId":"{vehicleId}","numberPlate":"MH04AB1234","vehicleType":"{vehicleTypeId}","vehicleBrand":"Honda","vehicleModel":"City"}'

curl -X DELETE \
  http://localhost:8080/vehicles/{vehicleId}


# ParkingSlot

curl -X POST http://localhost:8080/parking_slots \
-H "Content-Type: application/json" \
-d '{"slotNumber":"P1","floorNo":1,"floorLimit":2,"floorSlotCapacity":10,"costPerHour":20.0,"vehicleTypeAllowed":"{vehicleTypeId}","allocationDuration":"PT2H"}'

curl -X GET \
  "http://localhost:8080/parking_slots"

curl -X GET \
  "http://localhost:8080/parking_slots?floorNo=1&available=true&vehicleType=Car%20Sedan"

curl -X GET \
  "http://localhost:8080/parking_slots/{slotId}"

curl -X GET \
  "http://localhost:8080/parking_slots/{slotId}/tickets"

curl -X GET \
  "http://localhost:8080/parking_slots/scan-overstay"

curl -X PUT http://localhost:8080/parking_slots/{slotId} \
-H "Content-Type: application/json" \
-d '{"slotId":"{slotId}","slotNumber":"P1","floorNo":1,"floorLimit":2,"floorSlotCapacity":10,"costPerHour":20.0,"vehicleTypeAllowed":"{vehicleTypeId}"}'

curl -X DELETE \
  http://localhost:8080/parking_slots/{slotId}


# ParkingTicket

curl -X POST http://localhost:8080/parking_tickets/check-in \
-H "Content-Type: application/json" \
-d '{"vehicleId":"{vehicleId}","slotId":"{slotId}","allocationStrategy":"BEST_FIT"}'

curl -X POST http://localhost:8080/parking_tickets/check-in \
-H "Content-Type: application/json" \
-d '{"vehicleId":"{vehicleId}"}'

curl -X POST \
  http://localhost:8080/parking_tickets/{ticketId}/check-out

curl -X POST http://localhost:8080/parking_tickets/{ticketId}/pay \
-H "Content-Type: application/json" \
-d '{"amount":50.0}'

curl -X POST http://localhost:8080/parking_tickets/batch-check-in \
-H "Content-Type: application/json" \
-d '{"vehicleIds":["{vehicleId1}","{vehicleId2}"],"slotId":"{slotId}"}'

curl -X POST \
  http://localhost:8080/parking_tickets/undo-last-checkin

curl -X GET \
  "http://localhost:8080/parking_tickets/{ticketId}"
EOF
echo done

# Pagination & Sorting Examples

curl -X GET \
  "http://localhost:8080/vehicle_owners?page=0&size=10&sort=vehicleOwnerName,asc"

curl -X GET \
  "http://localhost:8080/vehicles?page=0&size=5&sort=numberPlate,desc"

curl -X GET \
  "http://localhost:8080/parking_slots?page=0&size=20&sort=floorNo,asc&available=true"


# Actuator

curl -X GET \
  "http://localhost:8080/actuator/health"

curl -X GET \
  "http://localhost:8080/actuator/metrics"

curl -X GET \
  "http://localhost:8080/actuator/info"


# Batch Vehicle Creation

curl -X POST http://localhost:8080/vehicles/batch \
-H "Content-Type: application/json" \
-d '{"vehicles":[{"numberPlate":"MH04AB1234","vehicleType":"{vehicleTypeId}","vehicleBrand":"Honda","vehicleModel":"City","owner":"{owner}"}]}'


# Authentication (RBAC)

curl -X POST http://localhost:8080/auth/login \
-H "Content-Type: application/json" \
-d '{"username":"admin","password":"admin123"}'

curl -X POST http://localhost:8080/auth/register-owner \
-H "Content-Type: application/json" \
-d '{"username":"owner1","password":"pass123","ownerId":"592470644664"}'

curl -X POST http://localhost:8080/auth/logout \
-H "Authorization: Bearer {token}"

# Admin: create role
curl -X POST http://localhost:8080/auth/admin/roles \
-H "Content-Type: application/json" \
-d '{"name":"OWNER"}'

# Admin: create privilege
curl -X POST http://localhost:8080/auth/admin/privileges \
-H "Content-Type: application/json" \
-d '{"name":"READ_VEHICLES"}'

# Admin: assign privilege to role
curl -X POST \
  http://localhost:8080/auth/admin/roles/OWNER/privileges/READ_VEHICLES

# Admin: create user
curl -X POST http://localhost:8080/auth/admin/users \
-H "Content-Type: application/json" \
-d '{"username":"owner2","password":"pass","role":"OWNER","ownerId":"592470644664"}'
