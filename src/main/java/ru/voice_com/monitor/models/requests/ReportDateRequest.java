package ru.voice_com.monitor.models.requests;

import java.time.LocalDateTime;

public class ReportDateRequest {

    private LocalDateTime from;

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }


}
