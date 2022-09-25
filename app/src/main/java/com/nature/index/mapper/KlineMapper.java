package com.nature.index.mapper;

import android.database.Cursor;
import com.nature.base.mapper.BaseKlineMapper;
import com.nature.base.model.Avg;
import com.nature.base.model.Kline;
import com.nature.base.model.Val;
import com.nature.common.db.BaseDB;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

@Component
public class KlineMapper implements BaseKlineMapper {

    private static final Function<Cursor, Kline> mapper = c -> {
        Kline i = new Kline();
        i.setCode(BaseDB.getString(c, "code"));
        i.setName(BaseDB.getString(c, "name"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setMarket(BaseDB.getString(c, "market"));
        i.setShare(BaseDB.getDouble(c, "share"));
        i.setAmount(BaseDB.getDouble(c, "amount"));
        Val price = new Val();
        i.setPrice(price);
        price.setOpen(BaseDB.getDouble(c, "price_open"));
        price.setLatest(BaseDB.getDouble(c, "price_latest"));
        price.setHigh(BaseDB.getDouble(c, "price_high"));
        price.setLow(BaseDB.getDouble(c, "price_low"));
        Val net = new Val();
        i.setNet(net);
        net.setOpen(BaseDB.getDouble(c, "open"));
        net.setLatest(BaseDB.getDouble(c, "latest"));
        net.setHigh(BaseDB.getDouble(c, "high"));
        net.setLow(BaseDB.getDouble(c, "low"));
        Avg avg = new Avg();
        i.setAvg(avg);
        avg.setWeek(BaseDB.getDouble(c, "avg_week"));
        avg.setMonth(BaseDB.getDouble(c, "avg_month"));
        avg.setSeason(BaseDB.getDouble(c, "avg_season"));
        avg.setYear(BaseDB.getDouble(c, "avg_year"));
        return i;
    };
    private final DB db = DB.create("nature/index.db");

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
