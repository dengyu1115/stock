package com.nature.item.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.item.model.Kline;

import java.util.List;
import java.util.function.Function;

/**
 * etf kline mapper
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 19:15
 */
public class KlineMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS kline ( " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " open REAL, " +
            " latest REAL, " +
            " high REAL, " +
            " low REAL, " +
            " share REAL, " +
            " amount REAL, " +
            " avg_week REAL, " +
            " avg_month REAL, " +
            " avg_season REAL, " +
            " avg_year REAL, " +
            " PRIMARY KEY (code, market, date))";
    private static final String COLUMNS = "code, market, date, open, latest, high, low, share, amount" +
            ", avg_week, avg_month, avg_season, avg_year";
    private static final String SQL_QUERY_DATE = "" +
            "select t2.* from " +
            "(select code,market,? date from item) t1 " +
            "left join kline t2 on t1.code = t2.code and t1.market = t2.market and t1.date = t2.date " +
            "where t2.date is not null";
    private static final Function<Cursor, Kline> mapper = c -> {
        Kline t = new Kline();
        t.setCode(BaseDB.getString(c, "code"));
        t.setDate(BaseDB.getString(c, "date"));
        t.setMarket(BaseDB.getString(c, "market"));
        t.setOpen(BaseDB.getDouble(c, "open"));
        t.setLatest(BaseDB.getDouble(c, "latest"));
        t.setHigh(BaseDB.getDouble(c, "high"));
        t.setLow(BaseDB.getDouble(c, "low"));
        t.setShare(BaseDB.getDouble(c, "share"));
        t.setAmount(BaseDB.getDouble(c, "amount"));
        t.setAvgWeek(BaseDB.getDouble(c, "avg_week"));
        t.setAvgMonth(BaseDB.getDouble(c, "avg_month"));
        t.setAvgSeason(BaseDB.getDouble(c, "avg_season"));
        t.setAvgYear(BaseDB.getDouble(c, "avg_year"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public KlineMapper() {
        baseDB.executeSql(TABLE);
    }

    public int batchMerge(List<Kline> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO kline(").append(COLUMNS).append(")VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", d.getCode(), d.getMarket()
                            , d.getDate(), d.getOpen(), d.getLatest(), d.getHigh(), d.getLow(), d.getShare()
                            , d.getAmount(), d.getAvgWeek(), d.getAvgMonth(), d.getAvgSeason(), d.getAvgYear());
                });
        return baseDB.executeUpdate(param);
    }

    public List<Kline> list(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from kline")
                .append("where code = ? and market = ?", code, market)
                .append("order by date");
        return baseDB.list(param, mapper);
    }


    public List<Kline> list(String code, String market, String dateStart, String dateEnd) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from kline where")
                .append("code = ? and market = ? and date >= ? and date <= ?", code, market, dateStart, dateEnd)
                .append("order by date");
        return baseDB.list(param, mapper);
    }

    public List<Kline> listBefore(String code, String market, String date, int start, int limit) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from kline")
                .append("where code = ? and market = ? and open > 0", code, market);
        if (date != null) param.append("and date < ?", date);
        param.append("order by date desc limit ?, ?", start, limit);
        return baseDB.list(param, mapper);
    }

    public List<Kline> listByDate(String date) {
        SqlBuilder param = SqlBuilder.build().append(SQL_QUERY_DATE, date);
        return baseDB.list(param, mapper);
    }

    public List<Kline> listAfter(String code, String market, String date) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from kline")
                .append("where code = ? and market = ? and open > 0", code, market);
        if (date != null) param.append("and date > ?", date);
        return baseDB.list(param, mapper);
    }

    public void delete() {
        baseDB.executeUpdate(SqlBuilder.build().append("delete from kline"));
    }

    public List<Kline> listLast() {
        SqlBuilder param = SqlBuilder.build().append("select t1.* from " +
                "(select code,max(date) date from kline group by code) t0 " +
                "join kline t1 on t0.code = t1.code and t0.date = t1.date");
        return baseDB.list(param, mapper);
    }

    public Kline findLast(String code, String market, String date) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from kline")
                .append("where code = ? and market = ? and date < ? order by date desc limit 1", code, market, date);
        return baseDB.find(param, mapper);
    }
}
