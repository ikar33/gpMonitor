package ru.voice_com.monitor.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import ru.voice_com.monitor.utils.ReportsProperties;

import java.util.Map;

@RestController
@PropertySource("classpath:reports-variables.properties")
public class MainController {

    private final ReportsProperties reportsProperties;

    public MainController(ReportsProperties reportsProperties) {
        this.reportsProperties = reportsProperties;
    }

    @RequestMapping("/")
    public ModelAndView page(Map<String, Object> model){
        model.put("requests_date_format",reportsProperties.getPropValue("reports.requests.date_format", String.class));
        return new ModelAndView("html/main");
    }
}
