package ru.voice_com.monitor.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.voice_com.monitor.models.responses.CommonStatisticModel;
import ru.voice_com.monitor.serviceinterfaces.IMonitorService;
import ru.voice_com.monitor.utils.ReportsProperties;
import java.time.LocalDateTime;

@Component
@PropertySource("classpath:reports-variables.properties")
@EnableScheduling
public class ScheduledReports {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledReports.class);

    private final ReportsArchiveService reportsArchiveService;
    private final IMonitorService monitorService;
    private final ReportsProperties reportsProperties;

    public ScheduledReports(ReportsArchiveService reportsArchiveService, IMonitorService monitorService, ReportsProperties reportsProperties) {
        this.reportsArchiveService = reportsArchiveService;
        this.monitorService = monitorService;
        this.reportsProperties = reportsProperties;
    }

    @Scheduled(fixedDelayString = "${dials_statistic.fixedDelay}")
    public void initCommonCallsStatsReport() {
        String reportName = reportsProperties.getPropValue("dials_statistic.report_name", String.class) ;
        LocalDateTime today = reportsProperties.getCurrentStartDate();
        CommonStatisticModel response_body = monitorService.getDialsStatistic(today);
        reportsArchiveService.addReportsResponse(response_body, reportName, today);
        logger.info("Report {} initialized for date = {}", reportName, today.toString());
    }


    @Scheduled(fixedDelayString = "${dials_aggregate_statistic.fixedDelay}")
    public void initDialsAggregateStatisticReport() {
        String reportName = reportsProperties.getPropValue("dials_aggregate_statistic.report_name", String.class) ;
        LocalDateTime today = reportsProperties.getCurrentStartDate();
        CommonStatisticModel response_body = monitorService.dialsAggregateStatistic(today);
        reportsArchiveService.addReportsResponse(response_body, reportName, today);
        logger.info("Report {} initialized for date = {}", reportName, today.toString());
    }

    @Scheduled(fixedDelayString = "${calls_daily_plan.fixedDelay}")
    public void initCallsDailyPlanReport() {
        String reportName = reportsProperties.getPropValue("calls_daily_plan.report_name", String.class) ;
        LocalDateTime today = reportsProperties.getCurrentStartDate();
        CommonStatisticModel response_body = monitorService.callsDailyPlan();
        reportsArchiveService.addReportsResponse(response_body, reportName, today);
        logger.info("Report {} initialized for date = {}", reportName, today.toString());
    }


}
