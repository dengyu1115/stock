package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.stock.model.Kline;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;


public class KlineMapper {

    private static final Function<Cursor, Kline> mapper = c -> {
        Kline i = new Kline();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setMarket(BaseDB.getString(c, "market"));
        i.setPriceOpen(BaseDB.getDouble(c, "price_open"));
        i.setPriceLatest(BaseDB.getDouble(c, "price_latest"));
        i.setPriceHigh(BaseDB.getDouble(c, "price_high"));
        i.setPriceLow(BaseDB.getDouble(c, "price_low"));
        i.setShare(BaseDB.getDouble(c, "share"));
        i.setAmount(BaseDB.getDouble(c, "amount"));
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

    public List<Kline> list(String code, String market) {
        SqlBuilder param = SqlBuilder.build().append("select t1.name,t2.open price_open,t2.latest price_latest," +
                "t2.high price_high,t2.low price_low,t2.share,t2.amount,t3.* from item t1 " +
                "left join price t2 on t1.code = t2.code and t1.market = t2.market " +
                "left join net t3 on t2.code = t3.code and t2.market = t3.market and t2.date = t3.date " +
                "where t1.code = ? and t1.market = ? and t2.code is not null and t3.code is not null " +
                "order by t2.date", code, market);
        return db.list(param, mapper);
    }

    public List<Kline> list(String code, String market, String start, String end) {
        SqlBuilder param = SqlBuilder.build().append("select t1.name,t2.open price_open,t2.latest price_latest," +
                "t2.high price_high,t2.low price_low,t2.share,t2.amount,t3.* from item t1 " +
                "left join price t2 on t1.code = t2.code and t1.market = t2.market " +
                "left join net t3 on t2.code = t3.code and t2.market = t3.market and t2.date = t3.date " +
                "where t1.code = ? and t1.market = ? and t2.code is not null and t3.code is not null", code, market);
        if (StringUtils.isNotBlank(start)) {
            param.append("and date >= ?", start);
        }
        if (StringUtils.isNotBlank(end)) {
            param.append("and date <= ?", end);
        }
        param.append("order by t2.date desc");
        return db.list(param, mapper);
    }

    public List<Kline> listByDate(String date, String keyword) {
        SqlBuilder param = SqlBuilder.build().append("select t1.name,t2.open price_open,t2.latest price_latest," +
                "t2.high price_high,t2.low price_low,t2.share,t2.amount,t3.* " +
                "from (select code,market,? date,name from item", date);
        if (StringUtils.isNotBlank(keyword)) {
            param.append("where code like '%'||?||'%' or name like '%'||?||'%'", keyword, keyword);
        }
        param.append(") t1 " +
                "left join price t2 on t1.code = t2.code and t1.market = t2.market and t1.date = t2.date " +
                "left join net t3 on t1.code = t3.code and t1.market = t3.market and t1.date = t3.date " +
                "where t2.date is not null and t3.date is not null");
        return db.list(param, mapper);
    }

}
