package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;
import com.nature.stock.model.Net;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

@Component
public class NetMapper {

    private static final int BATCH_SIZE = 70;
    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS net ( " +
            " code TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " open REAL, " +
            " latest REAL, " +
            " high REAL, " +
            " low REAL, " +
            " avg_week REAL, " +
            " avg_month REAL, " +
            " avg_season REAL, " +
            " avg_year REAL, " +
            " PRIMARY KEY (code, market, date))";
    private static final String COLUMNS = "code, market, date, open, latest, high, low, avg_week, avg_month, " +
            "avg_season, avg_year";
    private static final Function<Cursor, Net> mapper = c -> {
        Net i = new Net();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setMarket(BaseDB.getString(c, "market"));
        i.setOpen(BaseDB.getDouble(c, "open"));
        i.setLatest(BaseDB.getDouble(c, "latest"));
        i.setHigh(BaseDB.getDouble(c, "high"));
        i.setLow(BaseDB.getDouble(c, "low"));
        i.setAvgWeek(BaseDB.getDouble(c, "avg_week"));
        i.setAvgMonth(BaseDB.getDouble(c, "avg_month"));
        i.setAvgSeason(BaseDB.getDouble(c, "avg_season"));
        i.setAvgYear(BaseDB.getDouble(c, "avg_year"));
        return i;
    };
    private final DB db = DB.create("nature/stock.db");

    public NetMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Net> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return db.batchExec(list, BATCH_SIZE, this::doBatchMerge);
    }

    private int doBatchMerge(List<Net> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO net(").append(COLUMNS).append(")VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", d.getCode(), d.getMarket(), d.getDate(),
                            d.getOpen(), d.getLatest(), d.getHigh(), d.getLow(), d.getAvgWeek(), d.getAvgMonth(),
                            d.getAvgSeason(), d.getAvgYear());
                });
        return db.executeUpdate(param);
    }

    public void delete() {
        db.executeUpdate(SqlBuilder.build().append("delete from net"));
    }

    public List<Net> list(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from net")
                .append("where code = ? and market = ?", code, market)
                .append("order by date");
        return db.list(param, mapper);
    }

    public List<Net> list(String code, String market, String start, String end) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from net")
                .append("where code = ? and market = ?", code, market);
        if (StringUtils.isNotBlank(start)) {
            param.append("and date >= ?", start);
        }
        if (StringUtils.isNotBlank(end)) {
            param.append("and date <= ?", end);
        }
        param.append("order by date");
        return db.list(param, mapper);
    }

    public List<Net> listBefore(String code, String market, String date, int limit) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from net")
                .append("where code = ? and market = ? and date < ? ", code, market, date)
                .append("order by date desc limit ?", limit);
        return db.list(param, mapper);
    }

    public List<Net> listByDate(String date, String keyword) {
        SqlBuilder param = SqlBuilder.build().append("select t1.name,t2.* from " +
                "(select code,market,? date,name from item", date);
        if (StringUtils.isNotBlank(keyword)) {
            param.append("where code like '%'||?||'%' or name like '%'||?||'%'", keyword, keyword);
        }
        param.append(") t1 " +
                "left join net t2 on t1.code = t2.code and t1.market = t2.market and t1.date = t2.date " +
                "where t2.date is not null");
        return db.list(param, mapper);
    }

    public Net findLatest(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select").append(COLUMNS).append("from net")
                .append("where code = ? and market = ?", code, market)
                .append("order by date desc limit 1");
        return db.find(param, mapper);
    }

}
