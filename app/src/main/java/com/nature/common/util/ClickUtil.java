package com.nature.common.util;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ClickUtil {

    private static long millis;

    public static void doClick(View view, Runnable runnable) {
        view.setClickable(false);
        try {
            long currMillis = System.currentTimeMillis();
            if (currMillis - millis < 1000) {
                throw new RuntimeException("点击过于频繁");
            }
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
            PopUtil.alert(view.getContext(), e.getMessage());
        } finally {
            view.setClickable(true);
            millis = System.currentTimeMillis();
        }
    }

    private static final Map<View, Long> VIEW_TIME = new ConcurrentHashMap<>();

    public static void asyncClick(View view, Supplier<String> supplier) {
        Handler handler = new Handler(msg -> {
            PopUtil.alert(view.getContext(), msg.getData().getString("data"));
            return false;
        });
        new Thread(() -> {
            view.setClickable(false);
            try {
                long currMillis = System.currentTimeMillis();
                if (currMillis - millis < 1000) {
                    throw new RuntimeException("点击过于频繁");
                }
                String s = supplier.get();
                if (s != null) {
                    handler.sendMessage(message(s));
                }
            } catch (Exception e) {
                e.printStackTrace();
                handler.sendMessage(message(e.getMessage()));
            } finally {
                view.setClickable(true);
                millis = System.currentTimeMillis();
            }
        }).start();
    }

    private static Message message(String content) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("data", content);
        msg.setData(data);
        return msg;
    }

    public static void asyncClick(View view, Runnable run, Runnable handled) {
        Handler handler = new Handler(msg -> {
            handled.run();
            return false;
        });
        new Thread(() -> {
            view.setBackgroundColor(Color.BLUE);
            view.setClickable(false);
            try {
                Long millis = VIEW_TIME.get(view);
                if (millis != null && System.currentTimeMillis() - millis < 1000) {
                    throw new RuntimeException("点击过于频繁");
                }
                run.run();
            } catch (Exception e) {
                e.printStackTrace();
                Looper.prepare();
                PopUtil.alert(view.getContext(), e.getMessage());
            } finally {
                view.setBackgroundColor(Color.WHITE);
                view.setClickable(true);
                VIEW_TIME.remove(view);
                handler.sendMessage(new Message());
            }
        }).start();
    }
}
