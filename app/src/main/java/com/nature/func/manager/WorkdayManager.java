package com.nature.func.manager;

import android.annotation.SuppressLint;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.HttpUtil;
import com.nature.func.mapper.WorkdayMapper;
import com.nature.func.model.Month;
import com.nature.func.model.Workday;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressLint("DefaultLocale")
public class WorkdayManager {

    private static final String FORMAT_DATE = "yyyy-MM-dd";

    private static final String FORMAT_TIME = "HH:mm:ss";

    private static final String URL_HOLIDAY = "https://tool.bitefu.net/jiari/?d=%s";

    private static final String START_TIME = "09:25:00";

    private static final String END_TIME = "15:05:00";

    private static final String TYPE_HOLIDAY = "1";

    private static final String TYPE_WORKDAY = "0";

    @Injection
    private WorkdayMapper workdayMapper;

    public <R> R doInTradeTimeOrNot(Function<String, R> yf, Function<String, R> nf) {
        String today = this.getToday();
        String latestWorkDay = workdayMapper.getLatestWorkDay(today);
        if (today.equals(latestWorkDay)) {
            String nowTime = this.getNowTime();
            if (nowTime.compareTo(START_TIME) < 0) { // 交易日交易开始前
                return nf.apply(this.getPreWorkDay(today));
            } else if (nowTime.compareTo(END_TIME) > 0) { // 交易日交易结束
                return nf.apply(today);
            } else { // 交易日交易期间
                return yf.apply(today);
            }
        } else { // 非交易日
            return nf.apply(latestWorkDay);
        }
    }

    public <R> R doInTradeTimeOrNot(Supplier<R> yf, Supplier<R> nf) {
        String today = this.getToday();
        String latestWorkDay = workdayMapper.getLatestWorkDay(today);
        String nowTime;
        if (today.equals(latestWorkDay)
                && (nowTime = this.getNowTime()).compareTo(START_TIME) > 0 && nowTime.compareTo(END_TIME) < 0) {
            return yf.get();
        }
        return nf.get();
    }

    public String getLatestWorkDay() {
        return workdayMapper.getLatestWorkDay(this.getToday());
    }

    public String getNextWorkDay(String date) {
        return workdayMapper.getNextWorkDay(date);
    }

    public String getPreWorkDay(String date) {
        return workdayMapper.getPreWorkDay(date);
    }

    public int reloadAll(String year) {
        return workdayMapper.batchMerge(this.getYearWorkDays(year));
    }

    public int loadLatest(String year) {
        int exists = workdayMapper.count(year);
        if (exists > 0) {
            return exists;
        }
        return workdayMapper.batchMerge(this.getYearWorkDays(year));
    }

    public List<String> listWorkDays(String date) {
        return workdayMapper.listWorkDays(date);
    }

    public List<String> list(String date) {
        return workdayMapper.list(date);
    }

    private List<Workday> getYearWorkDays(String year) {
        Map<String, Workday> workDays = this.initYearDays(year);    // 获取全年工作日
        List<String> holidays = this.getHolidaysFromNet(year);      // 获取全年节假日
        List<String> weekends = this.initWeekends(year);            // 获取全年周末
        Set<String> days = new TreeSet<>(holidays);
        days.addAll(weekends);
        return workDays.entrySet().parallelStream().map(entry -> {  // 假日标记
            if (days.contains(entry.getKey())) entry.getValue().setType(TYPE_HOLIDAY);
            return entry.getValue();
        }).sorted(Comparator.comparing(Workday::getDate)).collect(Collectors.toList());
    }

    private List<String> getHolidaysFromNet(String year) {
        String s = HttpUtil.doGet(String.format(URL_HOLIDAY, year), lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject map = JSON.parseObject(s).getJSONObject(year);
        List<String> holidays = new LinkedList<>();
        for (String date : map.keySet()) {
            holidays.add(String.format("%s-%s-%s", year, date.substring(0, 2), date.substring(2, 4)));
        }
        return holidays;
    }

    private Map<String, Workday> initYearDays(String year) {
        Map<String, Workday> workDays = new HashMap<>();
        try {
            Date date = DateUtils.parseDate(year, "yyyy");  // 年初第一天
            int i = 0;
            while (true) {
                Workday workDay = new Workday();
                String day = DateFormatUtils.format(DateUtils.addDays(date, i++), FORMAT_DATE);
                workDay.setDate(day);
                workDay.setType(TYPE_WORKDAY);
                workDays.put(day, workDay);
                if (day.endsWith("12-31")) break;   // 生成至年底截止
            }
        } catch (ParseException e) {// ignore
        }
        return workDays;
    }

    private List<String> initWeekends(String year) {
        List<String> days = new ArrayList<>();
        try {
            Date date = DateUtils.parseDate(year, "yyyy");
            int i = 0;
            Date nextYear = DateUtils.addYears(date, 1);
            while (true) {
                Date d = DateUtils.addDays(date, i++);
                Calendar calendar = DateUtils.toCalendar(d);
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                if (d.after(nextYear)) break; // 第二次处理当年第一天说明已经跨年
                if (dayOfWeek == 1 || dayOfWeek == 7) days.add(DateFormatUtils.format(d, FORMAT_DATE));
            }
        } catch (ParseException e) {// ignore
        }
        return days;
    }

    public String getToday() {
        return DateFormatUtils.format(new Date(), FORMAT_DATE);
    }

    public String getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DateFormatUtils.format(calendar.getTime(), FORMAT_DATE);
    }

    public String getNowTime() {
        return DateFormatUtils.format(new Date(), FORMAT_TIME);
    }

    public boolean isTodayWorkDay() {
        String today = this.getToday();
        return today.equals(workdayMapper.getLatestWorkDay(today));
    }

    public boolean isYesterdayWorkDay() {
        String yesterday = this.getYesterday();
        return yesterday.equals(workdayMapper.getLatestWorkDay(yesterday));
    }

    public List<Month> listYearMonths(String year) {
        List<Workday> workdays = workdayMapper.listByYear(year);
        Map<String, List<Workday>> map = workdays.stream()
                .collect(Collectors.groupingBy(i -> i.getDate().substring(0, 7)));
        List<Month> results = new ArrayList<>();
        map.keySet().stream().sorted().forEach(i -> {
            List<Workday> list = map.get(i);
            if (list != null) {
                Month month = new Month();
                month.setMonth(i);
                for (Workday w : list) {
                    month.setDateType(w.getDate(), w.getType());
                }
                results.add(month);
            }
        });
        return results;
    }

}
