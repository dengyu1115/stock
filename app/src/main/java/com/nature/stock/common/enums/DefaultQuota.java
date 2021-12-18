package com.nature.stock.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 默认的大盘指标
 * @author nature
 * @version 1.0.0
 * @since 2020/11/28 14:29
 */
public enum DefaultQuota {

    HS("000300", "沪深300"),
    SZ("000001", "上证综指"),
    SC("399001", "深证成指"),
    ZXB("399005", "中小板指"),
    CYB("399006", "创业板指");

    private final String code;
    private final String name;

    DefaultQuota(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(String code) {
        DefaultQuota[] values = values();
        for (DefaultQuota group : values) {
            if (group.code.equals(code)) return group.name;
        }
        return null;
    }

    public static List<String> codes() {
        return Arrays.stream(values()).map(DefaultQuota::getCode).collect(Collectors.toList());
    }

    public String getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }
}
