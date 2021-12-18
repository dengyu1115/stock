package com.nature.stock.common.mapper;


import android.database.Cursor;
import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.db.SqlParam;
import com.nature.stock.common.model.TaskRecord;

import java.util.List;
import java.util.function.Function;

/**
 * 任务记录
 * @author nature
 * @version 1.0.0
 * @since 2020/2/23 21:42
 */
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

    /**
     * 查询执行完成或者正执行任务数量
     * @param code      code
     * @param date      date
     * @param timeStart timeStart
     * @param timeEnd   timeEnd
     * @return int
     */
    public int countExecute(String code, String date, String timeStart, String timeEnd) {
        return baseDB.find(SqlParam.build().append(SQL_COUNT, code, date, timeStart, timeEnd), countMapper);
    }

    /**
     * 查询任务记录
     * @param date date
     * @return list
     */
    public List<TaskRecord> list(String date) {
        SqlParam sql = SqlParam.build().append("select code, date, start_time, end_time, status, exception")
                .append("from task_record");
        if (date != null) sql.append("where date = ?", date);
        sql.append("order by date desc, start_time");
        return baseDB.list(sql, resultMapper);
    }

    public int merge(TaskRecord d) {
        SqlParam sql = SqlParam.build().append("replace into task_record(code, date, start_time, end_time, status")
                .append(", exception) values(?, ?, ?, ?, ?, ?)", d.getCode(), d.getDate(), d.getStartTime(),
                        d.getEndTime(), d.getStatus(), d.getException());
        return baseDB.executeUpdate(sql);
    }

}
