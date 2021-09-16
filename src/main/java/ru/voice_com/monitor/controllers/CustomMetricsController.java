package ru.voice_com.monitor.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.voice_com.monitor.cache.aspect.UseReportArchive;
import ru.voice_com.monitor.services.CustomMetricsService;

import java.util.Map;

@RestController
@RequestMapping(value = "/custom_metrics")
public class CustomMetricsController {
    private static final Logger logger = LoggerFactory.getLogger(CustomMetricsController.class);

    private final CustomMetricsService customMetricsService;

    public CustomMetricsController(CustomMetricsService customMetricsService) {
        this.customMetricsService = customMetricsService;
    }


    @UseReportArchive(reportName = "sabre_pl.report_name")
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/get-sabre-log")
    ResponseEntity getSabreLog() {
        logger.info("Dials statistic request");
        //return customMetricsService.getSabrePlLog();
        return ResponseEntity.ok(customMetricsService.getSabrePlLog());
    }
}
