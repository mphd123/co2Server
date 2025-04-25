package co2.co2Server.database.controller;

import co2.co2Server.database.Co2Entry;
import co2.co2Server.database.SensorDataDB;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Date;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class webcontroller {

    private SensorDataDB db;

    public webcontroller(SensorDataDB db) {
        this.db = db;
    }


    @GetMapping("/co2all")
    public String getCo2Entries() throws Exception {
        try {
            List<Co2Entry> list = db.getEntries();
            if (list.isEmpty()) return "empty list";
            return list.toString();
        }
        catch (Exception e){
            return "got an exception for getting all the co2 entries error code is = " + e;
        }
    }

    @GetMapping("/co2")
    public String getlatestCo2() throws Exception {
        try  {
            return db.getEntries().getLast().toString();
        }
        catch (NoSuchElementException e){
            return "there is no co2 element error code is = " + e;
        }
    }

    @PostMapping(path = "/addEntry")
    public ResponseEntity<Co2Entry> addEntry(@RequestBody Co2Entry entry) {
        try {
            db.addEntry(entry.getCo2(), entry.getTemperature(), entry.getSensorName(), entry.getDate());
        } catch (Exception e) {
            return ResponseEntity.unprocessableEntity().body(entry);
        }

        return ResponseEntity.accepted().body(entry);
    }

    @GetMapping("/")
    public String main2() throws Exception {
            return "test";
    }
}
