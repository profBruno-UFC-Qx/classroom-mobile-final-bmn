"""Simulador temporário da telemetria do EnergyTrack.

Quando o hardware real estiver pronto, este arquivo pode ser substituído
pelo envio HTTP feito pelo ESP32, STM32, Raspberry Pi ou outro dispositivo.
"""

from __future__ import annotations

import random
import time

import requests

API_URL = "http://127.0.0.1:8000"

DEVICES = [
    {
        "id": "sensor-ar-sala",
        "name": "Ar-condicionado",
        "room": "Sala",
        "type": "AIR_CONDITIONER",
        "base_power": 900.0,
    },
    {
        "id": "sensor-geladeira",
        "name": "Geladeira",
        "room": "Cozinha",
        "type": "REFRIGERATOR",
        "base_power": 140.0,
    },
    {
        "id": "sensor-lavadora",
        "name": "Máquina de lavar",
        "room": "Lavanderia",
        "type": "WASHING_MACHINE",
        "base_power": 500.0,
    },
]


def register_device(device: dict) -> bool:
    """Cria novamente o dispositivo caso ele tenha sido removido pelo app."""
    try:
        response = requests.post(
            f"{API_URL}/api/devices",
            json={
                "id": device["id"],
                "name": device["name"],
                "room": device["room"],
                "type": device["type"],
                "active": True,
            },
            timeout=5,
        )

        if response.status_code == 201:
            print(f"Dispositivo criado novamente: {device['name']}")
            return True

        if response.status_code == 409:
            return True

        print(
            f"Não foi possível registrar {device['name']}: "
            f"{response.status_code} - {response.text}"
        )
        return False

    except requests.RequestException as error:
        print(f"Erro ao registrar {device['name']}: {error}")
        return False


def ensure_devices() -> None:
    """Garante que os sensores do simulador existam na API."""
    for device in DEVICES:
        register_device(device)


def send_telemetry(device: dict, power_w: float) -> bool:
    """Envia a medição e recria o dispositivo caso ele tenha sido removido."""
    payload = {
        "deviceId": device["id"],
        "voltageV": 220.0,
        "currentA": round(power_w / 220.0, 4),
        "powerW": round(power_w, 2),
    }

    try:
        response = requests.post(
            f"{API_URL}/api/telemetry",
            json=payload,
            timeout=5,
        )

        if response.status_code == 201:
            print(f"{device['name']}: {power_w:.1f} W")
            return True

        if response.status_code == 404:
            print(
                f"{device['name']} foi removido pelo app. "
                "Criando novamente para continuar a telemetria..."
            )

            if register_device(device):
                retry = requests.post(
                    f"{API_URL}/api/telemetry",
                    json=payload,
                    timeout=5,
                )

                if retry.status_code == 201:
                    print(f"{device['name']}: {power_w:.1f} W")
                    return True

                print(
                    f"Falha ao reenviar {device['name']}: "
                    f"{retry.status_code} - {retry.text}"
                )
                return False

        print(
            f"Falha ao enviar medição de {device['name']}: "
            f"{response.status_code} - {response.text}"
        )
        return False

    except requests.RequestException as error:
        print(f"Erro de conexão ao enviar {device['name']}: {error}")
        return False


def send_measurements() -> None:
    while True:
        for device in DEVICES:
            power_w = max(
                0.0,
                device["base_power"] + random.uniform(-35.0, 35.0),
            )

            send_telemetry(device, power_w)

        time.sleep(10)


if __name__ == "__main__":
    print("Iniciando simulador de telemetria...")
    ensure_devices()
    send_measurements()