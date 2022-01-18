package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.stock.model.Group;

import java.util.List;
import java.util.function.Function;

public class GroupMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `group` ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " PRIMARY KEY (code))";
    private static final Function<Cursor, Group> mapper = c -> {
        Group t = new Group();
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        return t;
    };
    private final DB db = DB.create("nature/stock.db");

    public GroupMapper() {
        db.executeSql(TABLE);
    }

    public int merge(Group group) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO `group` (code, name) VALUES ")
                .append("(?, ?)", group.getCode(), group.getName());
        return db.executeUpdate(param);
    }

    public List<Group> list() {
        SqlBuilder param = SqlBuilder.build().append("select code, name from `group`");
        return db.list(param, mapper);
    }

    public int delete(String code) {
        SqlBuilder param = SqlBuilder.build().append("delete from `group` where code = ?", code);
        return db.executeUpdate(param);
    }

    public Group findByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select code, name from `group` where code = ?", code);
        return db.find(param, mapper);
    }

}
