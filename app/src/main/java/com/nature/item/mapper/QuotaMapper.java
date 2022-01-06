package com.nature.item.mapper;

import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.item.model.Quota;

import java.util.List;
import java.util.function.Function;

/**
 * etf basic mapper
 * @author nature
 * @version 1.0.0
 * @since 2020/4/4 19:15
 */
public class QuotaMapper {

    private static final String TABLE = "" +
            "CREATE TABLE IF NOT EXISTS quota ( " +
            " code TEXT NOT NULL, " +
            " date TEXT NOT NULL, " +
            " syl REAL, " +
            " sz_z REAL, " +
            " gb_z REAL, " +
            " sz_lt REAL, " +
            " gb_lt REAL, " +
            " price REAL, " +
            " count REAL, " +
            " syl_rate REAL, " +
            " sz_z_rate REAL, " +
            " gb_z_rate REAL, " +
            " sz_lt_rate REAL, " +
            " gb_lt_rate REAL, " +
            " price_rate REAL, " +
            " count_rate REAL, " +
            " PRIMARY KEY (code, date))";
    private static final String COLUMN = "code, date, syl, sz_z, gb_z, sz_lt, gb_lt, price, count, syl_rate, " +
            "sz_z_rate, gb_z_rate, sz_lt_rate, gb_lt_rate, price_rate, count_rate";
    private static final Function<Cursor, Quota> mapper = c -> {
        Quota t = new Quota();
        t.setCode(BaseDB.getString(c, "code"));
        t.setDate(BaseDB.getString(c, "date"));
        t.setSyl(BaseDB.getDouble(c, "syl"));
        t.setSzZ(BaseDB.getDouble(c, "sz_z"));
        t.setGbZ(BaseDB.getDouble(c, "gb_z"));
        t.setSzLt(BaseDB.getDouble(c, "sz_lt"));
        t.setGbLt(BaseDB.getDouble(c, "gb_lt"));
        t.setPrice(BaseDB.getDouble(c, "price"));
        t.setCount(BaseDB.getDouble(c, "count"));
        t.setSylRate(BaseDB.getDouble(c, "syl_rate"));
        t.setSzZRate(BaseDB.getDouble(c, "sz_z_rate"));
        t.setGbZRate(BaseDB.getDouble(c, "gb_z_rate"));
        t.setSzLtRate(BaseDB.getDouble(c, "sz_lt_rate"));
        t.setGbLtRate(BaseDB.getDouble(c, "gb_lt_rate"));
        t.setPriceRate(BaseDB.getDouble(c, "price_rate"));
        t.setCountRate(BaseDB.getDouble(c, "count_rate"));
        return t;
    };
    private final BaseDB baseDB = BaseDB.create();

    public QuotaMapper() {
        baseDB.executeSql(TABLE);
    }


    public int batchMerge(List<Quota> list) {
        SqlBuilder param = SqlBuilder.build().append("REPLACE INTO quota(" + COLUMN + ") VALUES ")
                .foreach(list, null, null, ",", (d, sqlParam) -> {
                    sqlParam.append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                            d.getCode(), d.getDate(), d.getSyl(), d.getSzZ(), d.getGbZ(), d.getSzLt(), d.getGbLt(),
                            d.getPrice(), d.getCount(), d.getSylRate(), d.getSzZRate(), d.getGbZRate(), d.getSzLtRate(),
                            d.getGbLtRate(), d.getPriceRate(), d.getCountRate());
                });
        return baseDB.executeUpdate(param);
    }

    public List<Quota> listByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from quota")
                .append("where code = ? order by date", code);
        return baseDB.list(param, mapper);
    }

    public Quota findFirstByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from quota")
                .append("where code = ? order by date asc limit 1", code);
        return baseDB.find(param, mapper);
    }


    public Quota findLastByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from quota")
                .append("where code = ? order by date desc limit 1", code);
        return baseDB.find(param, mapper);
    }

    public void delete() {
        baseDB.executeUpdate(SqlBuilder.build().append("delete from quota"));
    }

    public Quota findLast(String code, String date) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from quota")
                .append("where code = ? and date < ? order by date desc limit 1", code, date);
        return baseDB.find(param, mapper);
    }

    public List<Quota> list(String code, String dateStat, String dateEnd) {
        SqlBuilder param = SqlBuilder.build().append("select " + COLUMN + " from quota")
                .append("where code = ? and date >= ? and date <= ? order by date", code, dateStat, dateEnd);
        return baseDB.list(param, mapper);
    }
}
