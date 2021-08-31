package ru.voice_com.monitor.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.voice_com.monitor.models.responses.CommonStatisticModel;
import ru.voice_com.monitor.repositories.aeroflot.AeroflotRepository;
import ru.voice_com.monitor.repositories.dialer.DialerRepository;
import ru.voice_com.monitor.repositories.sabreixarchive.SabreIxArchiveRepository;

import ru.voice_com.monitor.serviceinterfaces.IMonitorService;
import ru.voice_com.monitor.utils.ReportsProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class MonitorService implements IMonitorService {

    private static final Logger logger = LoggerFactory.getLogger(MonitorService.class);

    private final AeroflotRepository aeroflotRepository;
    private final DialerRepository dialerRepository;
    private final SabreIxArchiveRepository sabreIxArchiveRepository;
    private final int defaultMaxMomentsNumber;
    private ReportsProperties reportsProperties;

    public MonitorService(AeroflotRepository aeroflotRepository,
                          DialerRepository dialerRepository,
                          SabreIxArchiveRepository sabreIxArchiveRepository,
                          ReportsProperties reportsProperties,
                          @Value("${dialjob.ids}") String dialjobIds,
                          @Value("${default.max.moments.number}") int defaultMaxMomentsNumber) {
        this.aeroflotRepository = aeroflotRepository;
        this.dialerRepository = dialerRepository;
        this.sabreIxArchiveRepository = sabreIxArchiveRepository;
        this.defaultMaxMomentsNumber = defaultMaxMomentsNumber;
        this.reportsProperties = reportsProperties;
    }

    /*
    Отчет плановое количество звонков с учетом успешных попыток
     */
    public CommonStatisticModel callsDailyPlan() {
        CommonStatisticModel commonStatisticModel = new CommonStatisticModel();
        List<Map<String, Object>> requestResult = this.aeroflotRepository.callsDailyPlan();
        commonStatisticModel.addFieldsLegend("higth_priority", "Задачи с высоким приоритетом");
        commonStatisticModel.addFieldsLegend("middle_priority", "Задачи с средним приоритетом");
        commonStatisticModel.setValues(requestResult);
        return commonStatisticModel;
    }


    public CommonStatisticModel unprocessedDialsWaitTime(Integer limitInSec, Integer periodInSec) {
        CommonStatisticModel commonStatisticModel = new CommonStatisticModel();
        commonStatisticModel.addFieldsLegend("max_wait_time", "Максимальное время ожидания обработки");
        commonStatisticModel.addFieldsLegend("remains", "");

        List<Map<String, Object>> requestResult = this.dialerRepository.unprocessedDialsWaitTime(limitInSec, String.format("%dsec", periodInSec));
        commonStatisticModel.setValues(requestResult);
        return commonStatisticModel;
    }

    /*
    Специальный отчет по статистике вызовов - аггригированные метрики
    *Максимальное время ожидания обработки
    *Среднее время звонка
     */
    public CommonStatisticModel dialsAggregateStatistic(LocalDateTime localDateTime) {
        CommonStatisticModel commonStatisticModel = new CommonStatisticModel();
        commonStatisticModel.addFieldsLegend("max_wait_time", "Максимальное время ожидания обработки");
        commonStatisticModel.addFieldsLegend("avg_dial_time", "Среднее время звонка");
        List<Map<String, Object>> requestResult = this.dialerRepository.dialsAggregateStatistic(reportsProperties.getPropValue("dials_aggregate_stat.interval", Integer.class), localDateTime);
        commonStatisticModel.setValues(requestResult);
        return commonStatisticModel;
    }

    /*
    Общая статистика вызовов за указанный день. Запрос возвращает данные с указанием временного шага.
    *Количество необработанных звонков
    *Количество звонков в обработке
    *Количество успешных звонков
    *Количество неуспешных звонков
     */
    public CommonStatisticModel getDialsStatistic(LocalDateTime localDateTime) {
        CommonStatisticModel commonStatisticModel = new CommonStatisticModel();
        commonStatisticModel.addFieldsLegend("calls_in_order", "Количество необработанных звонков!");
        commonStatisticModel.addFieldsLegend("calls_in_process", "Количество звонков в обработке");
        commonStatisticModel.addFieldsLegend("successfull_calls", "Количество успешных звонков");
        commonStatisticModel.addFieldsLegend("aborted_calls", "Количество неуспешных звонков");
        List<Map<String, Object>> requestResult = this.dialerRepository.getCallsStatistic(reportsProperties.getPropValue("dials_statistic.interval", Integer.class), localDateTime);
        commonStatisticModel.setValues(requestResult);
        return commonStatisticModel;
    }

}
