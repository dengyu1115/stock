package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.stock.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

public class ItemMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS item ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " PRIMARY KEY (code, market))";
    private static final Function<Cursor, Item> mapper = c -> {
        Item t = new Item();
        t.setCode(DB.getString(c, "code"));
        t.setName(DB.getString(c, "name"));
        t.setMarket(DB.getString(c, "market"));
        return t;
    };
    private final DB db = DB.create("nature/stock.db");

    public ItemMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Item> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO item (code, name, market) VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?)", d.getCode(), d.getName(), d.getMarket());
                });
        return db.executeUpdate(param);
    }

    public List<Item> list(String keyWord) {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market from item");
        if (StringUtils.isNotBlank(keyWord))
            param.append("where name like '%'||?||'%' or code like '%'||?||'%'", keyWord, keyWord);
        return db.list(param, mapper);
    }

    public int delete() {
        return db.executeUpdate(SqlBuilder.build().append("delete from item"));
    }
}
