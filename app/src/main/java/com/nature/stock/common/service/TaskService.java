package com.nature.stock.common.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.nature.stock.R;
import com.nature.stock.common.constant.Constant;
import com.nature.stock.common.manager.TaskManager;
import com.nature.stock.common.util.InstanceHolder;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 示例服务（运行于前台，可以在锁屏状态执行，定时器逻辑）
 * @author nature
 * @version 1.0.0
 * @since 2020/1/4 12:20
 */
public class TaskService extends Service {

    /**
     * 启动notification的id，两次启动应是同一个id
     */
    private final static int NOTIFICATION_ID = 1;
    /**
     * 服务通道id
     */
    private final static String CHANNEL_ID = "KLINE_CHANNEL";
    /**
     * 服务通道name
     */
    private final static String CHANNEL_NAME = "KLINE服务通道";
    /**
     * 执行间隔
     */
    private static final int PERIOD = 60 * 1000;
    private static final AtomicInteger counter = new AtomicInteger();
    /**
     * 定时器
     */
    private static ScheduledExecutorService service;
    private final TaskManager taskManager = InstanceHolder.get(TaskManager.class);
    /**
     * 唤醒锁
     */
    private PowerManager.WakeLock wl;

    /**
     * 创建服务
     */
    @Override
    public void onCreate() {
        super.onCreate();
        this.createNotificationChannel();
        this.getNotificationManager().notify(NOTIFICATION_ID, this.notification("服务初始化..."));
        // 设置为前台进程，降低oom_adj，提高进程优先级，提高存活机率
        this.startForeground(NOTIFICATION_ID, this.notification("服务前台启动..."));
        this.acquireWakeLock();
        synchronized (TaskService.class) {  // 保证逻辑只启动一次
            if (service != null) {
                return;
            }
        }
        getService().scheduleAtFixedRate(this.task(), this.calculateDelay(), PERIOD, TimeUnit.MILLISECONDS);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 服务启动后执行逻辑
     * @param intent  intent
     * @param flags   flags
     * @param startId startId
     * @return int
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    /**
     * 计算延迟执行时间
     * @return int
     */
    private long calculateDelay() {
        Calendar calendar = Calendar.getInstance();
        long now = calendar.getTimeInMillis();
        calendar.add(Calendar.MINUTE, 1);
        long later = calendar.getTimeInMillis() / PERIOD * PERIOD;
        return later - now;
    }

    /**
     * 服务终止调用
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.stopForeground(true);
        this.releaseWakeLock();
        service.shutdown();
        service = null;
    }

    /**
     * 获取timer
     * @return timer
     */
    private ScheduledExecutorService getService() {
        if (service == null) {
            synchronized (TaskService.class) {
                if (service == null) {
                    service = new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors());
                }
            }
        }
        return service;
    }

    /**
     * 定时任务执行的逻辑
     * @return TimerTask
     */
    private Runnable task() {
        return () -> {
            try {
                String date = DateFormatUtils.format(new Date(), Constant.FORMAT_DATETIME);
                String s = String.format("%s:%s", date, counter.incrementAndGet());
                getNotificationManager().notify(NOTIFICATION_ID, notification(s));
                service.execute(taskManager::execute);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }

    /**
     * 创建通知对象
     * @return Notification
     */
    private Notification notification(String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.app_icon).setContentText(content).setContentTitle("KLINE正在运行");
        return builder.build();
    }

    /**
     * 创建通知channel
     */
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        this.getNotificationManager().createNotificationChannel(channel);
    }

    /**
     * 获取通知管理
     * @return NotificationManager
     */
    private NotificationManager getNotificationManager() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        return notificationManager;
    }

    /**
     * 获取唤醒锁
     */
    @SuppressLint({"WakelockTimeout", "InvalidWakeLockTag"})
    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        assert pm != null;
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskService");
        wl.acquire();   // 获取唤醒锁
    }

    /**
     * 释放唤醒锁
     */
    private void releaseWakeLock() {
        wl.release();
    }

}
