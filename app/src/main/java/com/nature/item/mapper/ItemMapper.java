package com.nature.item.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.item.model.Item;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

public class ItemMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS item ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " type TEXT NOT NULL, " +
            " PRIMARY KEY (code, market))";
    private static final Function<Cursor, Item> mapper = c -> {
        Item t = new Item();
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        t.setMarket(BaseDB.getString(c, "market"));
        t.setType(BaseDB.getString(c, "type"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public ItemMapper() {
        baseDB.executeSql(TABLE);
    }

    public int batchMerge(List<Item> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO item (code, name, market, type) VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?)", d.getCode(), d.getName(), d.getMarket(), d.getType());
                });
        return baseDB.executeUpdate(param);
    }

    public List<Item> listByType(String type) {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market, type from item");
        if (StringUtils.isNotBlank(type)) param.append("where type = ?", type);
        return baseDB.list(param, mapper);
    }

    public List<Item> listByKeyWord(String keyWord) {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market, type from item");
        if (StringUtils.isNotBlank(keyWord))
            param.append("where name like '%'||?||'%' or code like '%'||?||'%'", keyWord, keyWord);
        return baseDB.list(param, mapper);
    }

    public List<Item> list(String type, String keyWord) {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market, type from item");
        param.append("where type = ?", type);
        if (StringUtils.isNotBlank(keyWord)) {
            param.append("and (name like '%'||?||'%' or code like '%'||?||'%')", keyWord, keyWord);
        }
        return baseDB.list(param, mapper);
    }

    public int delete() {
        return baseDB.executeUpdate(SqlBuilder.build().append("delete from item"));
    }
}
