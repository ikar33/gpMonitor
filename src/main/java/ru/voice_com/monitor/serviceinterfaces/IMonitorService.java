package ru.voice_com.monitor.serviceinterfaces;

import ru.voice_com.monitor.models.responses.CommonStatisticModel;

import java.time.LocalDateTime;

public interface IMonitorService {


    CommonStatisticModel callsDailyPlan();

    CommonStatisticModel unprocessedDialsWaitTime(Integer limitInSec, Integer periodInSec);

    CommonStatisticModel dialsAggregateStatistic(LocalDateTime localDateTime);

    CommonStatisticModel getDialsStatistic(LocalDateTime localDateTime);


}
