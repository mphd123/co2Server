package co2.co2Server.database;

import java.util.Date;

public class Co2Entry {
    private int entryId, co2, temperature;
    private String sensorName;
    private Date date;

    public Co2Entry(int entryId, int co2, int temperatur, String sensorName, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }
}
