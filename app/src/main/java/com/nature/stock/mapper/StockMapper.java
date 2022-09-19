package com.nature.stock.mapper;

import android.database.Cursor;
import com.nature.base.mapper.BaseItemMapper;
import com.nature.common.db.DB;
import com.nature.common.db.SqlBuilder;
import com.nature.common.ioc.annotation.Component;
import com.nature.stock.model.Stock;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.function.Function;

@Component
public class StockMapper implements BaseItemMapper<Stock> {

    private static final int BATCH_SIZE = 190;
    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS stock ( " +
            " code TEXT NOT NULL, " +
            " name TEXT NOT NULL, " +
            " market TEXT NOT NULL, " +
            " exchange TEXT KEY, " +
            " industry TEXT KEY, " +
            " PRIMARY KEY (code, market))";
    private static final Function<Cursor, Stock> mapper = c -> {
        Stock t = new Stock();
        t.setCode(DB.getString(c, "code"));
        t.setName(DB.getString(c, "name"));
        t.setMarket(DB.getString(c, "market"));
        t.setExchange(DB.getString(c, "exchange"));
        t.setIndustry(DB.getString(c, "industry"));
        return t;
    };
    private final DB db = DB.create("nature/stock.db");

    public StockMapper() {
        db.executeSql(TABLE);
    }

    public int batchMerge(List<Stock> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }
        return db.batchExec(list, BATCH_SIZE, this::doBatchMerge);
    }

    private int doBatchMerge(List<Stock> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO stock (code, name, market, exchange, industry) VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?, ?)", d.getCode(), d.getName(), d.getMarket(), d.getExchange(), d.getIndustry());
                });
        return db.executeUpdate(param);
    }

    public int delete() {
        return db.executeUpdate(SqlBuilder.build().append("delete from stock"));
    }

    @Override
    public List<Stock> list() {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market, exchange, industry from stock");
        return db.list(param, mapper);
    }

    public List<Stock> list(String exchange, String industry, String keyWord) {
        SqlBuilder param = SqlBuilder.build().append("select code, name, market, exchange, industry from stock where 1 = 1");
        if (StringUtils.isNotBlank(exchange)) {
            param.append("and exchange = ?", exchange);
        }
        if (StringUtils.isNotBlank(industry)) {
            param.append("and industry = ?", industry);
        }
        if (StringUtils.isNotBlank(keyWord)) {
            param.append("and (name like '%'||?||'%' or code like '%'||?||'%')", keyWord, keyWord);
        }
        return db.list(param, mapper);
    }

    public List<Stock> list(String group, String keyWord) {
        SqlBuilder param = SqlBuilder.build().append("select t1.code, t1.name, t1.market, t1.exchange, t1.industry" +
                " from stock t1 " +
                "join item_group t2 on t1.code = t2.code and t1.market = t2.market " +
                "where t2.`group` = ?", group);
        if (StringUtils.isNotBlank(keyWord)) {
            param.append("and (t1.name like '%'||?||'%' or t2.code like '%'||?||'%')", keyWord, keyWord);
        }
        return db.list(param, mapper);
    }

}
