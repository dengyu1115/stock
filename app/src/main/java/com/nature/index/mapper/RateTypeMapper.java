package com.nature.index.mapper;

import android.database.Cursor;
import com.nature.base.mapper.BaseRateTypeMapper;
import com.nature.base.model.RateType;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class RateTypeMapper implements BaseRateTypeMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS rate_type ( " +
            " code TEXT NOT NULL, " +
            " title TEXT NOT NULL, " +
            " PRIMARY KEY (code))";
    private static final String COLUMN = "code, title";
    private static final Function<Cursor, RateType> mapper = c -> {
        RateType i = new RateType();
        i.setCode(BaseDB.getString(c, "code"));
        i.setTitle(BaseDB.getString(c, "title"));
        return i;
    };
    private final DB db = DB.create("nature/index.db");

    public RateTypeMapper() {
        db.executeSql(TABLE);
    }


    public int merge(RateType d) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO rate_type(" + COLUMN + ") VALUES ")
                .append("(?, ?)", d.getCode(), d.getTitle());
        return db.executeUpdate(param);
    }

    public int delete(String code) {
        SqlBuilder param = SqlBuilder.build().append("delete from rate_type where code = ?", code);
        return db.executeUpdate(param);
    }

    public List<RateType> list() {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from rate_type");
        return db.list(param, mapper);
    }

    public RateType find(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from rate_type where code = ?", code);
        return db.find(param, mapper);
    }
}
