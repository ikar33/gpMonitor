package ru.voice_com.monitor.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
@PropertySource("classpath:reports-variables.properties")
public class ReportsProperties {

    @Autowired
    private Environment env;

    public <T> T getPropValue(String name, Class<T> variableType) {
        return env.getProperty(name, variableType);
    }


    public DateTimeFormatter getRequestsDatetimeFormatter() {
        return DateTimeFormatter.ofPattern(env.getProperty("reports.requests.date_time_format"));
    }

    public LocalDateTime getCurrentStartDate() {
        LocalDateTime today = (LocalDate.now()).atTime(0, 0, 0, 0);
        today.atOffset(ZoneOffset.UTC);
        return today;
    }
}
