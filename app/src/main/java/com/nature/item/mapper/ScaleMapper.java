package com.nature.item.mapper;


import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.item.model.Scale;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

/**
 * 价格净值mapper
 * @author nature
 * @version 1.0.0
 * @since 2020/8/8 15:47
 */
public class ScaleMapper {

    private static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS scale (" +
            "code TEXT NOT NULL," +
            "date TEXT NOT NULL," +
            "amount REAL," +
            "change REAL," +
            "PRIMARY KEY (code, date)" +
            ")";


    private final BaseDB baseDB = BaseDB.create();

    private final Function<Cursor, Scale> mapper = c -> {
        Scale i = new Scale();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setAmount(BaseDB.getDouble(c, "amount"));
        i.setChange(BaseDB.getDouble(c, "change"));
        return i;
    };

    public ScaleMapper() {
        baseDB.executeSql(SQL_TABLE);
    }

    public int batchMerge(List<Scale> list) {
        SqlBuilder param = SqlBuilder.build().append("replace into scale(code, date, amount, change)")
                .foreach(list, "values", null, ",", (i, p) -> {
                    p.append("(?, ?, ?, ?)", i.getCode(), i.getDate(), i.getAmount(), i.getChange());
                });
        return baseDB.executeUpdate(param);
    }

    public List<Scale> listLast(String date) {
        SqlBuilder param = SqlBuilder.build().append("select t1.* from (" +
                "select code,max(date) date from scale where date <= ? group by code) t0 " +
                "join scale t1 on t0.code = t1.code and t0.date = t1.date", date);
        return baseDB.list(param, mapper);
    }

    public List<Scale> listByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select code, date, amount, change from scale where code = ?", code);
        return baseDB.list(param, mapper);
    }

    public List<Scale> listLatest(String keyword) {
        SqlBuilder param = SqlBuilder.build().append("select t1.*,t2.name from (" +
                "select code,max(date) date from scale group by code) t0 " +
                "join scale t1 on t0.code = t1.code and t0.date = t1.date " +
                "left join item t2 on t1.code = t2.code");
        if (StringUtils.isNotBlank(keyword)) {
            param.append("where t1.code like ?||'%' or t2.name like ?||'%'", keyword, keyword);
        }
        return baseDB.list(param, mapper);
    }
}
