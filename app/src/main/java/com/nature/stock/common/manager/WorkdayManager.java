package com.nature.stock.common.manager;

import android.annotation.SuppressLint;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nature.stock.common.ioc.annotation.Injection;
import com.nature.stock.common.mapper.WorkdayMapper;
import com.nature.stock.common.model.Month;
import com.nature.stock.common.model.Workday;
import com.nature.stock.common.util.HttpUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * work day manager
 * @author nature
 * @version 1.0.0
 * @since 2019/12/14 20:51
 */
@SuppressLint("DefaultLocale")
public class WorkdayManager {

    /**
     * 日期格式
     */
    private static final String FORMAT_DATE = "yyyy-MM-dd";
    /**
     * 时间格式
     */
    private static final String FORMAT_TIME = "HH:mm:ss";
    /**
     * 查询是否工作日的接口
     */
    private static final String URL_HOLIDAY = "https://tool.bitefu.net/jiari/?d=%s";
    /**
     * 时间 09:00:00
     */
    private static final String START_TIME = "09:25:00";
    /**
     * 时间 15:30:00
     */
    private static final String END_TIME = "15:05:00";
    /**
     * 类型：节假日
     */
    private static final String TYPE_HOLIDAY = "1";
    /**
     * 类型：工作日
     */
    private static final String TYPE_WORKDAY = "0";

    private static final int YEAR_START = 10, YEAR_END = 22;

    @Injection
    private WorkdayMapper workdayMapper;

    /**
     * 交易或者非交易时间分别执行
     * @param yf  交易时间执行的逻辑
     * @param nf  非交易时间执行的逻辑
     * @param <R> 返回结果类型
     * @return R
     */
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

    /**
     * 交易或者非交易时间分别执行
     * @param yf  交易时间执行的逻辑
     * @param nf  非交易时间执行的逻辑
     * @param <R> 返回结果类型
     * @return R
     */
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

    /**
     * 获取最近交易日
     * @return string
     */
    public String getLatestWorkDay() {
        return workdayMapper.getLatestWorkDay(this.getToday());
    }

    /**
     * 获取下一工作日
     * @param date 指定日期
     * @return string
     */
    public String getNextWorkDay(String date) {
        return workdayMapper.getNextWorkDay(date);
    }

    /**
     * 获取上一工作日
     * @param date 指定日期
     * @return string
     */
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

    /**
     * 返回全部工作日
     * @param date 日期
     * @return list
     */
    public List<String> listWorkDays(String date) {
        return workdayMapper.listWorkDays(date);
    }

    /**
     * 返回全部工作日
     * @param date 日期
     * @return list
     */
    public List<String> list(String date) {
        return workdayMapper.list(date);
    }

    /**
     * 获取一年工作日数据
     * @param year year
     * @return list
     */
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

    /**
     * 从网络获取全年全部节假日
     * @param year year
     * @return 节假日集合
     */
    private List<String> getHolidaysFromNet(String year) {
        String s = HttpUtil.doGet(String.format(URL_HOLIDAY, year), lines -> lines.collect(Collectors.toList()).get(0));
        JSONObject map = JSON.parseObject(s).getJSONObject(year);
        List<String> holidays = new LinkedList<>();
        for (String date : map.keySet()) {
            holidays.add(String.format("%s-%s-%s", year, date.substring(0, 2), date.substring(2, 4)));
        }
        return holidays;
    }

    /**
     * 初始化一年全部日期数据
     * @param year year
     * @return map
     */
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

    /**
     * 初始化周末数据
     * @param year year
     * @return list
     */
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

    /**
     * 获取今天值
     * @return string
     */
    public String getToday() {
        return DateFormatUtils.format(new Date(), FORMAT_DATE);
    }

    /**
     * 获取昨天值
     * @return string
     */
    public String getYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return DateFormatUtils.format(calendar.getTime(), FORMAT_DATE);
    }

    /**
     * 获取当前时间
     * @return string
     */
    public String getNowTime() {
        return DateFormatUtils.format(new Date(), FORMAT_TIME);
    }

    /**
     * 判断今日是否交易日
     * @return boolean
     */
    public boolean isTodayWorkDay() {
        String today = this.getToday();
        return today.equals(workdayMapper.getLatestWorkDay(today));
    }

    /**
     * 判断昨日是否交易日
     * @return boolean
     */
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
