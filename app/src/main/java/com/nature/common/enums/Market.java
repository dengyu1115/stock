package com.nature.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum Market {

    SZ("0", "深圳"),
    SH("1", "上海"),
    BJ("2", "北京");

    private String code;
    private String name;

    private static final Map<String, String> codeNames = Arrays.stream(values())
            .collect(Collectors.toMap(Market::getCode, Market::getName));

    public static String codeToName(String code) {
        return codeNames.get(code);
    }
}
