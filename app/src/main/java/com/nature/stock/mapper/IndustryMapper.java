package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;
import com.nature.stock.model.Industry;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.function.Function;

@Component
public class IndustryMapper {

    private static final int BATCH_SIZE = 200;
    private static final String TABLE = ""
            + "CREATE TABLE IF NOT EXISTS industry ( "
            + " code TEXT NOT NULL, "
            + " name TEXT NOT NULL, "
            + " PRIMARY KEY (code))";
    private static final String COLUMNS = "code, name";
    private static final Function<Cursor, Industry> mapper = c -> {
        Industry i = new Industry();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        return i;
    };
    private final DB db = DB.create("nature/stock.db");

    public IndustryMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Industry> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return db.batchExec(list, BATCH_SIZE, this::doBatchMerge);
    }

    private int doBatchMerge(List<Industry> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO industry(")
                .append(COLUMNS).append(")VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?)", d.getCode(), d.getName());
                });
        return db.executeUpdate(param);
    }

    public void delete() {
        db.executeUpdate(SqlBuilder.build().append("delete from industry"));
    }

    public List<Industry> list() {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from industry");
        return db.list(param, mapper);
    }

}
