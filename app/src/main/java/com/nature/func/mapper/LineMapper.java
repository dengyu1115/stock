package com.nature.func.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.func.model.Line;

import java.util.List;
import java.util.function.Function;

public class LineMapper {

    private static final Function<Cursor, Line> mapper = c -> {
        Line t = new Line();
        t.setDate(BaseDB.getString(c, "date"));
        t.setPrice(BaseDB.getDouble(c, "price"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public List<Line> list(String sql) {
        return baseDB.list(SqlBuilder.build().append(sql), mapper);
    }


}
