package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.stock.model.RateDef;

import java.util.List;
import java.util.function.Function;

public class RateDefMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS rate_def ( " +
            " code TEXT NOT NULL, " +
            " type_code TEXT NOT NULL, " +
            " title TEXT NOT NULL, " +
            " type TEXT NOT NULL, " +
            " days REAL, " +
            " date_start TEXT, " +
            " date_end TEXT, " +
            " PRIMARY KEY (type_code,code))";
    private static final String COLUMN = "code, type_code, title, type, days, date_start, date_end";
    private static final Function<Cursor, RateDef> mapper = c -> {
        RateDef i = new RateDef();
        i.setCode(BaseDB.getString(c, "code"));
        i.setTypeCode(BaseDB.getString(c, "type_code"));
        i.setTitle(BaseDB.getString(c, "title"));
        i.setType(BaseDB.getString(c, "type"));
        i.setDays(BaseDB.getInt(c, "days"));
        i.setDateStart(BaseDB.getString(c, "date_start"));
        i.setDateEnd(BaseDB.getString(c, "date_end"));
        return i;
    };
    private final BaseDB baseDB = BaseDB.create();

    public RateDefMapper() {
        baseDB.executeSql(TABLE);
    }


    public int merge(RateDef d) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO rate_def(" + COLUMN + ") VALUES ")
                .append("(?, ?, ?, ?, ?, ?, ?)", d.getCode(), d.getTypeCode(), d.getTitle(), d.getType(),
                        d.getDays(), d.getDateStart(), d.getDateEnd());
        return baseDB.executeUpdate(param);
    }

    public int delete(String type, String code) {
        SqlBuilder param = SqlBuilder.build().append("delete from rate_def")
                .append("where code = ? and type_code = ?", code, type);
        return baseDB.executeUpdate(param);
    }

    public List<RateDef> list(String type) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMN)
                .append(" from rate_def where type_code = ?", type);
        return baseDB.list(param, mapper);
    }

    public RateDef find(String type, String code) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMN)
                .append(" from rate_def where code = ? and type_code = ?", code, type);
        return baseDB.find(param, mapper);
    }
}
