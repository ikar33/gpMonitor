package ru.voice_com.monitor.cache;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.voice_com.monitor.cache.aspect.UseReportArchive;
import ru.voice_com.monitor.services.ReportsArchiveService;
import ru.voice_com.monitor.utils.ReportsProperties;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;

@Aspect
@Component
public class ReportsCacheAspect {
    private static final Logger logger = LoggerFactory.getLogger(ReportsCacheAspect.class);

    private final ReportsArchiveService reportsArchiveService;
    private final ReportsProperties reportsProperties;

    public ReportsCacheAspect(ReportsArchiveService reportsArchiveService, ReportsProperties reportsProperties) {
        this.reportsArchiveService = reportsArchiveService;
        this.reportsProperties = reportsProperties;
    }

    @Pointcut("@annotation(ru.voice_com.monitor.cache.aspect.UseReportArchive) && args(request,..)")
    public void callHandleServletRequest(Map<String, String> request) {
    }

    /*
    If requests for current date request, then get data from cache archive, otherwise pass request further
     */
    @Around(value = "callHandleServletRequest(request)")
    public Object aroundReportRequest(ProceedingJoinPoint joinPoint, Map<String, String> request) throws Throwable {
        String reportName = reportsProperties.getPropValue(getReportsName(joinPoint), String.class) ;
        LocalDateTime reportDate = getReportsDateTime(joinPoint, request);
        logger.info("Requesting method: {} for report date = ", reportName, reportDate.toString());
        if(reportDate.equals(reportsProperties.getCurrentStartDate())) {
            return reportsArchiveService.getReport(reportName, reportDate);
        }else{
            return joinPoint.proceed();
        }
    }

    private UseReportArchive getAnnotation(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.getAnnotation(UseReportArchive.class);
    }

    private String getReportsName(JoinPoint joinPoint) {
        return getAnnotation(joinPoint).reportName();
    }

    private LocalDateTime getReportsDateTime(JoinPoint joinPoint, Map<String, String> request) {
        String reportsDateTimeField = getAnnotation(joinPoint).reportDateField();
        String reportsDateTimeStr = request.get(reportsDateTimeField);
        if (reportsDateTimeStr != null && reportsDateTimeStr.length() > 0) {
            return LocalDateTime.parse(request.get(reportsDateTimeField), reportsProperties.getRequestsDatetimeFormatter());
        } else {
            return reportsProperties.getCurrentStartDate();
        }
    }
}
