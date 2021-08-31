package ru.voice_com.monitor.repositories.dialer;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface DialerRepository {

    @SelectProvider(method = "unprocessedDialsWaitTime", type = StatementProvider.class)
    List<Map<String, Object>> unprocessedDialsWaitTime(@Param("limit_seconds") Integer limitSeconds, @Param("interval_seconds") String lastNSeconds);

    @SelectProvider(method = "dialsAggregateStatistic", type = StatementProvider.class)
    List<Map<String, Object>> dialsAggregateStatistic(@Param("intervalInMinutes") Integer intervalInMinutes, @Param("reportDate") LocalDateTime reportDate);

    @SelectProvider(method = "callsStatisticQuery", type = StatementProvider.class)
    List<Map<String, Object>> getCallsStatistic(@Param("intervalInMinutes") Integer intervalInMinutes, @Param("reportDate") LocalDateTime reportDate);

}
