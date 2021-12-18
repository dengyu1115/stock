package com.nature.stock.common.mapper;


import android.database.Cursor;
import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.db.SqlParam;
import com.nature.stock.common.model.TaskInfo;

import java.util.List;
import java.util.function.Function;

/**
 * 任务信息
 * @author nature
 * @version 1.0.0
 * @since 2020/2/23 21:42
 */
public class TaskInfoMapper {

    private static final String SQL_QUERY = "select code, name, type, start_time, end_time, status from task_info";
    private static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS task_info (" +
            " code TEXT NOT NULL," +
            " name TEXT NULL," +
            " type TEXT NOT NULL," +
            " start_time TEXT NOT NULL," +
            " end_time TEXT NOT NULL," +
            " status TEXT NULL," +
            " PRIMARY KEY (code, start_time)" +
            ")";
    private static final Function<Cursor, TaskInfo> resultMapper = c -> {
        TaskInfo t = new TaskInfo();
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        t.setType(BaseDB.getString(c, "type"));
        t.setStartTime(BaseDB.getString(c, "start_time"));
        t.setEndTime(BaseDB.getString(c, "end_time"));
        t.setStatus(BaseDB.getString(c, "status"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public TaskInfoMapper() {
        baseDB.executeSql(SQL_TABLE);
    }

    /**
     * 查询全部任务信息
     * @return list
     */
    public List<TaskInfo> list() {
        return baseDB.list(SqlParam.build().append(SQL_QUERY), resultMapper);
    }

    /**
     * 查询全部任务信息
     * @return list
     */
    public List<TaskInfo> listValid() {
        return baseDB.list(SqlParam.build().append(SQL_QUERY).append("where status = '1'"), resultMapper);
    }

    public int merge(TaskInfo d) {
        SqlParam param = SqlParam.build().append("replace into task_info(code, name, type, start_time, end_time, " +
                        "status) values(?, ?, ?, ?, ?, ?)", d.getCode(), d.getName(), d.getType(),
                d.getStartTime(), d.getEndTime(), d.getStatus());
        return baseDB.executeUpdate(param);
    }

    public int delete(String code, String startTime) {
        SqlParam param = SqlParam.build().append("delete from task_info where code = ? and start_time = ?",
                code, startTime);
        return baseDB.executeUpdate(param);
    }
}
