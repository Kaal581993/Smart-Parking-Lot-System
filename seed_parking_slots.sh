#!/usr/bin/env bash
# Fill each floor to its capacity, using vehicle types as parking sections.
set -euo pipefail
if ! command -v python3 >/dev/null 2>&1; then
    echo "ERROR: python3 is required." >&2
    exit 1
fi
exec python3 - "$@" <<'PY'
import argparse
from collections import Counter, defaultdict
from concurrent.futures import ThreadPoolExecutor
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request


class API:
    def __init__(self, base):
        self.base = base.rstrip("/")
        self.token = ""

    def request(self, method, path, body=None):
        headers = {"Accept": "application/json"}
        if self.token:
            headers["Authorization"] = "Bearer " + self.token
        if body is not None:
            headers["Content-Type"] = "application/json"
        request = urllib.request.Request(self.base + path, method=method, headers=headers,
            data=None if body is None else json.dumps(body).encode())
        try:
            with urllib.request.urlopen(request, timeout=60) as response:
                data = response.read()
                return json.loads(data) if data else None
        except urllib.error.HTTPError as error:
            raise RuntimeError(f"{method} {path}: HTTP {error.code}: {error.read().decode(errors='replace')[:400]}") from None
        except urllib.error.URLError as error:
            raise RuntimeError(f"Cannot reach {self.base}: {error.reason}") from None

    def items(self, path):
        result = []
        page = 0
        while True:
            data = self.request("GET", f"{path}?page={page}&size=2000")
            if isinstance(data, list):
                return data
            result.extend(data["content"])
            if data["last"]:
                return result
            page += 1


def section_code(index):
    result = ""
    index += 1
    while index:
        index, digit = divmod(index - 1, 26)
        result = chr(65 + digit) + result
    return result


def run(api, args):
    types = api.items("/vehicle_type/get_vehicle_type")
    if not types:
        raise RuntimeError("No vehicle types exist. Run setup_vehicle_types.sh before seeding parking slots.")
    # Treat duplicate type names as one physical section while preserving the
    # type IDs of existing slots that already refer to those duplicate rows.
    groups = defaultdict(list)
    for vehicle_type in types:
        groups[vehicle_type["typeName"].strip().casefold()].append(vehicle_type)
    sections = [sorted(group, key=lambda item: item["typeId"])[0] for _, group in sorted(groups.items())]
    if len(sections) > args.slots_per_floor:
        raise RuntimeError("There are more vehicle types than slots per floor.")
    type_section = {item["typeId"]: index for index, section in enumerate(sections)
                    for item in groups[section["typeName"].strip().casefold()]}
    all_slots = api.items("/parking_slots")
    floor_slots = defaultdict(list)
    used_numbers = {item["slotNumber"] for item in all_slots}
    for slot in all_slots:
        floor_slots[slot["floorNo"]].append(slot)
    planned = []
    for floor in range(1, args.floors + 1):
        existing = floor_slots[floor]
        if len(existing) > args.slots_per_floor:
            raise RuntimeError(f"Floor {floor} already has {len(existing)} slots, exceeding the requested {args.slots_per_floor}. No slots were changed.")
        counts = [0] * len(sections)
        unassigned = []
        updates = {}
        for slot in existing:
            section = type_section.get((slot.get("vehicleTypeAllowed") or {}).get("typeId"))
            if section is None:
                unassigned.append(slot)
            else:
                counts[section] += 1
            if slot.get("floorLimit") != args.floors or slot.get("floorSlotCapacity") != args.slots_per_floor:
                updates[slot["slotId"]] = dict(slot, floorLimit=args.floors, floorSlotCapacity=args.slots_per_floor)
        for slot in unassigned:
            section = min(range(len(sections)), key=lambda index: (counts[index], index))
            counts[section] += 1
            updated = updates.setdefault(slot["slotId"], dict(slot))
            updated.update(floorLimit=args.floors, floorSlotCapacity=args.slots_per_floor,
                           vehicleTypeAllowed={"typeId": sections[section]["typeId"]})
        additions = []
        serials = [0] * len(sections)
        for _ in range(args.slots_per_floor - len(existing)):
            section = min(range(len(sections)), key=lambda index: (counts[index], index))
            counts[section] += 1
            vehicle_type = sections[section]
            while True:
                serials[section] += 1
                number = f"F{floor:02d}-{section_code(section)}-{serials[section]:03d}"
                if number not in used_numbers:
                    used_numbers.add(number)
                    break
            additions.append({"slotNumber": number, "floorNo": floor, "floorLimit": args.floors,
                "floorSlotCapacity": args.slots_per_floor, "costPerHour": vehicle_type["hourlyRate"],
                "vehicleTypeAllowed": {"typeId": vehicle_type["typeId"]}, "allocationDuration": "PT10H",
                "available": True, "active": True})
        planned.append((floor, list(updates.values()), additions))
    print(f"Plan: {args.floors} floors × {args.slots_per_floor} slots = {args.floors * args.slots_per_floor} slots.")
    print(f"Create {sum(len(additions) for _, _, additions in planned)} slots; update {sum(len(updates) for _, updates, _ in planned)} existing slots.")
    for index, vehicle_type in enumerate(sections):
        print(f"Section {section_code(index)}: {vehicle_type['typeName']}")
    print("Sections are balanced per floor, preserving existing vehicle-type assignments.", flush=True)
    if args.dry_run:
        print("Dry run complete; no parking records changed.")
        return
    with ThreadPoolExecutor(max_workers=args.workers) as executor:
        for floor, updates, additions in planned:
            for slot in updates:
                api.request("PUT", "/parking_slots/" + urllib.parse.quote(slot["slotId"], safe=""), slot)
            list(executor.map(lambda payload: api.request("POST", "/parking_slots", payload), additions))
            print(f"Floor {floor:02d}: {args.slots_per_floor} slots ready ({len(additions)} created, {len(updates)} updated).", flush=True)
    final = [slot for slot in api.items("/parking_slots") if 1 <= slot["floorNo"] <= args.floors]
    counts = Counter(slot["floorNo"] for slot in final)
    if any(counts[floor] != args.slots_per_floor for floor in range(1, args.floors + 1)):
        raise RuntimeError("Final floor counts differ from the plan. Rerun after checking for concurrent slot changes.")
    if any(slot.get("floorLimit") != args.floors or slot.get("floorSlotCapacity") != args.slots_per_floor
           or (slot.get("vehicleTypeAllowed") or {}).get("typeId") not in type_section for slot in final):
        raise RuntimeError("Final slot metadata differs from the plan.")
    print(f"Verified {len(final)} slots across {args.floors} floors.")
    totals = Counter(type_section[slot["vehicleTypeAllowed"]["typeId"]] for slot in final)
    for index, vehicle_type in enumerate(sections):
        print(f"Section {section_code(index)} — {vehicle_type['typeName']}: {totals[index]} slots total.")


def main():
    parser = argparse.ArgumentParser(prog="seed_parking_slots.sh", description="Fill 55 floors with 80 slots each, divided into vehicle-type sections. Safe to rerun.")
    parser.add_argument("--floors", type=int, default=55)
    parser.add_argument("--slots-per-floor", type=int, default=80)
    parser.add_argument("--workers", type=int, default=4)
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--base-url", default=os.environ.get("BASE_URL", "http://localhost:8080"))
    args = parser.parse_args()
    if not 1 <= args.floors <= 200 or not 1 <= args.slots_per_floor <= 1000 or not 1 <= args.workers <= 16:
        parser.error("floors must be 1..200, slots-per-floor 1..1000, and workers 1..16")
    if urllib.parse.urlsplit(args.base_url).scheme not in ("http", "https"):
        parser.error("base-url must use http or https")
    api = API(args.base_url)
    try:
        api.token = api.request("POST", "/auth/login", {"username": os.environ.get("ADMIN_USER", "admin"),
                                                        "password": os.environ.get("ADMIN_PASS", "admin123")})["token"]
        if "ROLE_ADMIN" not in api.request("GET", "/auth/me").get("authorities", []):
            raise RuntimeError("ADMIN_USER must have the ADMIN role.")
        run(api, args)
    finally:
        if api.token:
            try:
                api.request("POST", "/auth/logout")
            except (RuntimeError, OSError):
                pass


try:
    main()
except (RuntimeError, ValueError, KeyError, OSError) as error:
    print(f"ERROR: {error}\nFix the error and rerun; existing slots are reused.", file=sys.stderr)
    sys.exit(1)
except KeyboardInterrupt:
    print("\nInterrupted. Rerun the same command to finish the remaining slots.", file=sys.stderr)
    sys.exit(130)
PY
