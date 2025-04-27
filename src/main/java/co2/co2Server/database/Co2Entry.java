package co2.co2Server.database;

import java.sql.Timestamp;

public class Co2Entry {
    private int entryId, co2, temperature;
    private String sensorName;
    private Timestamp date;

    @Override
    public String toString() {
        return "Co2Entry{" +
                "entryId=" + entryId +
                ", co2=" + co2 +
                ", temperature=" + temperature +
                ", sensorName='" + sensorName + '\'' +
                ", date=" + date +
                '}';
    }

    public Co2Entry(int entryId, int co2, int temperatur, String sensorName, Timestamp date) {
        this.entryId = entryId;
        this.co2  = co2;
        this.temperature = temperatur;
        this.sensorName = sensorName;
        this.date = date;
    }

    public int getEntryId() {
        return entryId;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getSensorName() {
        return sensorName;
    }

    public void setSensorName(String sensorName) {
        this.sensorName = sensorName;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }
}
