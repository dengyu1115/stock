package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;
import com.nature.stock.model.Pe;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

@Component
public class PeMapper {

    private static final int BATCH_SIZE = 200;
    private static final String TABLE = "" + "CREATE TABLE IF NOT EXISTS pe ( " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " pe REAL, " +
            " PRIMARY KEY (code, market, date))";
    private static final String COLUMNS = "code, market, date, pe";
    private static final Function<Cursor, Pe> mapper = c -> {
        Pe i = new Pe();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        i.setMarket(BaseDB.getString(c, "market"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setPe(BaseDB.getDouble(c, "pe"));
        return i;
    };
    private final DB db = DB.create("nature/stock.db");

    public PeMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Pe> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return db.batchExec(list, BATCH_SIZE, this::doBatchMerge);
    }

    private int doBatchMerge(List<Pe> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO pe(").append(COLUMNS).append(")VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?)", d.getCode(), d.getMarket(), d.getDate(), d.getPe());
                });
        return db.executeUpdate(param);
    }

    public void delete() {
        db.executeUpdate(SqlBuilder.build().append("delete from pe"));
    }

    public List<Pe> list(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from pe")
                .append("where code = ? and market = ?", code, market).append("order by date");
        return db.list(param, mapper);
    }

    public List<Pe> list(String code, String market, String start, String end) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from pe")
                .append("where code = ? and market = ?", code, market);
        if (StringUtils.isNotBlank(start)) {
            param.append("and date >= ?", start);
        }
        if (StringUtils.isNotBlank(end)) {
            param.append("and date <= ?", end);
        }
        param.append("order by date desc");
        return db.list(param, mapper);
    }

    public List<Pe> listByDate(String date, String keyword) {
        SqlBuilder param = SqlBuilder.build().append("select t1.name,t2.* from "
                + "(select code,exchange,? date,name from stock", date);
        if (StringUtils.isNotBlank(keyword)) {
            param.append("where code like '%'||?||'%' or name like '%'||?||'%'", keyword, keyword);
        }
        param.append(") t1 "
                + "left join pe t2 on t1.code = t2.code and t1.exchange = t2.market and t1.date = t2.date "
                + "where t2.date is not null order by code");
        return db.list(param, mapper);
    }

    public Pe findLatest(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from pe")
                .append("where code = ? and market = ?", code, market).append("order by date desc limit 1");
        return db.find(param, mapper);
    }

}
