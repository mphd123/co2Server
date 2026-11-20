package co2.co2Server.database.controller;

import co2.co2Server.PlotPythonServer;
import co2.co2Server.database.Co2Entry;
import co2.co2Server.database.SensorDataDB;

import grpcJava.ImageClient;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@RestController
public class Webcontroller {

    private SensorDataDB db;
    private static Process pythonServerProcess = null;


    public Webcontroller(SensorDataDB db) {
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
    public String getNLast(@RequestParam(name = "count", defaultValue = "1") int count) {
        try {
            if (count == 1)  return  db.getMostRecentEntry().toString();
            List<Co2Entry> list = db.getEntries();
            return list.subList(Math.max(list.size() - count, 0), list.size()).toString();
        } catch (Exception e) {
            return "An error occurred while processing the request with count = " + count;
        }
    }

    public static final DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage(@RequestParam(name = "dateFrom", defaultValue = "") String dateFrom,@RequestParam(name = "dateTo", defaultValue = "") String dateTo) throws Exception {
        Timestamp timeFrom;
        Timestamp timeTo;
        try {
            timeFrom = (!dateFrom.isEmpty()) ?  new Timestamp(df.parse(dateFrom).getTime()) : new Timestamp(0);
            timeTo = (!dateTo.isEmpty()) ?  new Timestamp(df.parse(dateTo).getTime()) : new Timestamp(Long.MAX_VALUE);
        } catch (ParseException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            String errorMessage = "got error parsing the given dates the Format should be " + df.toString();
            return new ResponseEntity<>(errorMessage.getBytes(), headers, HttpStatus.BAD_REQUEST);
        }
        List<Co2Entry> entries = db.getEntries().stream().filter(co2Entry -> {

                if(!dateFrom.isEmpty() && co2Entry.getDate().before(timeFrom)) return  false;
                if(!dateTo.isEmpty() && co2Entry.getDate().after(timeTo)) return  false;
                return true;

        }).toList();
        long[] x = new long[entries.size()];
        double[] y = new double[entries.size()];
        for (int i = 0; i < entries.size(); i++) {
            Co2Entry entry = entries.get(i);
            x[i] = entry.getDate().getTime();
            y[i] = entry.getCo2();
        }
        byte[] imageBytes = ImageClient.getImage("localhost", PlotPythonServer.port, x, y);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/test")
    public String main2() throws Exception {
            return "test";
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

    @GetMapping("/api/summary")
    public Map<String, Object> summary() throws Exception {
        List<Co2Entry> all = db.getEntries();
        Map<String, Object> result = new LinkedHashMap<>();
        if (all.isEmpty()) return result;

        Co2Entry latest = Collections.max(all, Comparator.comparingLong(e -> e.getDate().getTime()));
        long now = System.currentTimeMillis();
        result.put("latest", latest);
        result.put("lastHour", average(all, now - 3_600_000L));
        result.put("lastDay", average(all, now - 86_400_000L));
        return result;
    }

    private Map<String, Object> average(List<Co2Entry> all, long since) {
        List<Co2Entry> part = all.stream()
                .filter(e -> e.getDate().getTime() >= since)
                .toList();
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("count", part.size());
        m.put("co2", part.isEmpty() ? null : part.stream().mapToDouble(e -> e.getCo2()).average().getAsDouble());
        m.put("temperature", part.isEmpty() ? null : part.stream().mapToDouble(e -> e.getTemperature()).average().getAsDouble());
        return m;
    }
}
