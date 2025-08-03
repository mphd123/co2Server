package co2.co2Server.database.controller;

import co2.co2Server.database.Co2Entry;
import co2.co2Server.database.SensorDataDB;
import org.jfree.chart.axis.DateAxis;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


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

    @GetMapping("/co2")
    public String addFoo(@RequestParam(name = "count") int count)  { 
        
        try {
            List<Co2Entry> list = db.getEntries();
            Collections.sort(list,(Co2Entry obj1, Co2Entry obj2) -> obj1.getDate().compareTo(obj2.getDate()));
            return list.subList(Math.max(list.size()-count,0), list.size()).toString();
        
            } catch (Exception e) {
            return "an error occured while processing the request with count = "+ count;
        }
}

    @GetMapping("/image")
    public ResponseEntity<byte[]> getImage() throws Exception {
        List<Co2Entry> entries = db.getEntries();

        TimeSeries series = new TimeSeries("CO₂ Measurements");

        for (Co2Entry entry : entries) {
            series.addOrUpdate(new Second(entry.getDate()), entry.getCo2());
        }

        TimeSeriesCollection dataset = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "CO₂ Measurements Over Time",
                "Timestamp",
                "CO₂ (ppm)",
                dataset
        );

        DateAxis axis = (DateAxis) chart.getXYPlot().getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd HH:mm"));

        byte[] imageBytes;
        try (var baos = new java.io.ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(baos, chart, 800, 600);
            imageBytes = baos.toByteArray();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);

        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }



    @GetMapping("/")
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
}
