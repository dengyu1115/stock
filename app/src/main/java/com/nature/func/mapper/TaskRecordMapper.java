package com.nature.func.mapper;


import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.func.model.TaskRecord;

import java.util.List;
import java.util.function.Function;

public class TaskRecordMapper {

    public static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS task_record ( " +
            " code TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " start_time TEXT NULL, " +
            " end_time TEXT NULL, " +
            " status TEXT NOT NULL, " +
            " exception TEXT NULL, " +
            " PRIMARY KEY (code, date, start_time) " +
            ")";
    public static final String SQL_COUNT = "" +
            "select count(1) cnt from task_record " +
            "where code = ? " +
            "and date = ? " +
            "and start_time >= ? " +
            "and start_time < ? " +
            "and status in ('0', '1')";
    private static final Function<Cursor, TaskRecord> resultMapper = c -> {
        TaskRecord t = new TaskRecord();
        t.setCode(BaseDB.getString(c, "code"));
        t.setDate(BaseDB.getString(c, "date"));
        t.setStartTime(BaseDB.getString(c, "start_time"));
        t.setEndTime(BaseDB.getString(c, "end_time"));
        t.setStatus(BaseDB.getString(c, "status"));
        t.setException(BaseDB.getString(c, "exception"));
        return t;
    };
    private static final Function<Cursor, Integer> countMapper = c -> BaseDB.getInt(c, "cnt");
    private final BaseDB baseDB = BaseDB.create();

    public TaskRecordMapper() {
        baseDB.executeSql(SQL_TABLE);
    }

    public int countExecute(String code, String date, String timeStart, String timeEnd) {
        return baseDB.find(SqlBuilder.build().append(SQL_COUNT, code, date, timeStart, timeEnd), countMapper);
    }

    public List<TaskRecord> list(String date) {
        SqlBuilder sql = SqlBuilder.build().append("select code, date, start_time, end_time, status, exception")
                .append("from task_record");
        if (date != null) sql.append("where date = ?", date);
        sql.append("order by date desc, start_time");
        return baseDB.list(sql, resultMapper);
    }

    public int merge(TaskRecord d) {
        SqlBuilder sql = SqlBuilder.build().append("replace into task_record(code, date, start_time, end_time, status")
                .append(", exception) values(?, ?, ?, ?, ?, ?)", d.getCode(), d.getDate(), d.getStartTime(),
                        d.getEndTime(), d.getStatus(), d.getException());
        return baseDB.executeUpdate(sql);
    }

}
