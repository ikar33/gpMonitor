package ru.voice_com.monitor.services;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.voice_com.monitor.models.responses.CommonStatisticModel;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class ReportsArchiveService {

    private static Map<Integer, ResponseEntity> reportsArchive = new HashMap<>();

    public void addReportsResponse(Object commonStatisticModel, String reportName, LocalDateTime localDateTime) {
        Integer reportsHashCode = Objects.hash(reportName, localDateTime);
        reportsArchive.put(reportsHashCode, ResponseEntity.ok(commonStatisticModel));
    }

    public ResponseEntity getReport(String reportName, LocalDateTime localDateTime){
        Integer reportsHashCode = Objects.hash(reportName, localDateTime);
        if(reportsArchive.containsKey(reportsHashCode)){
            return  reportsArchive.get(reportsHashCode);
        }
        return ResponseEntity.ok(new CommonStatisticModel());
    }

    public void clearArchive(){
        reportsArchive.clear();
    }

}
