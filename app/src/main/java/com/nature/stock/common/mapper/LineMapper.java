package com.nature.stock.common.mapper;

import android.database.Cursor;
import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.db.SqlParam;
import com.nature.stock.common.model.Line;

import java.util.List;
import java.util.function.Function;

/**
 * 折线
 * @author nature
 * @version 1.0.0
 * @since 2020/8/30 21:50
 */
public class LineMapper {

    private static final Function<Cursor, Line> mapper = c -> {
        Line t = new Line();
        t.setDate(BaseDB.getString(c, "date"));
        t.setPrice(BaseDB.getDouble(c, "price"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public List<Line> list(String sql) {
        return baseDB.list(SqlParam.build().append(sql), mapper);
    }


}
