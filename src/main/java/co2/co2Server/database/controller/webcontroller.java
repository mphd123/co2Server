package co2.co2Server.database.controller;

import co2.co2Server.database.Co2Entry;
import co2.co2Server.database.SensorDataDB;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class webcontroller {

    @GetMapping("/co2all")
    public List<Co2Entry> getCo2Entries() throws Exception {
        try (SensorDataDB db = new SensorDataDB()) {
            return db.getEntries();
        }
    }

    @GetMapping("/co2")
    public String getlatestCo2() throws Exception {
        try (SensorDataDB db = new SensorDataDB()) {
            return db.getEntries().getLast().toString();
        }
        catch (NoSuchElementException e){
            return "there is no co2 element error code is = " + e;
        }
    }

    @GetMapping("/")
    public String main2() throws Exception {
        try (SensorDataDB db = new SensorDataDB()) {
            return "test";
        }
    }
}
