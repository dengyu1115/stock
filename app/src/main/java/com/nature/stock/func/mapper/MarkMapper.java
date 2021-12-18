package com.nature.stock.func.mapper;

import android.database.Cursor;
import com.nature.stock.common.db.BaseDB;
import com.nature.stock.common.db.SqlParam;
import com.nature.stock.func.model.Mark;

import java.util.List;
import java.util.function.Function;

/**
 * 交易
 * @author nature
 * @version 1.0.0
 * @since 2020/11/7 9:15
 */
public class MarkMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS trade ( " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " price REAL NOT NULL, " +
            " rate_buy REAL NOT NULL, " +
            " rate_sell REAL NOT NULL, " +
            " PRIMARY KEY (code,market,date))";
    private static final String COLUMNS = "code, market, date, price, rate_buy, rate_sell";
    private static final Function<Cursor, Mark> mapper = c -> {
        Mark t = new Mark();
        t.setCode(BaseDB.getString(c, "code"));
        t.setMarket(BaseDB.getString(c, "market"));
        t.setDate(BaseDB.getString(c, "date"));
        t.setPrice(BaseDB.getDouble(c, "price"));
        t.setRateBuy(BaseDB.getDouble(c, "rate_buy"));
        t.setRateSell(BaseDB.getDouble(c, "rate_sell"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public MarkMapper() {
        baseDB.executeSql(TABLE);
    }

    public int merge(Mark d) {
        SqlParam param = SqlParam.build().append("REPLACE INTO trade (" + COLUMNS + ") VALUES ")
                .append("(?, ?, ?, ?, ?, ?)", d.getCode(), d.getMarket(), d.getDate(), d.getPrice(),
                        d.getRateBuy(), d.getRateSell());
        return baseDB.executeUpdate(param);
    }

    public List<Mark> list() {
        SqlParam param = SqlParam.build().append("select t2.code, t2.market, t2.date, t2.price, t2.rate_buy, " +
                "t2.rate_sell from (select code,market,max(date) date from trade group by code,market) t1 " +
                "left join trade t2 on t1.code = t2.code and t1.market = t2.market and t1.date = t2.date ");
        return baseDB.list(param, mapper);
    }

    public List<Mark> list(String code, String market) {
        SqlParam param = SqlParam.build().append("select " + COLUMNS + " from trade " +
                "where code = ? and market = ? order by date asc", code, market);
        return baseDB.list(param, mapper);
    }

    public int delete(String code, String market, String date) {
        SqlParam param = SqlParam.build().append("delete from trade  where code = ? and market = ? and date = ?",
                code, market, date);
        return baseDB.executeUpdate(param);
    }

    public Mark find(String code, String market, String date) {
        SqlParam param = SqlParam.build().append("select " + COLUMNS + " from trade  where " +
                "code = ? and market = ? and date = ?", code, market, date);
        return baseDB.find(param, mapper);
    }
}
