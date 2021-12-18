package com.nature.stock.common.mapper;

import android.database.Cursor;
import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.db.SqlParam;
import com.nature.stock.common.model.Definition;

import java.util.List;
import java.util.function.Function;

/**
 * 策略
 * @author nature
 * @version 1.0.0
 * @since 2020/9/19 12:06
 */
public class DefinitionMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS definition ( " +
            " code TEXT NOT NULL, " +
            " title TEXT NOT NULL, " +
            " type TEXT NOT NULL, " +
            " desc TEXT, " +
            " json TEXT, " +
            " PRIMARY KEY (type, code))";
    private static final String COLUMN = "code, title, type, desc, json";
    private static final Function<Cursor, Definition> mapper = c -> {
        Definition i = new Definition();
        i.setCode(BaseDB.getString(c, "code"));
        i.setTitle(BaseDB.getString(c, "title"));
        i.setType(BaseDB.getString(c, "type"));
        i.setDesc(BaseDB.getString(c, "desc"));
        i.setJson(BaseDB.getString(c, "json"));
        return i;
    };
    private final BaseDB baseDB = BaseDB.create();

    public DefinitionMapper() {
        baseDB.executeSql(TABLE);
    }


    public int merge(Definition d) {
        SqlParam param = SqlParam.build().append("REPLACE INTO definition(" + COLUMN + ") VALUES ")
                .append("(?, ?, ?, ?, ?)", d.getCode(), d.getTitle(), d.getType(), d.getDesc(), d.getJson());
        return baseDB.executeUpdate(param);
    }

    public int delete(String type, String code) {
        SqlParam param = SqlParam.build().append("delete from definition where type = ? and code = ?", type, code);
        return baseDB.executeUpdate(param);
    }

    public List<Definition> list(String type) {
        SqlParam param = SqlParam.build().append("select " + COLUMN + " from definition where type = ?", type);
        return baseDB.list(param, mapper);
    }

    public Definition find(String type, String code) {
        SqlParam param = SqlParam.build().append("select " + COLUMN + " from definition where type = ? and code = ?", type, code);
        return baseDB.find(param, mapper);
    }
}
