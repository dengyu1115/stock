package com.nature.stock.common.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Environment;
import com.nature.stock.common.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * DB的基本操作
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 12:18
 */
public class BaseDB {

    private static final String PATH = "elt/elt.db";

    private static final File INTERNAL = Environment.getExternalStorageDirectory();

    private static List<SQLiteDatabase> dbsIdling, dbsRunning;

    private static SQLiteDatabase db;

    private static BaseDB baseDB;

    /**
     * 创建单例
     * @return BaseDB
     */
    public synchronized static BaseDB create() {
        if (baseDB == null) {
            File file = new File(INTERNAL, PATH);
            FileUtil.createIfNotExists(file);
            dbsIdling = new LinkedList<>();
            dbsRunning = new LinkedList<>();
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
            int processors = Runtime.getRuntime().availableProcessors();
            for (int i = 0; i < processors; i++) {
                dbsIdling.add(SQLiteDatabase.openOrCreateDatabase(file, null));
            }
            baseDB = new BaseDB();
        }
        return baseDB;
    }

    public static Integer getInt(Cursor c, String col) {
        int columnIndex = c.getColumnIndex(col);
        if (columnIndex == -1) {
            return null;
        }
        String string = c.getString(columnIndex);
        return string == null ? null : Integer.valueOf(string);
    }

    public static Double getDouble(Cursor c, String col) {
        int columnIndex = c.getColumnIndex(col);
        if (columnIndex == -1) {
            return null;
        }
        String string = c.getString(columnIndex);
        return string == null ? null : Double.valueOf(string);
    }

    public static String getString(Cursor c, String col) {
        int columnIndex = c.getColumnIndex(col);
        if (columnIndex == -1) {
            return null;
        }
        return c.getString(columnIndex);
    }

    public <T> List<T> list(SqlParam sqlParam, Function<Cursor, T> function) {
        return this.list(sqlParam.toSQL(), sqlParam.toArgs(), function);
    }

    public <T> List<T> list(String sql, String[] args, Function<Cursor, T> function) {
        SQLiteDatabase database = this.gainDb();
        try {
            List<T> list = new ArrayList<>();
            try (Cursor cursor = database.rawQuery(sql, args)) {
                while (cursor.moveToNext()) {
                    list.add(function.apply(cursor));
                }
            }
            return list;
        } finally {
            this.returnDb(database);
        }
    }

    public <T> T find(SqlParam sqlParam, Function<Cursor, T> function) {
        return this.find(sqlParam.toSQL(), sqlParam.toArgs(), function);
    }

    public <T> T find(String sql, String[] args, Function<Cursor, T> function) {
        SQLiteDatabase database = this.gainDb();
        try {
            try (Cursor cursor = database.rawQuery(sql, args)) {
                if (cursor.getCount() > 1) {
                    throw new RuntimeException("more than one result");
                }
                if (cursor.moveToNext()) return function.apply(cursor);
                return null;
            }
        } finally {
            this.returnDb(database);
        }
    }

    public int executeSql(String sql) {
        try (SQLiteStatement statement = db.compileStatement(sql)) {
            return statement.executeUpdateDelete();
        }
    }

    public int executeUpdate(SqlParam sqlParam) {
        return executeUpdate(sqlParam.toSQL(), sqlParam.toArgs());
    }

    public int executeUpdate(String sql, String[] args) {
        try (SQLiteStatement statement = db.compileStatement(sql)) {
            for (int i = args.length; i > 0; i--) {
                String arg = args[i - 1];
                if (arg == null) {
                    statement.bindNull(i);
                } else {
                    statement.bindString(i, arg);
                }
            }
            return statement.executeUpdateDelete();
        }
    }

    /**
     * 批量执行
     * @param data      数据
     * @param batchSize 每批数量
     * @param function  每批执行逻辑
     * @param <T>       数据类型
     * @return 影响行数
     */
    public <T> int batchExec(List<T> data, int batchSize, Function<List<T>, Integer> function) {
        boolean nt = !db.inTransaction();
        if (nt) {   // 不在事务中新开事务
            AtomicInteger result = new AtomicInteger();
            this.doInTransaction(() -> result.set(this.doBatch(data, batchSize, function)));
            return result.get();
        } else {    // 在事务中沿用已有事务
            return this.doBatch(data, batchSize, function);
        }
    }

    private <T> int doBatch(List<T> data, int batchSize, Function<List<T>, Integer> function) {
        int size = data.size();
        int batch = size % batchSize == 0 ? size / batchSize : size / batchSize + 1;
        int updated = 0;
        for (int i = 0; i < batch; i++) {
            List<T> list = data.subList(batchSize * i, i == batch - 1 ? size : batchSize * (i + 1));
            updated += function.apply(list);
        }
        return updated;
    }

    /**
     * 在事务中执行
     * @param runnable 要执行的逻辑
     */
    public void doInTransaction(Runnable runnable) {
        db.beginTransaction();  // 一个事务必须在一个线程之内
        try {
            runnable.run();
            db.setTransactionSuccessful();  // 全部执行完成标记事务成功
        } finally { // 事务提交或回滚，结束事务
            db.endTransaction();
        }
    }

    private synchronized SQLiteDatabase gainDb() {
        if (dbsIdling.isEmpty()) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return gainDb();
        } else {
            SQLiteDatabase database = dbsIdling.get(0);
            dbsIdling.remove(database);
            dbsRunning.add(database);
            return database;
        }
    }

    private synchronized void returnDb(SQLiteDatabase database) {
        boolean empty = dbsIdling.isEmpty();
        dbsRunning.remove(database);
        dbsIdling.add(database);
        if (empty) this.notify();
    }

}
