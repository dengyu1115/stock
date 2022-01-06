package com.nature.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.nature.func.model.PriceNet;
import com.nature.common.util.TextUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 价值净值折线图
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 22:53
 */
public class PriceNetView extends View {

    private static final int SIZE_DEFAULT = 90, SIZE_MIN = 30, SIZE_MAX = 800;
    private static final int[] COLORS = new int[]{Color.RED, Color.GREEN, Color.BLUE, Color.GRAY, Color.YELLOW};
    private final String[] ns = new String[]{"日期:", "最高:", "最低:", "价格:", "净值:", "折价率:", "交易额:"};
    private final Q[] qs = new Q[]{new Q(ns[0]), new Q(ns[1]), new Q(ns[2]), new Q(ns[3]), new Q(ns[4]), new Q(ns[5]), new Q(ns[6])};
    /**
     * 位置坐标
     */
    private final XY all = new XY(), price = new XY(), amount = new XY(), rate = new XY();
    /**
     * 画笔
     */
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<PriceNet> data, list;
    private List<String> dateTexts, priceTexts, rateTexts, amountTexts;
    private int intervalDate, intervalPrice, intervalRate, intervalAmount, index, listSize = SIZE_DEFAULT, listStart, listEnd;
    private float unitDate, unitPrice, unitRate, unitAmount, dx, px, py, lx;
    private boolean longPressed, moving;
    private Double minPrice, minRate, minAmount;

    public PriceNetView(Context context) {
        super(context);
    }

    /**
     * 填充数据
     * @param data 数据
     */
    public void data(List<PriceNet> data) {
        this.data = data;
        if (data == null) throw new RuntimeException("数据不可集不可为null");
        int size = data.size();
        if (listSize < size) {  // 数量超出的截取尾部展示
            this.list = data.subList(listStart = size - listSize, listEnd = size);
        } else if (size >= SIZE_MIN) { // 数量超过最小size全部展示
            this.list = data;
            listStart = 0;
            listEnd = size;
        } else {    // 数据量不足，补全后展示
            this.list = new ArrayList<>();
            for (int i = 0; i < SIZE_MIN - size; i++) list.add(new PriceNet());
            list.addAll(data);
            this.data = list;
            listStart = 0;
            listEnd = list.size();
        }
        listSize = list.size();
        index = list.size() - 1;
    }

    /**
     * 事件处理
     * @param event 事件
     * @return boolean
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            px = event.getX();
            py = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            dx = 0;
            lx = event.getX();
            longPressed = false;
            moving = false;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 2) { // 双指操作放大缩小
                if (this.doScaleList(event)) return true;
            } else {
                if (this.doLongPress(event)) return true;    // 长按处理
                if (longPressed) {  // 长按移动下标
                    if (this.doMoveIndex(event)) return true;
                } else if (this.doMoveList(event)) return true;  // 非长按滑动列表
            }
        }
        return true;
    }

    /**
     * 长按
     * @param event 事件
     * @return boolean
     */
    private boolean doLongPress(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        float mx = Math.abs(x - px);
        float my = Math.abs(y - py);
        if (mx < 10 && my < 10) {
            if (!moving && !longPressed) longPressed = event.getEventTime() - event.getDownTime() > 500L;
            if (longPressed) this.doMoveIndex(event);
            return true;
        } else {
            moving = !longPressed;
        }
        return false;
    }

    /**
     * 移动列表
     * @param event 事件
     * @return boolean
     */
    private boolean doMoveList(MotionEvent event) {
        float x = event.getX(), diff = x - lx;
        int size = data.size();
        if (diff < 0) {
            if (listEnd == size) return true;
            int unit = this.listSize / SIZE_MIN;
            if (unit == 0) unit = 1;
            listEnd += unit;
            if (listEnd > size) {
                unit = unit - (size - listEnd);
                listEnd = size;
            }
            listStart += unit;
        } else {
            if (listStart == 0) return true;
            int unit = -this.listSize / SIZE_MIN;
            if (unit == 0) unit = -1;
            listStart += unit;
            if (listStart < 0) {
                unit = unit - listStart;
                listStart = 0;
            }
            listEnd += unit;
        }
        lx = x;
        list = data.subList(listStart, listEnd);
        index = list.size() - 1;
        invalidate();
        return false;
    }

    /**
     * 列表放大与缩小
     * @param event 事件
     * @return boolean
     */
    private boolean doScaleList(MotionEvent event) {
        float dx = Math.abs(event.getX(0) - event.getX(1));
        float diff = this.dx - dx;
        if (diff > 10 || diff < -10) {
            this.dx = dx;
            if (diff < 0) { // 缩小
                if (listSize <= SIZE_MIN) return true;
                int unit = this.listSize / SIZE_MIN;
                listSize -= unit;
                listStart += unit;
            } else {
                if (listSize >= SIZE_MAX) return true;
                int unit = this.listSize / SIZE_MIN;
                listSize += unit;
                if (listStart == 0) listEnd += unit;
                else listStart -= unit;
                if (listStart < 0) listStart = 0;
                if (listEnd > data.size()) listEnd = data.size();
                if (listSize > data.size()) listSize = data.size();
            }
            list = data.subList(listStart, listEnd);
            index = list.size() - 1;
            invalidate();
        }
        return false;
    }

    /**
     * 移动下标
     * @param event 事件
     * @return boolean
     */
    private boolean doMoveIndex(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        if (x < price.sx - unitDate / 2f || x > price.ex + unitDate / 2f || y < price.sy || y > price.ey) return true;
        int index = Math.round((x - price.sx) / unitDate);
        if (this.index != index) {
            this.index = index;
            this.invalidate();
        }
        return false;
    }

    /**
     * 布局初始化
     * @param changed changed
     * @param left    left
     * @param top     top
     * @param right   right
     * @param bottom  bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.fixWidthAndHeight();
            this.fixPrice();
            this.fixRate();
            this.fixAmount();
            this.fixTexts();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.calculateParams(list);
        this.drawBase(canvas);
    }

    private void calculateParams(List<PriceNet> data) {
        this.calculateDateParams(data);
        this.calculatePriceParams(data);
        this.calculateRateParams(data);
        this.calculateAmountParams(data);
        this.fillQTexts();
    }

    @SuppressLint("DefaultLocale")
    private void calculateAmountParams(List<PriceNet> data) {
        SortedSet<Double> amounts = new TreeSet<>();
        for (PriceNet d : data) {
            this.addDoubles(amounts, d.getAmount());
        }
        double min = amounts.first() * 1000d, max = amounts.last() * 1000d, count = 2d;
        if (max == min) { // 最大值与最小值相等，特殊处理
            max = max * 2;
            min = 0;
        }
        double v = Math.ceil((max - min) / count), first = Math.floor(min), last = first + v * (count - 1);
        if (last < max) count = 3d;
        amountTexts = new ArrayList<>();
        for (int i = 0; i < count; i++) amountTexts.add(TextUtil.amount((first + v * i) / 1000d));
        this.minAmount = first / 1000d;
        Double maxPrice = (first + (count - 1) * v) / 1000d;
        this.intervalAmount = (int) ((amount.ey - amount.sy) / (double) (amountTexts.size() - 1) + 0.5d);
        this.unitAmount = (float) ((amount.ey - amount.sy) / (maxPrice - this.minAmount));
    }

    @SuppressLint("DefaultLocale")
    private void calculateRateParams(List<PriceNet> data) {
        SortedSet<Double> rates = new TreeSet<>();
        for (PriceNet d : data) {
            this.addDoubles(rates, getAbs(d.getRateDiff()));
            this.addDoubles(rates, getAbs(d.getRateNet()));
            this.addDoubles(rates, getAbs(d.getRatePrice()));
        }
        double max = rates.last() * 1000d, first = Math.floor(-max), last = Math.ceil(max);
        rateTexts = new ArrayList<>();
        rateTexts.add(TextUtil.hundred(first / 1000d));
        rateTexts.add(TextUtil.hundred(0d));
        rateTexts.add(TextUtil.hundred(last / 1000d));
        this.minRate = first / 1000d;
        Double maxRate = last / 1000d;
        this.intervalRate = (int) ((rate.ey - rate.sy) / (double) (rateTexts.size() - 1) + 0.5d);
        this.unitRate = (float) ((rate.ey - rate.sy) / (maxRate - this.minRate));
    }

    private Double getAbs(Double rate) {
        if (rate == null) return null;
        return Math.abs(rate);
    }

    @SuppressLint("DefaultLocale")
    private void calculatePriceParams(List<PriceNet> data) {
        SortedSet<Double> prices = new TreeSet<>();
        for (PriceNet d : data) {
            this.addDoubles(prices, d.getPriceLatest());
            this.addDoubles(prices, d.getNetLatest());
            this.addDoubles(prices, d.getPriceHigh());
            this.addDoubles(prices, d.getPriceLow());
        }
        double min = prices.first() * 1000d, max = prices.last() * 1000d, count = 4d;
        if (max == min) { // 最大值与最小值相等，特殊处理
            max = max * 2;
            min = 0;
        }
        double v = Math.ceil((max - min) / count), first = Math.floor(min), last = first + v * (count - 1);
        if (last < max) count = 5d;
        priceTexts = new ArrayList<>();
        for (int i = 0; i < count; i++) priceTexts.add(String.format("%.4f", (first + v * i) / 1000d));
        this.minPrice = first / 1000d;
        Double maxPrice = (first + (count - 1) * v) / 1000d;
        this.intervalPrice = (int) ((price.ey - price.sy) / (double) (priceTexts.size() - 1) + 0.5d);
        this.unitPrice = (float) ((price.ey - price.sy) / (maxPrice - this.minPrice));
    }

    private void calculateDateParams(List<PriceNet> data) {
        List<String> dates = data.stream().map(PriceNet::getDate).collect(Collectors.toList());
        dateTexts = new ArrayList<>();
        int size = dates.size();
        int middle = size % 2 == 0 ? size / 2 : size / 2 + 1;
        if (middle > size - 1) middle = size - 1;
        dateTexts.addAll(Arrays.asList(dates.get(0), dates.get(middle), dates.get(size - 1)));
        this.intervalDate = (int) ((price.ex - price.sx) / (double) (dateTexts.size() - 1) + 0.5d);
        this.unitDate = (float) (price.ex - price.sx) / (data.size() - 1);
    }

    private void addDoubles(SortedSet<Double> doubles, Double d) {
        if (d != null) doubles.add(d);
    }

    private void fillQTexts() {
        PriceNet d = list.get(index);
        qs[0].c = TextUtil.text(d.getDate());
        qs[1].c = TextUtil.net(d.getPriceHigh());
        qs[2].c = TextUtil.net(d.getPriceLow());
        qs[3].c = TextUtil.net(d.getPriceLatest());
        qs[4].c = TextUtil.net(d.getNetLatest());
        qs[5].c = TextUtil.hundred(d.getRateDiff());
        qs[6].c = TextUtil.amount(d.getAmount());
    }

    /**
     * 宽高固定4：3
     */
    private void fixWidthAndHeight() {
        int width = this.getWidth();
        int height = this.getHeight();
        if ((float) width / (float) height > 2f) {   // 宽高比保持4：3
            all.sy = 0;
            all.ey = height;
            all.sx = (int) (width / 2f - height / 2f * 2f + 0.5f);
            all.ex = (int) (width / 2f + height / 2f * 2f + 0.5f);
        } else {
            all.sx = 0;
            all.ex = width;
            all.sy = (int) (height / 2f - width / 8f * 3f + 0.5f);
            all.ey = (int) (height / 2f + width / 8f * 3f + 0.5f);
        }
    }

    /**
     * 坐标轴横纵坐标固定
     */
    private void fixPrice() {
        price.sx = (int) (all.sx / 20f * 17f + all.ex / 20f * 3f + 0.5f);
        price.ex = (int) (all.sx / 20f * 1f + all.ex / 20f * 19f + 0.5f);
        price.sy = (int) (all.sy / 20f * 18f + all.ey / 20f * 2f + 0.5f);
        price.ey = (int) (all.sy / 20f * 7f + all.ey / 20f * 13f + 0.5f);
    }

    /**
     * 坐标轴横纵坐标固定
     */
    private void fixRate() {
        rate.sx = (int) (all.sx / 20f * 17f + all.ex / 20f * 3f + 0.5f);
        rate.ex = (int) (all.sx / 20f * 1f + all.ex / 20f * 19f + 0.5f);
        rate.sy = (int) (all.sy / 20f * 7f + all.ey / 20f * 13f + 0.5f);
        rate.ey = (int) (all.sy / 20f * 4f + all.ey / 20f * 16f + 0.5f);
    }

    /**
     * 坐标轴横纵坐标固定
     */
    private void fixAmount() {
        amount.sx = (int) (all.sx / 20f * 17f + all.ex / 20f * 3f + 0.5f);
        amount.ex = (int) (all.sx / 20f * 1f + all.ex / 20f * 19f + 0.5f);
        amount.sy = (int) (all.sy / 20f * 4f + all.ey / 20f * 16f + 0.5f);
        amount.ey = (int) (all.sy / 20f * 1f + all.ey / 20f * 19f + 0.5f);
    }

    private void fixTexts() {
        int x1 = (int) (all.sx * 19f / 20f + all.ex * 1f / 20f + 0.5f);
        int x2 = (int) (all.sx * 16f / 20f + all.ex * 4f / 20f + 0.5f);
        int x3 = (int) (all.sx * 14f / 20f + all.ex * 6f / 20f + 0.5f);
        int x4 = (int) (all.sx * 11f / 20f + all.ex * 9f / 20f + 0.5f);
        int x5 = (int) (all.sx * 9f / 20f + all.ex * 11f / 20f + 0.5f);
        int x6 = (int) (all.sx * 6f / 20f + all.ex * 14f / 20f + 0.5f);
        int x7 = (int) (all.sx * 4f / 20f + all.ex * 16f / 20f + 0.5f);
        int x8 = (int) (all.sx * 1f / 20f + all.ex * 19f / 20f + 0.5f);
        int y1 = (int) (all.sy * 6f / 8f + price.sy * 2f / 8f + 0.5f);
        int y2 = (int) (all.sy * 2f / 8f + price.sy * 6f / 8f + 0.5f);
        this.fixQ(qs[0], x1, x2, y1);
        this.fixQ(qs[1], x3, x4, y1);
        this.fixQ(qs[2], x5, x6, y1);
        this.fixQ(qs[3], x7, x8, y1);
        this.fixQ(qs[4], x1, x2, y2);
        this.fixQ(qs[5], x3, x4, y2);
        this.fixQ(qs[6], x5, x6, y2);
    }

    private void fixQ(Q q, int x1, int x2, int y) {
        q.sx = x1;
        q.ex = x2;
        q.y = y;
    }

    /**
     * 画基本内容（坐标轴）
     * @param canvas canvas
     */
    private void drawBase(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0f);
        // x轴线
        canvas.drawLine(price.sx, price.sy, price.ex, price.sy, paint);
        // x轴线
        canvas.drawLine(price.sx, price.ey, price.ex, price.ey, paint);
        // x轴线
        canvas.drawLine(price.sx, rate.ey, price.ex, rate.ey, paint);
        // x轴线
        canvas.drawLine(price.sx, amount.ey, price.ex, amount.ey, paint);
        // y轴线
        canvas.drawLine(price.sx, price.sy, price.sx, amount.ey, paint);
        // y轴线
        canvas.drawLine(price.ex, price.sy, price.ex, amount.ey, paint);
        // x轴刻度
        this.drawXIndex(canvas);
        // y轴刻度平线
        this.drawPriceIndexLine(canvas);
        this.drawRateIndexLine(canvas);
        this.drawAmountIndexLine(canvas);
        // y轴刻度
        this.drawPriceIndex(canvas);
        this.drawRateIndex(canvas);
        this.drawAmountIndex(canvas);
        this.drawXIndexLine(canvas);
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(25f);
        // 顶端指标数据
        for (Q q : qs) this.doDrawText(canvas, q);
        paint.setStyle(Paint.Style.STROKE);
        this.doDrawPriceLine(canvas, COLORS[2], PriceNet::getPriceLatest);
        this.doDrawPriceLine(canvas, COLORS[3], PriceNet::getNetLatest);
        this.doDrawPriceLine(canvas, COLORS[0], PriceNet::getPriceHigh);
        this.doDrawPriceLine(canvas, COLORS[1], PriceNet::getPriceLow);
        this.doDrawRateLine(canvas, COLORS[1], PriceNet::getRateDiff);
        this.doDrawRateLine(canvas, COLORS[2], PriceNet::getRatePrice);
        this.doDrawRateLine(canvas, COLORS[3], PriceNet::getRateNet);
        this.doDrawAmountLine(canvas, PriceNet::getAmount);
    }

    private void drawXIndexLine(Canvas canvas) {
        paint.setColor(Color.DKGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        float x = price.sx + index * unitDate;
        canvas.drawLine(x, price.sy, x, amount.ey, paint);
        paint.setPathEffect(null);
    }

    private void drawPriceIndex(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        for (int i = 0; i < priceTexts.size(); i++) {
            String text = priceTexts.get(i);
            float x = price.sx - this.getTextWidth(paint, text) - 20;
            float y = price.ey - i * intervalPrice + this.getTextHeight(paint, text) / 2f;
            if (i == 0) y = price.ey;
            canvas.drawText(text, x, y, paint);
        }
    }

    private void drawRateIndex(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        for (int i = 0; i < rateTexts.size(); i++) {
            String text = rateTexts.get(i);
            float x = rate.sx - this.getTextWidth(paint, text) - 20;
            float y = rate.ey - i * intervalRate + this.getTextHeight(paint, text) / 2f;
            if (i == 0) y = rate.ey - i * intervalRate - this.getTextHeight(paint, text) / 2f;
            if (i == rateTexts.size() - 1) y = rate.ey - i * intervalRate + this.getTextHeight(paint, text);
            canvas.drawText(text, x, y, paint);
        }
    }

    private void drawAmountIndex(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        for (int i = 0; i < amountTexts.size(); i++) {
            String text = amountTexts.get(i);
            float x = amount.sx - this.getTextWidth(paint, text) - 20;
            float y = amount.ey - i * intervalAmount + this.getTextHeight(paint, text) / 2f;
            if (i == amountTexts.size() - 1) y = amount.ey - i * intervalAmount + this.getTextHeight(paint, text);
            canvas.drawText(text, x, y, paint);
        }
    }

    private void drawXIndex(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        // x轴刻度
        for (int i = 0; i < dateTexts.size(); i++) {
            // x轴上的文字
            String text = dateTexts.get(i);
            if (text == null) continue;
            float x = price.sx + i * intervalDate - this.getTextWidth(paint, text) / 2f;
            float y = amount.ey + this.getTextHeight(paint, text) / 2f * 3f + 15;
            canvas.drawText(text, x, y, paint);
        }
    }

    private void drawPriceIndexLine(Canvas canvas) {
        paint.setColor(Color.LTGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        for (int i = 1; i < priceTexts.size() - 1; i++) {
            int indexY = price.ey - i * intervalPrice;
            canvas.drawLine(price.sx, indexY, price.ex, indexY, paint);
        }
        paint.setPathEffect(null);
    }

    private void drawRateIndexLine(Canvas canvas) {
        paint.setColor(Color.LTGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        for (int i = 1; i < rateTexts.size() - 1; i++) {
            int indexY = rate.ey - i * intervalRate;
            canvas.drawLine(rate.sx, indexY, rate.ex, indexY, paint);
        }
        paint.setPathEffect(null);
    }

    private void drawAmountIndexLine(Canvas canvas) {
        paint.setColor(Color.LTGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        for (int i = 1; i < amountTexts.size() - 1; i++) {
            int indexY = amount.ey - i * intervalAmount;
            canvas.drawLine(amount.sx, indexY, amount.ex, indexY, paint);
        }
        paint.setPathEffect(null);
    }

    private void doDrawPriceLine(Canvas canvas, int color, Function<PriceNet, Double> get) {
        // 折线
        Path path = new Path();
        boolean moved = false;
        for (int i = 0; i < list.size(); i++) {
            PriceNet k = list.get(i);
            float x = i * unitDate + price.sx;
            Double d = get.apply(k);
            if (d == null) continue;
            float y = (float) ((minPrice - d) * unitPrice + price.ey);
            if (!moved) {   // 先移动到第一个点的位置
                moved = true;
                path.moveTo(x, y);
            } else {    // 然后点与点连线
                path.lineTo(x, y);
            }
        }
        paint.setColor(color);
        canvas.drawPath(path, paint);
    }

    private void doDrawRateLine(Canvas canvas, int color, Function<PriceNet, Double> get) {
        // 折线
        Path path = new Path();
        boolean moved = false;
        for (int i = 0; i < list.size(); i++) {
            PriceNet k = list.get(i);
            float x = i * unitDate + rate.sx;
            Double d = get.apply(k);
            if (d == null) continue;
            float y = (float) ((minRate - d) * unitRate + rate.ey);
            if (!moved) {   // 先移动到第一个点的位置
                moved = true;
                path.moveTo(x, y);
            } else {    // 然后点与点连线
                path.lineTo(x, y);
            }
        }
        paint.setColor(color);
        canvas.drawPath(path, paint);
    }

    private void doDrawAmountLine(Canvas canvas, Function<PriceNet, Double> get) {
        // 折线
        Path path = new Path();
        boolean moved = false;
        for (int i = 0; i < list.size(); i++) {
            PriceNet k = list.get(i);
            float x = i * unitDate + amount.sx;
            Double d = get.apply(k);
            if (d == null) continue;
            float y = (float) ((minAmount - d) * unitAmount + amount.ey);
            if (!moved) {   // 先移动到第一个点的位置
                moved = true;
                path.moveTo(x, y);
            } else {    // 然后点与点连线
                path.lineTo(x, y);
            }
        }
        paint.setColor(Color.RED);
        canvas.drawPath(path, paint);
    }

    private void doDrawText(Canvas canvas, Q q) {
        float y = q.y + this.getTextHeight(paint, q.h) / 2f;
        canvas.drawText(q.h, q.sx, y, paint);
        canvas.drawText(q.c, q.ex - this.getTextWidth(paint, q.c), y, paint);
    }

    /**
     * 获取文字的宽度
     * @param paint paint
     * @param text  text
     * @return int
     */
    private int getTextWidth(Paint paint, String text) {
        return (int) paint.measureText(text);
    }

    /**
     * 获取文字的高度
     * @param paint paint
     * @param text  text
     * @return int
     */
    private int getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    /**
     * 位置
     */
    private static class XY {
        private int sx;
        private int sy;
        private int ex;
        private int ey;
    }

    /**
     * 指标
     */
    private static class Q {
        private final String h;
        private String c;
        private int sx;
        private int ex;
        private int y;

        private Q(String h) {
            this.h = h;
        }
    }
}
