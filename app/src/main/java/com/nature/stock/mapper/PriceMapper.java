package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.stock.model.Price;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;


public class PriceMapper {

    private static final int BATCH_SIZE = 100;
    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS price ( " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " open REAL, " +
            " latest REAL, " +
            " high REAL, " +
            " low REAL, " +
            " share REAL, " +
            " amount REAL, " +
            " PRIMARY KEY (code, market, date))";
    private static final String COLUMNS = "code, market, date, open, latest, high, low, share, amount";
    private static final Function<Cursor, Price> mapper = c -> {
        Price i = new Price();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setMarket(BaseDB.getString(c, "market"));
        i.setOpen(BaseDB.getDouble(c, "open"));
        i.setLatest(BaseDB.getDouble(c, "latest"));
        i.setHigh(BaseDB.getDouble(c, "high"));
        i.setLow(BaseDB.getDouble(c, "low"));
        i.setShare(BaseDB.getDouble(c, "share"));
        i.setAmount(BaseDB.getDouble(c, "amount"));
        return i;
    };
    private final DB db = DB.create("nature/stock.db");

    public PriceMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Price> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return db.batchExec(list, BATCH_SIZE, this::doBatchMerge);
    }

    private int doBatchMerge(List<Price> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO price(").append(COLUMNS).append(")VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?, ?, ?, ?, ?, ?)", d.getCode(), d.getMarket(), d.getDate(),
                            d.getOpen(), d.getLatest(), d.getHigh(), d.getLow(), d.getShare(), d.getAmount());
                });
        return db.executeUpdate(param);
    }

    public void delete() {
        db.executeUpdate(SqlBuilder.build().append("delete from price"));
    }

    public List<Price> list(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from price")
                .append("where code = ? and market = ?", code, market)
                .append("order by date");
        return db.list(param, mapper);
    }

    public List<Price> list(String code, String market, String start, String end) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from price")
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

    public List<Price> listByDate(String date, String keyword) {
        SqlBuilder param = SqlBuilder.build().append("select t1.name,t2.* from " +
                "(select code,market,? date,name from item", date);
        if (StringUtils.isNotBlank(keyword)) {
            param.append("where code like '%'||?||'%' or name like '%'||?||'%'", keyword, keyword);
        }
        param.append(") t1 " +
                "left join price t2 on t1.code = t2.code and t1.market = t2.market and t1.date = t2.date " +
                "where t2.date is not null");
        return db.list(param, mapper);
    }

    public Price findLatest(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from price")
                .append("where code = ? and market = ?", code, market)
                .append("order by date desc limit 1");
        return db.find(param, mapper);
    }

}
