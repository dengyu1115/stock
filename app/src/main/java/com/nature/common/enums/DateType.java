package com.nature.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum DateType {

    WORKDAY("0", "工作日"),
    HOLIDAY("1", "节假日");

    private String code;
    private String name;

    private static final Map<String, String> CODE_NAME = Arrays.stream(values())
            .collect(Collectors.toMap(DateType::getCode, DateType::getName));

    public static String codeToName(String code) {
        return CODE_NAME.get(code);
    }
}
