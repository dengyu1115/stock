package com.nature.stock.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum RateDefType {

    STATIC("STATIC", "固定"),
    PERIOD("PERIOD", "区间");

    private final String code;
    private final String desc;

    private static final Map<String, String> CODE_DESC = Arrays.stream(values())
            .collect(Collectors.toMap(RateDefType::getCode, RateDefType::getDesc));

    private static final Map<String, RateDefType> CODE_VALUE = Arrays.stream(values())
            .collect(Collectors.toMap(RateDefType::getCode, i -> i));

    public static String codeToDesc(String code) {
        return CODE_DESC.get(code);
    }

    public static RateDefType codeToValue(String code) {
        return CODE_VALUE.get(code);
    }
}
