package com.nature.stock.item.mapper;

import android.database.Cursor;
import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.db.SqlParam;
import com.nature.stock.item.model.ItemGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * etf basic mapper
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 19:15
 */
public class ItemGroupMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS item_group ( " +
            " `group` TEXT NOT NULL, " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " type TEXT NOT NULL, " +
            " PRIMARY KEY (`group`, code, market))";
    private static final Function<Cursor, ItemGroup> mapper = c -> {
        ItemGroup t = new ItemGroup();
        t.setGroup(BaseDB.getString(c, "group"));
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        t.setMarket(BaseDB.getString(c, "market"));
        t.setType(BaseDB.getString(c, "type"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public ItemGroupMapper() {
        baseDB.executeSql(TABLE);
    }

    public int merge(ItemGroup i) {
        SqlParam param = SqlParam.build().append("REPLACE INTO item_group (`group`, code, name, market, type)")
                .append("VALUES (?, ?, ?, ?, ?)", i.getGroup(), i.getCode(), i.getName(), i.getMarket(), i.getType());
        return baseDB.executeUpdate(param);
    }

    public List<ItemGroup> list(String group, String keyWord) {
        SqlParam param = SqlParam.build().append("select `group`,code, name, market, type from item_group");
        param.append("where `group` = ?", group);
        if (StringUtils.isNotBlank(keyWord))
            param.append("and name like '%'||?||'%' or code like '%'||?||'%'", keyWord, keyWord);
        return baseDB.list(param, mapper);
    }

    public int delete(String group, String code, String market) {
        SqlParam param = SqlParam.build().append("delete from item_group")
                .append("where `group` = ? and code = ? and market = ?", group, code, market);
        return baseDB.executeUpdate(param);
    }

    public List<ItemGroup> listByGroups(List<String> groups) {
        SqlParam param = SqlParam.build().append("select `group`,code, name, market, type from item_group");
        param.foreach(groups, "where `group` in(", ")", ",", (i, p) -> {
            p.append("?", i);
        });
        return baseDB.list(param, mapper);
    }
}
