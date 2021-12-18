package com.nature.stock.common.util;

import com.nature.stock.common.constant.Constant;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;

public class CommonUtil {

    public static Date parseDate(String date, String format) {
        try {
            return DateUtils.parseDate(date, format);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String addDays(String date, int days) {
        Date parseDate = parseDate(date, Constant.FORMAT_DATE);
        Date resultDate = DateUtils.addDays(parseDate, days);
        return DateFormatUtils.format(resultDate, Constant.FORMAT_DATE);
    }

    public static String addWeeks(String date, int weeks) {
        Date parseDate = parseDate(date, Constant.FORMAT_DATE);
        Date resultDate = DateUtils.addWeeks(parseDate, weeks);
        return DateFormatUtils.format(resultDate, Constant.FORMAT_DATE);
    }

    public static String addMonths(String date, int months) {
        Date parseDate = parseDate(date, Constant.FORMAT_DATE);
        Date resultDate = DateUtils.addMonths(parseDate, months);
        return DateFormatUtils.format(resultDate, Constant.FORMAT_DATE);
    }

    public static String addYears(String date, int years) {
        Date parseDate = parseDate(date, Constant.FORMAT_DATE);
        Date resultDate = DateUtils.addYears(parseDate, years);
        return DateFormatUtils.format(resultDate, Constant.FORMAT_DATE);
    }

    public static String formatDate(String date) {
        return String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6, 8));
    }

    public static String formatDate(Date date) {
        return DateFormatUtils.format(date, Constant.FORMAT_DATE);
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> nullsLast(
            Function<? super T, ? extends U> keyExtractor) {
        Objects.requireNonNull(keyExtractor);
        return (Comparator<T> & Serializable)
                (c1, c2) -> {
                    U u1 = keyExtractor.apply(c1), u2 = keyExtractor.apply(c2);
                    if (u1 != null && u2 != null) return u1.compareTo(u2);
                    else if (u1 == null && u2 != null) return -1;
                    else if (u1 != null) return 1;
                    else return 0;
                };
    }
}