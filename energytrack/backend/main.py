from __future__ import annotations

import json
import uuid
from collections import defaultdict
from datetime import datetime, timezone
from pathlib import Path
from typing import Optional

from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field

APP_DIR = Path(__file__).resolve().parent
DB_FILE = APP_DIR / "energytrack_db.json"
TARIFF_BRL_PER_KWH = 0.80

app = FastAPI(title="EnergyTrack Hardware API", version="1.0.0")
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=False,
    allow_methods=["*"],
    allow_headers=["*"],
)


class DeviceInput(BaseModel):
    id: Optional[str] = None
    name: str = Field(min_length=2, max_length=60)
    room: str = Field(min_length=2, max_length=60)
    type: str = "OTHER"
    active: bool = True


class TelemetryInput(BaseModel):
    deviceId: str
    voltageV: float = Field(gt=0)
    currentA: float = Field(ge=0)
    powerW: Optional[float] = Field(default=None, ge=0)
    timestamp: Optional[datetime] = None


def empty_db() -> dict:
    return {"devices": [], "telemetry": []}


def load_db() -> dict:
    if not DB_FILE.exists():
        save_db(empty_db())
    try:
        data = json.loads(DB_FILE.read_text(encoding="utf-8"))
        return {
            "devices": data.get("devices", []),
            "telemetry": data.get("telemetry", []),
        }
    except (json.JSONDecodeError, OSError):
        return empty_db()


def save_db(data: dict) -> None:
    DB_FILE.write_text(
        json.dumps(data, ensure_ascii=False, indent=2),
        encoding="utf-8",
    )


def utc_now() -> datetime:
    return datetime.now(timezone.utc)


def parse_time(value: str) -> datetime:
    return datetime.fromisoformat(value.replace("Z", "+00:00"))


def get_device_or_404(db: dict, device_id: str) -> dict:
    device = next((item for item in db["devices"] if item["id"] == device_id), None)
    if device is None:
        raise HTTPException(status_code=404, detail="Dispositivo não encontrado")
    return device


def device_events(db: dict, device_id: str) -> list[dict]:
    return sorted(
        [entry for entry in db["telemetry"] if entry["deviceId"] == device_id],
        key=lambda entry: entry["timestamp"],
    )


def same_local_day(timestamp: str, ref: datetime) -> bool:
    return parse_time(timestamp).date() == ref.date()


def energy_today(db: dict, device_id: str, reference: Optional[datetime] = None) -> float:
    ref = reference or utc_now()
    return sum(
        entry["energyKwh"]
        for entry in db["telemetry"]
        if entry["deviceId"] == device_id and same_local_day(entry["timestamp"], ref)
    )


def last_power(db: dict, device_id: str) -> float:
    events = device_events(db, device_id)
    return events[-1]["powerW"] if events else 0.0


def as_device_response(db: dict, device: dict) -> dict:
    return {
        "id": device["id"],
        "name": device["name"],
        "room": device["room"],
        "type": device["type"],
        "active": device["active"],
        "energyKwhToday": round(energy_today(db, device["id"]), 4),
        "currentPowerWatts": round(last_power(db, device["id"]), 2),
    }


@app.get("/health")
def health() -> dict:
    return {"status": "ok", "service": "EnergyTrack Hardware API"}


@app.get("/api/devices")
def list_devices() -> list[dict]:
    db = load_db()
    return [as_device_response(db, device) for device in db["devices"]]


@app.post("/api/devices", status_code=201)
def create_device(payload: DeviceInput) -> dict:
    db = load_db()
    device_id = payload.id or str(uuid.uuid4())
    if any(device["id"] == device_id for device in db["devices"]):
        raise HTTPException(status_code=409, detail="Já existe um dispositivo com esse ID")

    device = {
        "id": device_id,
        "name": payload.name.strip(),
        "room": payload.room.strip(),
        "type": payload.type,
        "active": payload.active,
    }
    db["devices"].append(device)
    save_db(db)
    return as_device_response(db, device)


@app.put("/api/devices/{device_id}")
def update_device(device_id: str, payload: DeviceInput) -> dict:
    db = load_db()
    device = get_device_or_404(db, device_id)
    device.update(
        name=payload.name.strip(),
        room=payload.room.strip(),
        type=payload.type,
        active=payload.active,
    )
    save_db(db)
    return as_device_response(db, device)


@app.delete("/api/devices/{device_id}", status_code=204)
def delete_device(device_id: str) -> None:
    db = load_db()
    get_device_or_404(db, device_id)
    db["devices"] = [device for device in db["devices"] if device["id"] != device_id]
    db["telemetry"] = [entry for entry in db["telemetry"] if entry["deviceId"] != device_id]
    save_db(db)


@app.post("/api/telemetry", status_code=201)
def receive_telemetry(payload: TelemetryInput) -> dict:
    """Endpoint que o hardware (ESP32, STM32, Raspberry Pi etc.) chama periodicamente."""
    db = load_db()
    device = get_device_or_404(db, payload.deviceId)
    measured_at = payload.timestamp or utc_now()
    if measured_at.tzinfo is None:
        measured_at = measured_at.replace(tzinfo=timezone.utc)

    power_w = payload.powerW if payload.powerW is not None else payload.voltageV * payload.currentA
    previous_events = device_events(db, payload.deviceId)
    energy_kwh = 0.0
    if previous_events:
        previous_time = parse_time(previous_events[-1]["timestamp"])
        elapsed_seconds = max(0.0, (measured_at - previous_time).total_seconds())
        # Evita acumular um salto irreal caso o sensor fique desligado por muito tempo.
        elapsed_seconds = min(elapsed_seconds, 300.0)
        energy_kwh = (power_w * elapsed_seconds) / 3_600_000.0

    entry = {
        "id": str(uuid.uuid4()),
        "deviceId": payload.deviceId,
        "timestamp": measured_at.isoformat(),
        "voltageV": round(payload.voltageV, 2),
        "currentA": round(payload.currentA, 4),
        "powerW": round(power_w, 2),
        "energyKwh": energy_kwh,
    }
    db["telemetry"].append(entry)
    save_db(db)

    return {
        "received": True,
        "device": device["name"],
        "powerW": entry["powerW"],
        "energyKwhAdded": round(energy_kwh, 8),
    }


@app.get("/api/dashboard")
def dashboard() -> dict:
    db = load_db()
    now = utc_now()
    yesterday = now.replace(day=now.day)  # only reference; subtract below
    from datetime import timedelta
    yesterday = now - timedelta(days=1)

    today_kwh = sum(
        entry["energyKwh"] for entry in db["telemetry"] if same_local_day(entry["timestamp"], now)
    )
    yesterday_kwh = sum(
        entry["energyKwh"] for entry in db["telemetry"] if same_local_day(entry["timestamp"], yesterday)
    )
    month_kwh = sum(
        entry["energyKwh"]
        for entry in db["telemetry"]
        if parse_time(entry["timestamp"]).year == now.year and parse_time(entry["timestamp"]).month == now.month
    )

    goal_kwh = 200.0
    percent_vs_goal = ((month_kwh - goal_kwh) / goal_kwh) * 100.0

    month_names = [
        "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
        "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro",
    ]

    return {
        "monthLabel": f"{month_names[now.month - 1]} {now.year}",
        "monthlyConsumptionKwh": round(month_kwh, 4),
        "goalKwh": goal_kwh,
        "monthlyCostBrl": round(month_kwh * TARIFF_BRL_PER_KWH, 2),
        "percentVsGoal": round(percent_vs_goal, 2),
        "todayConsumptionKwh": round(today_kwh, 4),
        "yesterdayConsumptionKwh": round(yesterday_kwh, 4),
        "todayCostBrl": round(today_kwh * TARIFF_BRL_PER_KWH, 2),
    }


@app.get("/api/history")
def history() -> list[dict]:
    db = load_db()
    grouped: dict[str, list[dict]] = defaultdict(list)
    for entry in db["telemetry"]:
        grouped[parse_time(entry["timestamp"]).date().isoformat()].append(entry)

    records = []
    for day, entries in sorted(grouped.items(), reverse=True):
        total_kwh = sum(item["energyKwh"] for item in entries)
        max_power = max((item["powerW"] for item in entries), default=0.0)
        records.append(
            {
                "id": day,
                "date": datetime.fromisoformat(day).strftime("%d/%m/%Y"),
                "description": f"{len(entries)} leituras • pico de {max_power:.0f} W",
                "energyKwh": round(total_kwh, 4),
                "costBrl": round(total_kwh * TARIFF_BRL_PER_KWH, 2),
                "alert": max_power >= 1500.0,
            }
        )
    return records
