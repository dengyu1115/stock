package com.nature.func.mapper;


import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.func.model.PriceNet;

import java.util.List;
import java.util.function.Function;

/**
 * 价格净值mapper
 * @author nature
 * @version 1.0.0
 * @since 2020/8/8 15:47
 */
public class PriceNetMapper {

    private static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS %s (" +
            "code TEXT NOT NULL," +
            "date TEXT NOT NULL," +
            "price_last REAL," +
            "price_latest REAL," +
            "price_high REAL," +
            "price_low REAL," +
            "net_last REAL," +
            "net_latest REAL," +
            "rate_price REAL," +
            "rate_net REAL," +
            "rate_diff REAL," +
            "scale REAL," +
            "amount REAL," +
            "rate_amount REAL," +
            "PRIMARY KEY (code,date)" +
            ")";

    private static final String SQL_COLUMN = "code, date, price_last, price_latest, price_high, price_low, " +
            "net_last, net_latest, rate_price, rate_net, rate_diff, scale, amount, rate_amount";


    private final BaseDB baseDB = BaseDB.create();

    private final Function<Cursor, PriceNet> mapper = c -> {
        PriceNet i = new PriceNet();
        i.setCode(BaseDB.getString(c, "code"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setPriceLast(BaseDB.getDouble(c, "price_last"));
        i.setPriceLatest(BaseDB.getDouble(c, "price_latest"));
        i.setPriceHigh(BaseDB.getDouble(c, "price_high"));
        i.setPriceLow(BaseDB.getDouble(c, "price_low"));
        i.setNetLast(BaseDB.getDouble(c, "net_last"));
        i.setNetLatest(BaseDB.getDouble(c, "net_latest"));
        i.setRatePrice(BaseDB.getDouble(c, "rate_price"));
        i.setRateNet(BaseDB.getDouble(c, "rate_net"));
        i.setRateDiff(BaseDB.getDouble(c, "rate_diff"));
        i.setScale(BaseDB.getDouble(c, "scale"));
        i.setAmount(BaseDB.getDouble(c, "amount"));
        i.setRateAmount(BaseDB.getDouble(c, "rate_amount"));
        return i;
    };

    public PriceNetMapper() {
        baseDB.executeSql(String.format(SQL_TABLE, "price_net"));
        baseDB.executeSql(String.format(SQL_TABLE, "price_net_temp"));
    }

    public int delete() {
        return baseDB.executeUpdate(SqlBuilder.build().append("delete from price_net"));
    }

    public int batchSave(List<PriceNet> list) {
        SqlBuilder param = SqlBuilder.build().append("insert into price_net_temp(" + SQL_COLUMN + ")")
                .foreach(list, "values", null, ",", (i, p) ->
                        p.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", i.getCode(), i.getDate(),
                                i.getPriceLast(), i.getPriceLatest(), i.getPriceHigh(), i.getPriceLow(), i.getNetLast(),
                                i.getNetLatest(), i.getRatePrice(),
                                i.getRateNet(), i.getRateDiff(), i.getScale(), i.getAmount(), i.getRateAmount()));
        return baseDB.executeUpdate(param);
    }

    public int batchMerge(List<PriceNet> list) {
        SqlBuilder param = SqlBuilder.build().append("replace into price_net(" + SQL_COLUMN + ")")
                .foreach(list, "values", null, ",", (i, p) ->
                        p.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", i.getCode(), i.getDate(),
                                i.getPriceLast(), i.getPriceLatest(), i.getPriceHigh(), i.getPriceLow(), i.getNetLast(),
                                i.getNetLatest(), i.getRatePrice(),
                                i.getRateNet(), i.getRateDiff(), i.getScale(), i.getAmount(), i.getRateAmount()));
        return baseDB.executeUpdate(param);
    }

    public List<PriceNet> list(String date) {
        SqlBuilder param = SqlBuilder.build().append("select " + SQL_COLUMN + " from price_net")
                .append("where date = ?", date);
        return baseDB.list(param, mapper);
    }

    public List<PriceNet> listByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + SQL_COLUMN + " from price_net")
                .append("where code = ? order by date", code);
        return baseDB.list(param, mapper);
    }

    public PriceNet findLatest(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + SQL_COLUMN + " from price_net")
                .append("where code = ? order by date desc limit 1", code);
        return baseDB.find(param, mapper);
    }
}
