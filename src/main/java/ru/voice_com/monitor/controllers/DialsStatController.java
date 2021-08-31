package ru.voice_com.monitor.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.voice_com.monitor.cache.aspect.UseReportArchive;
import ru.voice_com.monitor.models.responses.CommonStatisticModel;
import ru.voice_com.monitor.serviceinterfaces.IMonitorService;
import ru.voice_com.monitor.utils.ReportsProperties;

import java.time.LocalDateTime;
import java.util.Map;


@RestController
@RequestMapping(value = "/dialstat")
public class DialsStatController {

    private final IMonitorService monitorService;
    private final ReportsProperties reportsProperties;

    public DialsStatController(IMonitorService monitorService, ReportsProperties reportsProperties) {
        this.monitorService = monitorService;
        this.reportsProperties = reportsProperties;
    }

    private static final Logger logger = LoggerFactory.getLogger(DialsStatController.class);

    @UseReportArchive(reportName = "dials_statistic.report_name", reportDateField = "report_date")
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/get-calls-stat", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getCallsCommonStatistic(@RequestBody Map<String, String> request) {
        LocalDateTime dateFrom = LocalDateTime.parse(request.get("report_date"), reportsProperties.getRequestsDatetimeFormatter());
        logger.info("Dials statistic request");
        CommonStatisticModel response_body = monitorService.getDialsStatistic(dateFrom);
        return ResponseEntity.ok(response_body);
    }

    @UseReportArchive(reportName = "dials_aggregate_statistic.report_name", reportDateField = "report_date")
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/get-dial-agg-stat", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getDialsAggregateStatistic(@RequestBody Map<String, String> request) {
        LocalDateTime dateFrom = LocalDateTime.parse(request.get("report_date"), reportsProperties.getRequestsDatetimeFormatter());
        logger.info("Dials statistic request");
        CommonStatisticModel response_body = monitorService.dialsAggregateStatistic(dateFrom);
        return ResponseEntity.ok(response_body);
    }


    @CrossOrigin(origins = "*")
    @PostMapping(path = "/get-processing-wait-time", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getDialsProcessinWaitTime(@RequestBody Map<String, String> request) {
        logger.info("Dials statistic request");
        CommonStatisticModel response_body = monitorService.unprocessedDialsWaitTime(Integer.valueOf(request.get("limitInSec")), Integer.valueOf(request.get("periodInSec")));
        return ResponseEntity.ok(response_body);
    }

    @UseReportArchive(reportName = "calls_daily_plan.report_name", reportDateField = "report_date")
    @CrossOrigin(origins = "*")
    @PostMapping(path = "/get-calls-plan", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity getDialsAggregateStatistic() {
        logger.info("Dials statistic request");
        CommonStatisticModel response_body = monitorService.callsDailyPlan();
        return ResponseEntity.ok(response_body);
    }


}
