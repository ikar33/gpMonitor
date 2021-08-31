package ru.voice_com.monitor.repositories.aeroflot;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface AeroflotRepository {

    @SelectProvider(method = "callsDailyPlan", type = StatementProvider.class)
    List<Map<String, Object>> callsDailyPlan();
}
