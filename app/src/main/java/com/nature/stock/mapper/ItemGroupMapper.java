package com.nature.stock.mapper;

import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.stock.model.ItemGroup;

public class ItemGroupMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS item_group ( " +
            " `group` TEXT NOT NULL, " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " PRIMARY KEY (`group`, code, market))";

    private final DB db = DB.create("nature/stock.db");

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

}
