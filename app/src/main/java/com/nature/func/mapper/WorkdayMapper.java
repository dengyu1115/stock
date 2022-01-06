package com.nature.func.mapper;


import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.func.model.Workday;

import java.util.List;
import java.util.function.Function;

public class WorkdayMapper {

    private static final String DATE = "date";

    private static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS work_day (" +
            "date TEXT NOT NULL," +
            "type TEXT NOT NULL," +
            "PRIMARY KEY (date)" +
            ")";

    private final DB db = DB.create("nature/func.db");

    private final Function<Cursor, Workday> mapper = c -> {
        Workday d = new Workday();
        d.setDate(BaseDB.getString(c, "date"));
        d.setType(BaseDB.getString(c, "type"));
        return d;
    };

    public WorkdayMapper() {
        db.executeSql(SQL_TABLE);
    }

    public String getLatestWorkDay(String date) {
        String sql = "select date" +
                "       from work_day" +
                "      where date <= ?" +
                "        and type = '0'" +
                "      order by date desc" +
                "      limit 1";
        return db.find(sql, new String[]{date}, c -> BaseDB.getString(c, DATE));
    }

    public String getNextWorkDay(String date) {
        String sql = "select date" +
                "       from work_day" +
                "      where date > ?" +
                "        and type = '0'" +
                "      order by date" +
                "      limit 1";
        return db.find(sql, new String[]{date}, c -> BaseDB.getString(c, DATE));
    }

    public String getPreWorkDay(String date) {
        String sql = "select date" +
                "       from work_day" +
                "      where date < ?" +
                "        and type = '0'" +
                "      order by date desc" +
                "      limit 1";
        return db.find(sql, new String[]{date}, c -> BaseDB.getString(c, DATE));
    }

    public int batchMerge(List<Workday> workdays) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO work_day (date, type) VALUES")
                .foreach(workdays, null, null, ",",
                        (w, p) -> p.append("(?, ?)", w.getDate(), w.getType()));
        return db.executeUpdate(param);
    }

    public List<String> listWorkDays(String date) {
        SqlBuilder param = SqlBuilder.build().append("select date from work_day")
                .append("where type = '0' and date <= ? order by date desc", date);
        return db.list(param, c -> BaseDB.getString(c, DATE));
    }

    public int count(String year) {
        SqlBuilder param = SqlBuilder.build().append("select count(1) from work_day")
                .append("where date like ?", year.concat("%"));
        return db.find(param, c -> c.getInt(0));
    }

    public List<Workday> listByYear(String year) {
        SqlBuilder param = SqlBuilder.build().append("select date, type from work_day")
                .append("where date like ?", year.concat("%"));
        return db.list(param, mapper);
    }

    public List<String> list(String date) {
        SqlBuilder param = SqlBuilder.build().append("select date from work_day")
                .append("where date <= ? order by date desc", date);
        return db.list(param, c -> BaseDB.getString(c, DATE));
    }
}
