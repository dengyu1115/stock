package com.nature.func.mapper;

import android.database.Cursor;
import com.alibaba.fastjson.JSON;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.func.model.LineDef;
import com.nature.func.model.Strategy;

import java.util.List;
import java.util.function.Function;

public class StrategyMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS strategy ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " date TEXT, " +
            " json TEXT, " +
            " PRIMARY KEY (code))";
    private static final String COLUMN = "code, name, date, json";
    private static final Function<Cursor, Strategy> mapper = c -> {
        Strategy t = new Strategy();
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        t.setDate(BaseDB.getString(c, "date"));
        String json = BaseDB.getString(c, "json");
        if (json != null) t.setList(JSON.parseArray(json, LineDef.class));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public StrategyMapper() {
        baseDB.executeSql(TABLE);
    }


    public int merge(Strategy d) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO strategy(" + COLUMN + ") VALUES ")
                .append("(?, ?, ?, ?)", d.getCode(), d.getName(), d.getDate(),
                        d.getList() == null ? null : JSON.toJSONString(d.getList()));
        return baseDB.executeUpdate(param);
    }

    public int delete(String code) {
        SqlBuilder param = SqlBuilder.build().append("delete from strategy where code = ?", code);
        return baseDB.executeUpdate(param);
    }

    public List<Strategy> list() {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from strategy");
        return baseDB.list(param, mapper);
    }

    public Strategy findByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from strategy where code = ?", code);
        return baseDB.find(param, mapper);
    }
}
