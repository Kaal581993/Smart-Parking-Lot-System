#Address

curl -X POST http://localhost:8080/address/add_address \
-H "Content-Type: application/json" \
-d '{"flatNoHouseNo":"A/204","societyNameAptName":"Gracy Arc co.op. hsg","areaName":"khau gali rd.","townOrCityName":"Bhayandar West","countryName":"India","pincode":"401101"}'

curl -X POST http://localhost:8080/address/add_address \
-H "Content-Type: application/json" \
-d '{"flatNoHouseNo":"A/301","societyNameAptName":"Gracy Arc co.op. hsg","areaName":"khau gali rd.","townOrCityName":"Bhayandar West","countryName":"India","pincode":"401101"}'

curl -X GET "http://localhost:8080/address/get_address"

curl -X GET "http://localhost:8080/address/get_address?addressId={addressId}"

curl -X DELETE http://localhost:8080/address/delete_address/{addressId}


#Aadhaar

curl -X POST http://localhost:8080/aadhaar/add_aadhaar \
-H "Content-Type: application/json" \
-d '{"aadhaarNo":"592470644664","firstName":"Viral","lastName":"Prajapati","middleName":"Ganeshbhai","gender":"MALE","addressId":"66c39fbf-60b7-424d-a845-a3c34375721e","dateOfBirth":"08/05/1993","email":"viral.prajapati.nmims@gmail.com","panNo":"BXHPP7990R","phone":"9967126565"}'

curl -X POST http://localhost:8080/aadhaar/add_aadhaar \
-H "Content-Type: application/json" \
-d '{"aadhaarNo":"592470677665","firstName":"Mithil","lastName":"Jain","middleName":"Prakash","gender":"MALE","addressId":"2c29c0df-af87-4174-8f01-cb5940d0cbcb","dateOfBirth":"16/07/1995","email":"mithil.jain.hinduja@gmail.com","panNo":"BXQQH8768D","phone":"7865664678"}'

curl -X GET "http://localhost:8080/aadhaar/get_aadhaar"

curl -X GET "http://localhost:8080/aadhaar/get_aadhaar?ownerAadhaarId={ownerAadhaarId}"

curl -X DELETE http://localhost:8080/aadhaar/delete_aadhaar/{ownerAadhaarId}


#License

curl -X POST http://localhost:8080/driving_license/add_license \
-H "Content-Type: application/json" \
-d '{"stateCode":"MH","rtoDistrictCode":"04","seriesCode":"2013002","serialNumber":"8755"}'

curl -X POST http://localhost:8080/driving_license/add_license \
-H "Content-Type: application/json" \
-d '{"stateCode":"MH","rtoDistrictCode":"04","seriesCode":"2013008","serialNumber":"9795"}'

curl -X GET "http://localhost:8080/driving_license/get_license"

curl -X GET "http://localhost:8080/driving_license/get_license?licenseId={licenseId}"

curl -X DELETE http://localhost:8080/driving_license/delete_license/{licenseId}


#VehicleType

curl -X POST http://localhost:8080/vehicle_type/add_vehicle_type \
-H "Content-Type: application/json" \
-d '{"vehicleTypeName":"Car","vehicleTypeDescription":"Four wheeler"}'

curl -X GET "http://localhost:8080/vehicle_type/get_vehicle_type"

curl -X GET "http://localhost:8080/vehicle_type/get_vehicle_type?vehicleTypeId={vehicleTypeId}"

curl -X PUT http://localhost:8080/vehicle_type/update_vehicle_type/{vehicleTypeId} \
-H "Content-Type: application/json" \
-d '{"vehicleTypeName":"Car","vehicleTypeDescription":"Updated"}'

curl -X DELETE http://localhost:8080/vehicle_type/delete_vehicle_type/{vehicleTypeId}


#VehicleDetails

curl -X POST http://localhost:8080/vehicle_details/add_vehicle_details \
-H "Content-Type: application/json" \
-d '{"vehicleNumber":"MH04AB1234","vehicleTypeId":"{vehicleTypeId}"}'

curl -X GET "http://localhost:8080/vehicle_details/get_vehicle_details"

curl -X GET "http://localhost:8080/vehicle_details/get_vehicle_details?vehicleDetailsId={vehicleDetailsId}"

curl -X DELETE http://localhost:8080/vehicle_details/delete_vehicle_details/{vehicleDetailsId}


#VehicleOwner

curl -X POST http://localhost:8080/vehicle_owner/add_vehicle_owner \
-H "Content-Type: application/json" \
-d '{"aadhaarNo":"592470644664","licenseNumber":"MH0420130028755","vehicleDetailsId":"{vehicleDetailsId}"}'

curl -X GET "http://localhost:8080/vehicle_owner/get_vehicle_owner"

curl -X GET "http://localhost:8080/vehicle_owner/get_vehicle_owner?vehicleOwnerId={vehicleOwnerId}"

curl -X GET "http://localhost:8080/vehicle_owner/get_vehicle_owner?aadhaarNumber=592470644664"

curl -X GET "http://localhost:8080/vehicle_owner/get_vehicle_owner?licenseNumber=MH0420130028755"

curl -X PUT http://localhost:8080/vehicle_owner/update_vehicle_owner_by_aadhaar_number/592470644664 \
-H "Content-Type: application/json" \
-d '{"aadhaarNo":"592470644664","licenseNumber":"MH0420130028755","vehicleDetailsId":"{vehicleDetailsId}"}'

curl -X DELETE http://localhost:8080/vehicle_owner/delete_vehicle_owner/{vehicleOwnerId}


#ParkingSlot

curl -X POST http://localhost:8080/parking_lot/add_parking_slot_details \
-H "Content-Type: application/json" \
-d '{"slotNumber":"P1","slotStatus":"AVAILABLE"}'

curl -X GET "http://localhost:8080/parking_lot/get_parking_slot"

curl -X GET "http://localhost:8080/parking_lot/get_parking_slot?parkingSlotId={parkingSlotId}"

curl -X DELETE http://localhost:8080/parking_lot/delete_parking_slot/{parkingSlotId}
