package com.nature.item.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.item.model.Group;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

public class GroupMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS `group` ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " type TEXT NOT NULL, " +
            " remark TEXT NOT NULL, " +
            " PRIMARY KEY (code,type))";
    private static final Function<Cursor, Group> mapper = c -> {
        Group t = new Group();
        t.setCode(BaseDB.getString(c, "code"));
        t.setName(BaseDB.getString(c, "name"));
        t.setType(BaseDB.getString(c, "type"));
        t.setRemark(BaseDB.getString(c, "remark"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public GroupMapper() {
        baseDB.executeSql(TABLE);
    }

    public int merge(Group group) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO `group` (code, type, name, remark) VALUES ")
                .append("(?, ?, ?, ?)", group.getCode(), group.getType(), group.getName(), group.getRemark());
        return baseDB.executeUpdate(param);
    }

    public List<Group> list(String type) {
        SqlBuilder param = SqlBuilder.build().append("select code, type, name, remark from `group`");
        if (StringUtils.isNoneBlank(type)) param.append("where type = ?", type);
        return baseDB.list(param, mapper);
    }

    public int delete(String code, String type) {
        SqlBuilder param = SqlBuilder.build().append("delete from `group`  where code = ? and type = ?", code, type);
        return baseDB.executeUpdate(param);
    }

    public Group findByCode(String code, String type) {
        SqlBuilder param = SqlBuilder.build().append("select code, type, name, remark from `group` " +
                "where code = ? and type = ?", code, type);
        return baseDB.find(param, mapper);
    }

}
