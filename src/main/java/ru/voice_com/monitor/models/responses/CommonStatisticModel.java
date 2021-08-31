package ru.voice_com.monitor.models.responses;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonStatisticModel {
    private List types = new ArrayList();
    private List<Map<String, Object>> values = new ArrayList<>();


    public void addFieldsLegend(String fieldName, String legend){
        types.add(new HashMap() {{
            put("fieldName", fieldName);
            put("legend", legend);
        }});
    }

    public List<Map<String, Object>> getTypes() {
        return types;
    }

    public List<Map<String, Object>> getValues() {
        return values;
    }

    public void setValues(List<Map<String, Object>> values) {
        this.values = values;
    }
}
