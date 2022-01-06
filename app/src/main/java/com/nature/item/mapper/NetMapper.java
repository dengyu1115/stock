package com.nature.item.mapper;


import android.database.Cursor;
import com.nature.common.db.BaseDB;
import com.nature.common.db.SqlBuilder;
import com.nature.item.model.Net;

import java.util.List;
import java.util.function.Function;

/**
 * 价格净值mapper
 * @author nature
 * @version 1.0.0
 * @since 2020/8/8 15:47
 */
public class NetMapper {

    private static final String DATE = "date";

    private static final String SQL_TABLE = "" +
            "CREATE TABLE IF NOT EXISTS net (" +
            "code TEXT NOT NULL," +
            "date TEXT NOT NULL," +
            "net REAL," +
            "rate REAL," +
            "net_total REAL," +
            "rate_total REAL," +
            "PRIMARY KEY (code,date)" +
            ")";


    private final BaseDB baseDB = BaseDB.create();

    private final Function<Cursor, Net> mapper = c -> {
        Net i = new Net();
        i.setCode(BaseDB.getString(c, "code"));
        i.setDate(BaseDB.getString(c, "date"));
        i.setNet(BaseDB.getDouble(c, "net"));
        i.setRate(BaseDB.getDouble(c, "rate"));
        i.setNetTotal(BaseDB.getDouble(c, "net_total"));
        i.setRateTotal(BaseDB.getDouble(c, "rate_total"));
        return i;
    };

    public NetMapper() {
        baseDB.executeSql(SQL_TABLE);
    }

    public int batchMerge(List<Net> list) {
        SqlBuilder param = SqlBuilder.build().append("replace into net(code, date, net, rate, net_total, rate_total)")
                .foreach(list, "values", null, ",", (i, p) -> {
                    p.append("(?, ?, ?, ?, ?, ?)", i.getCode(), i.getDate(), i.getNet(), i.getRate(),
                            i.getNetTotal(), i.getRateTotal());
                });
        return baseDB.executeUpdate(param);
    }

    public List<Net> listByCode(String code) {
        SqlBuilder param = SqlBuilder.build().append("select code, date, net, rate, net_total, rate_total " +
                "from net where code = ?", code);
        return baseDB.list(param, mapper);
    }

    public List<Net> listAfter(String code, String date) {
        SqlBuilder param = SqlBuilder.build().append("select code, date, net, rate, net_total, rate_total from net")
                .append("where code = ? and date > ?", code, date);
        return baseDB.list(param, mapper);
    }

    public List<Net> list(String code, String dateStart, String dateEnd) {
        SqlBuilder param = SqlBuilder.build().append("select code, date, net, rate, net_total, rate_total from net")
                .append("where code = ? and date >= ? and date <= ?", code, dateStart, dateEnd)
                .append("order by date");
        return baseDB.list(param, mapper);
    }

    public Net findLast(String code, String date) {
        SqlBuilder param = SqlBuilder.build().append("select code, date, net, rate, net_total, rate_total from net")
                .append("where code = ? and date < ? order by date desc limit 1", code, date);
        return baseDB.find(param, mapper);
    }

    public List<Net> listLast() {
        SqlBuilder param = SqlBuilder.build().append("select t1.* from " +
                "(select code,max(date) date from net group by code) t0 " +
                "join net t1 on t0.code = t1.code and t0.date = t1.date");
        return baseDB.list(param, mapper);
    }

    public List<Net> listLast(String date) {
        SqlBuilder param = SqlBuilder.build().append("select t1.* from " +
                "(select code,max(date) date from net where date < ? group by code) t0 " +
                "join net t1 on t0.code = t1.code and t0.date = t1.date", date);
        return baseDB.list(param, mapper);
    }

    public void delete() {
        baseDB.executeUpdate(SqlBuilder.build().append("delete from net"));
    }

    public List<Net> listByDate(String date) {
        SqlBuilder param = SqlBuilder.build().append("select code, date, net, rate, net_total, rate_total from net " +
                "where date = ?", date);
        return baseDB.list(param, mapper);
    }
}
