#!/usr/bin/env bash
# Seed linked dummy owners through the API. Python's standard library handles
# JSON, HTTP, and the credentials file; no jq or extra packages are required.
set -euo pipefail

if ! command -v python3 >/dev/null 2>&1; then
    echo "ERROR: python3 is required." >&2
    exit 1
fi

seed_script_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
exec python3 - "$seed_script_dir" "$@" <<'PY'
import argparse
import csv
import datetime
import fcntl
import json
import os
from pathlib import Path
import secrets
import sys
import urllib.error
import urllib.parse
import urllib.request


class API:
    def __init__(self, base_url):
        self.base_url = base_url.rstrip("/")
        self.token = ""

    def request(self, method, path, payload=None, token=None):
        headers = {"Accept": "application/json"}
        access_token = self.token if token is None else token
        if access_token:
            headers["Authorization"] = "Bearer " + access_token
        if payload is not None:
            headers["Content-Type"] = "application/json"
        request = urllib.request.Request(
            self.base_url + path,
            data=None if payload is None else json.dumps(payload).encode(),
            headers=headers,
            method=method,
        )
        try:
            with urllib.request.urlopen(request, timeout=30) as response:
                body = response.read()
                return json.loads(body) if body else None
        except urllib.error.HTTPError as error:
            detail = error.read().decode(errors="replace")
            raise RuntimeError(f"{method} {path}: HTTP {error.code}: {detail[:500]}") from None
        except urllib.error.URLError as error:
            raise RuntimeError(f"Cannot reach {self.base_url}: {error.reason}. Start the backend first.") from None

    def items(self, path):
        result = []
        page = 0
        while True:
            data = self.request("GET", f"{path}?page={page}&size=200")
            if isinstance(data, list):
                return data
            if not isinstance(data, dict) or not isinstance(data.get("content"), list):
                raise RuntimeError(f"Unexpected list response from {path}")
            result.extend(data["content"])
            if data.get("last", True):
                return result
            page += 1


def owner_id(owner):
    return (owner.get("aadhaar") or {}).get("aadhaarNo") or (owner.get("ownerIdentityId") or {}).get("aadharNo")


def dummy_record(index):
    serial = f"{index:04d}"
    return {
        "username": f"dummy_owner_{index:03d}",
        "owner_name": f"Dummy Owner {index:03d}",
        "aadhaar_no": "99000000" + serial,
        "license_id": "MH99ZZ" + serial,
        "number_plate": "MH99ZZ" + serial,
        "email": f"dummy.owner.{serial}@example.test",
        "phone": "900000" + serial,
        "pan": "DUMMP" + serial + "T",
        "dob": f"{index % 28 + 1:02d}/{index % 12 + 1:02d}/{1980 + index % 25}",
        "serial": serial,
    }


def seed(api, args, credentials):
    fields = ["username", "password", "owner_name", "aadhaar_no", "license_id", "number_plate"]
    credentials.seek(0)
    reader = csv.DictReader(credentials, delimiter="\t")
    if reader.fieldnames is not None and reader.fieldnames != fields:
        raise RuntimeError("The password file has an unexpected format; use a different --password-file.")
    saved = {}
    for row in reader:
        if None in row or any(not row.get(field) for field in fields) or row["username"] in saved:
            raise RuntimeError("The password file contains an incomplete or duplicate row; repair it before rerunning.")
        saved[row["username"]] = row
    credentials.seek(0, os.SEEK_END)
    writer = csv.DictWriter(credentials, fieldnames=fields, delimiter="\t", lineterminator="\n")

    users = {user["username"]: user for user in api.items("/auth/admin/users")}
    addresses = {
        address["flatNoHouseNo"]: address for address in api.items("/address/get_address")
        if address.get("societyNameAptName") == "Dummy Parking Residency"
    }
    aadhaars = {item["aadhaarNo"]: item for item in api.items("/aadhaar/get_aadhaar")}
    licenses = {item["licenseId"]: item for item in api.items("/driving_license/get_license")}
    owners = {owner_id(item): item for item in api.items("/vehicle_owners")}
    vehicles = {item["numberPlate"]: item for item in api.items("/vehicles")}
    types = api.items("/vehicle_type/get_vehicle_type")
    records = [dummy_record(index) for index in range(args.start_index, args.start_index + args.count)]

    # Check every deterministic ID before creating anything. Existing records
    # belonging to someone else must never be overwritten by a seed run.
    for record in records:
        username = record["username"]
        previous = saved.get(username)
        if previous and any(previous[field] != record[field] for field in fields if field != "password"):
            raise RuntimeError(f"Saved identity differs for {username}; use the original password file.")
        user = users.get(username)
        if user:
            if user.get("ownerId") != record["aadhaar_no"] or (user.get("role") or {}).get("name") != "OWNER":
                raise RuntimeError(f"{username} belongs to a different account; choose another --start-index.")
            if not previous:
                raise RuntimeError(f"No saved password for existing account {username}; use its original --password-file.")
        aadhaar = aadhaars.get(record["aadhaar_no"])
        if aadhaar and aadhaar.get("email") != record["email"]:
            raise RuntimeError(f"Aadhaar ID {record['aadhaar_no']} is already in use; choose another --start-index.")
        owner = owners.get(record["aadhaar_no"])
        if owner and (owner.get("vehicleOwnerEmail") != record["email"] or
                      (owner.get("licenseNo") or {}).get("licenseId") != record["license_id"]):
            raise RuntimeError(f"Owner ID {record['aadhaar_no']} is already in use; choose another --start-index.")
        license_record = licenses.get(record["license_id"])
        if license_record and any(license_record.get(key) != value for key, value in {
            "stateCode": "MAHARASHTRA", "rtoDistrictCode": "99", "seriesCode": "ZZ", "serialNumber": record["serial"]
        }.items()):
            raise RuntimeError(f"License ID {record['license_id']} is already in use; choose another --start-index.")
        vehicle = vehicles.get(record["number_plate"])
        if vehicle and owner_id(vehicle.get("owner") or {}) != record["aadhaar_no"]:
            raise RuntimeError(f"Plate {record['number_plate']} is already in use; choose another --start-index.")

    if reader.fieldnames is None:
        writer.writeheader()
        credentials.flush()
        os.fsync(credentials.fileno())
    if not types:
        types = [api.request("POST", "/vehicle_type/add_vehicle_type", {
            "typeName": "Car Sedan", "hourlyRate": 25.0, "dailyRate": 120.0,
        })]
    types.sort(key=lambda item: item["typeId"])
    created = dict.fromkeys(("addresses", "aadhaars", "licenses", "owners", "vehicles", "users"), 0)

    for position, record in enumerate(records, 1):
        username = record["username"]
        if username not in saved:
            row = {field: record[field] for field in fields if field != "password"}
            row["password"] = "Demo!" + secrets.token_urlsafe(15) + "aA1"
            # Persist the password before sending the registration request so a
            # lost HTTP response or interrupted run cannot lose credentials.
            writer.writerow(row)
            credentials.flush()
            os.fsync(credentials.fileno())
            saved[username] = row

        flat = "DEMO-" + record["serial"]
        if flat not in addresses:
            addresses[flat] = api.request("POST", "/address/add_address", {
                "flatNoHouseNo": flat, "societyNameAptName": "Dummy Parking Residency",
                "areaName": "Test Area", "townOrCityName": "Pune", "stateCode": "MAHARASHTRA",
                "countryName": "India", "pincode": "411001",
            })
            created["addresses"] += 1
        address_id = addresses[flat]["addressId"]
        if record["aadhaar_no"] not in aadhaars:
            aadhaars[record["aadhaar_no"]] = api.request("POST", "/aadhaar/add_aadhaar", {
                "aadhaarNo": record["aadhaar_no"], "firstName": "Dummy", "middleName": "Test",
                "lastName": "Owner" + record["serial"], "gender": "FEMALE" if position % 2 else "MALE",
                "dateOfBirth": record["dob"], "email": record["email"], "phone": record["phone"],
                "addressId": address_id, "panNo": record["pan"],
            })
            created["aadhaars"] += 1
        if record["license_id"] not in licenses:
            licenses[record["license_id"]] = api.request("POST", "/driving_license/add_license", {
                "licenseId": record["license_id"], "stateCode": "MAHARASHTRA", "rtoDistrictCode": "99",
                "seriesCode": "ZZ", "serialNumber": record["serial"],
            })
            created["licenses"] += 1
        if record["aadhaar_no"] not in owners:
            owners[record["aadhaar_no"]] = api.request("POST", "/vehicle_owners", {
                "vehicleOwnerName": record["owner_name"], "vehicleOwnerPhone": int(record["phone"]),
                "vehicleOwnerEmail": record["email"], "aadhaar": {"aadhaarNo": record["aadhaar_no"]},
                "licenseNo": {"licenseId": record["license_id"]},
                "ownerIdentityId": {"aadharNo": record["aadhaar_no"], "licenseNo": record["license_id"]},
                "vehicleOwnerPanNo": record["pan"], "vehicleOwnerDOB": record["dob"],
                "vehicleOwnerAddress": {"addressId": address_id},
                "licenseExpiryDate": f"31/12/{datetime.date.today().year + 5}",
                "vehicleOwnerLedgerBalance": 1000.0, "vehicleOwnerDues": 0.0,
            })
            created["owners"] += 1
        if record["number_plate"] not in vehicles:
            vehicle_type = types[(position - 1) % len(types)]
            vehicles[record["number_plate"]] = api.request("POST", "/vehicles", {
                "numberPlate": record["number_plate"], "vehicleType": {"typeId": vehicle_type["typeId"]},
                "owner": {"aadhaar": {"aadhaarNo": record["aadhaar_no"]}},
                "vehicleBrand": "Demo Motors", "vehicleModel": "Demo " + vehicle_type["typeName"],
            })
            created["vehicles"] += 1
        if username not in users:
            users[username] = api.request("POST", "/auth/register-owner", {
                "username": username, "password": saved[username]["password"], "ownerId": record["aadhaar_no"],
            })
            created["users"] += 1

        # Confirm that every saved password actually signs in, including reruns.
        login = api.request("POST", "/auth/login", {
            "username": username, "password": saved[username]["password"],
        }, token="")
        api.request("POST", "/auth/logout", token=login["token"])
        if position % 10 == 0 or position == args.count:
            print(f"{position}/{args.count} owners, licenses, vehicles, and logins ready.", flush=True)

    print("Created this run: " + ", ".join(f"{count} {name}" for name, count in created.items()))
    print(f"Credentials: {args.password_file} (tab-separated text, permissions 600)")


def main():
    script_dir = Path(sys.argv.pop(1))
    parser = argparse.ArgumentParser(prog="seed_dummy_records.sh", description="Create 100 linked dummy owners, licenses, vehicles, and OWNER logins. Reruns resume the same records.")
    parser.add_argument("--count", type=int, default=100, help="number of owners, licenses, vehicles, and users (default: 100)")
    parser.add_argument("--start-index", type=int, default=1, help="first dummy record number (default: 1)")
    parser.add_argument("--base-url", default=os.environ.get("BASE_URL", "http://localhost:8080"))
    parser.add_argument("--password-file", type=Path, default=Path(os.environ.get("PASSWORD_FILE", str(script_dir / "password.txt"))))
    args = parser.parse_args()
    if args.count < 1 or args.start_index < 1 or args.start_index + args.count - 1 > 9999:
        parser.error("count and start-index must describe a range within 1..9999")
    if urllib.parse.urlsplit(args.base_url).scheme not in ("http", "https"):
        parser.error("base-url must use http or https")
    api = API(args.base_url)
    try:
        api.token = api.request("POST", "/auth/login", {
            "username": os.environ.get("ADMIN_USER", "admin"),
            "password": os.environ.get("ADMIN_PASS", "admin123"),
        })["token"]
        if "ROLE_ADMIN" not in api.request("GET", "/auth/me").get("authorities", []):
            raise RuntimeError("ADMIN_USER must have the ADMIN role.")
        descriptor = os.open(args.password_file, os.O_RDWR | os.O_CREAT | os.O_NOFOLLOW, 0o600)
        with os.fdopen(descriptor, "r+", encoding="utf-8", newline="") as credentials:
            try:
                fcntl.flock(credentials, fcntl.LOCK_EX | fcntl.LOCK_NB)
            except BlockingIOError:
                raise RuntimeError("Another seed run is using this password file.") from None
            os.fchmod(credentials.fileno(), 0o600)
            print(f"Seeding {args.count} dummy records starting at {args.start_index} on {api.base_url}...", flush=True)
            seed(api, args, credentials)
    finally:
        if api.token:
            try:
                api.request("POST", "/auth/logout")
            except (RuntimeError, OSError):
                pass


try:
    main()
except (RuntimeError, OSError, ValueError, KeyError, csv.Error) as error:
    print(f"ERROR: {error}\nSaved passwords are retained. Fix the error and rerun the same command to resume.", file=sys.stderr)
    sys.exit(1)
except KeyboardInterrupt:
    print("\nInterrupted. Saved passwords are retained; rerun the same command to resume.", file=sys.stderr)
    sys.exit(130)
PY
