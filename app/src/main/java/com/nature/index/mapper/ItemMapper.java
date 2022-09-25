package com.nature.index.mapper;

import android.database.Cursor;
import com.nature.base.mapper.BaseItemMapper;
import com.nature.base.model.Item;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

@Component
public class ItemMapper implements BaseItemMapper<Item> {

    private static final int BATCH_SIZE = 190;
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
    private final DB db = DB.create("nature/index.db");

    public ItemMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Item> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return db.batchExec(list, BATCH_SIZE, this::doBatchMerge);
    }

    private int doBatchMerge(List<Item> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO item (code, name, market) VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?, ?)", d.getCode(), d.getName(), d.getMarket());
                });
        return db.executeUpdate(param);
    }

    public int delete() {
        return db.executeUpdate(SqlBuilder.build().append("delete from item"));
    }

    @Override
    public List<Item> list() {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market from item");
        return db.list(param, mapper);
    }

    public List<Item> list(String group, String keyWord) {
        SqlBuilder param = SqlBuilder.build().append("select t1.code, t1.name, t1.market" +
                " from item t1 " +
                "join item_group t2 on t1.code = t2.code and t1.market = t2.market " +
                "where t2.`group` = ?", group);
        if (StringUtils.isNotBlank(keyWord)) {
            param.append("and (t1.name like '%'||?||'%' or t2.code like '%'||?||'%')", keyWord, keyWord);
        }
        return db.list(param, mapper);
    }

}
