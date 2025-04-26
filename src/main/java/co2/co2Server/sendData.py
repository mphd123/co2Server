import requests
import datetime
import time
import mh_z19

# URL of your server
url = "http://localhost:8080/addEntry"

def send_sensor_data():
    sensor_data = mh_z19.read_all()
    if not sensor_data:
        print(f"[{datetime.datetime.now()}] Failed to read from sensor.")
        return

    payload = {
        "co2": sensor_data.get("co2", 0),
        "temperature": sensor_data.get("temperature", 0),
        "sensorName": "sensor_01",
        "date": datetime.datetime.now().strftime("%Y-%m-%d")
    }

    try:
        response = requests.post(url, json=payload)
        print(f"[{datetime.datetime.now()}] Sent data: {payload}")
        print(f"Status: {response.status_code}, Response: {response.text}")
    except Exception as e:
        print(f"[{datetime.datetime.now()}] Failed to send data:", e)

if __name__ == "__main__":
    while True:
        send_sensor_data()
        time.sleep(3600)
