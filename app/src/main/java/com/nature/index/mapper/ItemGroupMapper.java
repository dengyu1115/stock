package com.nature.index.mapper;

import android.database.Cursor;
import com.nature.base.mapper.BaseItemGroupMapper;
import com.nature.base.model.ItemGroup;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class ItemGroupMapper implements BaseItemGroupMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS item_group ( " +
            " `group` TEXT NOT NULL, " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " PRIMARY KEY (`group`, code, market))";

    private static final Function<Cursor, ItemGroup> mapper = c -> {
        ItemGroup t = new ItemGroup();
        t.setGroup(DB.getString(c, "group"));
        t.setCode(DB.getString(c, "code"));
        t.setMarket(DB.getString(c, "market"));
        return t;
    };

    private final DB db = DB.create("nature/index.db");

    public ItemGroupMapper() {
        db.executeSql(TABLE);
    }

    public int merge(ItemGroup i) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO item_group (`group`, code, market)")
                .append("VALUES (?, ?, ?)", i.getGroup(), i.getCode(), i.getMarket());
        return db.executeUpdate(param);
    }

    public int delete(String group, String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("delete from item_group")
                .append("where `group` = ? and code = ? and market = ?", group, code, market);
        return db.executeUpdate(param);
    }

    @Override
    public List<ItemGroup> listByGroup(String group) {
        SqlBuilder param = SqlBuilder.build().append("select code, market, `group` from item_group " +
                "where `group` = ?", group);
        return db.list(param, mapper);
    }

}
