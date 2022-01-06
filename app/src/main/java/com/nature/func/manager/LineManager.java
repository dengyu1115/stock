package com.nature.func.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.func.mapper.LineMapper;
import com.nature.func.model.Line;
import com.nature.func.model.LineDef;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * 折线
 * @author nature
 * @version 1.0.0
 * @since 2020/8/30 22:40
 */
public class LineManager {

    @Injection
    private LineMapper lineMapper;


    public List<Line> list(String sql) {
        return lineMapper.list(sql);
    }

    public void calculate(List<Line> list) {
        if (list == null || list.isEmpty()) return;
        Line first = list.get(0);
        Line pre = first;
        for (Line line : list) {
            line.setRate((line.getPrice() - pre.getPrice()) / pre.getPrice());
            line.setRateTotal((line.getPrice() - first.getPrice()) / first.getPrice());
            pre = line;
        }
    }

    public void format(List<LineDef> list, String dateStart) {
        TreeSet<String> dates = new TreeSet<>();
        for (LineDef d : list) {
            List<Line> lines = d.getList();
            dates.add(lines.get(0).getDate());
        }
        if (dates.isEmpty()) return;
        final String start = StringUtils.isNotBlank(dateStart) && dates.last().compareTo(dateStart) < 0 ?
                dateStart : dates.last();
        for (LineDef d : list) {
            List<Line> lines = d.getList();
            d.setList(lines.stream().filter(i -> i.getDate().compareTo(start) >= 0).collect(Collectors.toList()));
        }
    }
}
