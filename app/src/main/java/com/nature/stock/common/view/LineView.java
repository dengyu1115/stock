package com.nature.stock.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.nature.stock.common.model.Line;
import com.nature.stock.common.model.LineDef;
import com.nature.stock.common.util.TextUtil;

import java.util.*;
import java.util.stream.Collectors;

/**
 * K线图
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 22:53
 */
public class LineView extends View {

    private static final int SIZE_DEFAULT = 90, SIZE_MIN = 30, SIZE_MAX = 1800, MOVEMENT = 30;
    private static final String DELIMITER = ":";
    private final int[] tx = new int[10];
    /**
     * 位置坐标
     */
    private final XY all = new XY(), price = new XY();
    /**
     * 画笔
     */
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<String> dates, list, dateTexts, priceTexts;
    private Map<String, Line> map;
    private int intervalDate, intervalPrice, index, listSize = SIZE_DEFAULT, listStart, listEnd, titleY;
    private float unitDate, unitPrice, dx, px, py, lx;
    private boolean longPressed, moving;
    private String dateStart;
    private List<Q> qs = Arrays.asList(new Q("日期", null, "初始", Color.BLACK));
    private Double minPrice;

    public LineView(Context context) {
        super(context);
        this.resetParams();
    }

    /**
     * 填充数据
     * @param data 数据
     */
    public void data(List<LineDef> data) {
        if (data == null) throw new RuntimeException("数据集不可为null");
        TreeSet<String> set = new TreeSet<>();
        qs = new ArrayList<>();
        qs.add(new Q("日期", null, "初始", Color.BLACK));
        for (LineDef def : data) {
            set.addAll(def.getList().stream().map(Line::getDate).collect(Collectors.toList()));
            qs.add(new Q(def.getTitle(), "增长", "总增长", def.getColor()));
        }
        this.map = new TreeMap<>();
        for (LineDef def : data) {
            String title = def.getTitle();
            List<Line> list = def.getList();
            for (Line line : list) {
                this.map.put(String.join(DELIMITER, line.getDate(), title), line);
            }
        }
        if (set.isEmpty()) {
            this.resetParams();
            this.invalidate();
            return;
        }
        dateStart = set.first();
        this.dates = new ArrayList<>(set);
        int size = this.dates.size();
        if (listSize < size) {  // 数量超出的截取尾部展示
            this.list = dates.subList(listStart = size - listSize, listEnd = size);
        } else if (size >= SIZE_MIN) { // 数量超过最小size全部展示
            this.list = dates;
            listStart = 0;
            listEnd = size;
        } else {    // 数据量不足，补全后展示
            this.list = new ArrayList<>();
            for (int i = 0; i < SIZE_MIN - size; i++) this.list.add(null);
            this.list.addAll(dates);
            listStart = 0;
            listEnd = dates.size();
        }
        listSize = this.list.size();
        index = this.list.size() - 1;
        this.invalidate();
    }

    private void resetParams() {
        listStart = 0;
        listEnd = 0;
        listSize = SIZE_DEFAULT;
        index = 0;
        dates = new ArrayList<>();
        list = new ArrayList<>();
        dateTexts = new ArrayList<>();
        priceTexts = new ArrayList<>();
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
        if (mx < MOVEMENT && my < MOVEMENT) {
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
        lx = x;
        int size = dates.size();
        if (diff < 0) {
            if (listEnd == size) return true;
            int unit = this.listSize / SIZE_MIN;
            if (unit == 0) unit = 1;
            listStart += unit;
            if (listStart + listSize > size) listStart = size - listSize;
        } else {
            if (listStart == 0) return true;
            int unit = this.listSize / SIZE_MIN;
            if (unit == 0) unit = 1;
            listStart -= unit;
            if (listStart < 0) listStart = 0;
        }
        listEnd = listStart + listSize;
        list = dates.subList(listStart, listEnd);
        if (size > 0) index = list.size() - 1;
        this.invalidate();
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
        if (diff > -MOVEMENT && diff < MOVEMENT) {
            return false;
        }
        this.dx = dx;
        int size = dates.size();
        listSize = list.size();
        if (diff < 0) { // 缩小
            if (listSize <= SIZE_MIN) return true;
            int unit = this.listSize / SIZE_MIN;
            listSize -= unit;
            if (listSize < SIZE_MIN) listSize = SIZE_MIN;
            listStart += unit;
            if (listStart + listSize > size) listStart = size - listStart;
        } else {
            if (listSize >= SIZE_MAX) return true;
            int unit = this.listSize / SIZE_MIN;
            listSize += unit;
            if (listSize > size) listSize = size;
            if (listSize > SIZE_MAX) listSize = SIZE_MAX;
            listStart -= unit;
            if (listStart < 0) listStart = 0;
        }
        listEnd = listStart + listSize;
        list = dates.subList(listStart, listEnd);
        if (size > 0) index = list.size() - 1;
        this.invalidate();
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
            this.fixTexts();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.calculateParams(list);
        this.drawBase(canvas);
    }

    private void calculateParams(List<String> dates) {
        this.calculateDateParams(dates);
        this.calculatePriceParams(dates);
        this.fillQTexts();
    }

    private Double getRateTotal(String date, String title) {
        Line line = this.map.get(String.join(DELIMITER, date, title));
        return line == null ? null : line.getRateTotal();

    }

    @SuppressLint("DefaultLocale")
    private void calculatePriceParams(List<String> dates) {
        if (dates == null || dates.isEmpty()) return;
        SortedSet<Double> prices = new TreeSet<>();
        for (String d : dates) {
            for (int i = 1; i < qs.size(); i++) {
                this.addDoubles(prices, this.getRateTotal(d, qs.get(i).h));
            }
        }
        double min = prices.first() * 1000d, max = prices.last() * 1000d, count = 4d;
        if (max == min) { // 最大值与最小值相等，特殊处理
            max = max * 2;
            min = 0;
        }
        double v = Math.ceil((max - min) / count), first = Math.floor(min), last = first + v * (count - 1);
        if (last < max) count = 5d;
        priceTexts = new ArrayList<>();
        for (int i = 0; i < count; i++) priceTexts.add(TextUtil.hundred((first + v * i) / 1000d));
        this.minPrice = first / 1000d;
        Double maxPrice = (first + (count - 1) * v) / 1000d;
        this.intervalPrice = (int) ((price.ey - price.sy) / (double) (priceTexts.size() - 1) + 0.5d);
        this.unitPrice = (float) ((price.ey - price.sy) / (maxPrice - this.minPrice));
    }

    private void calculateDateParams(List<String> dates) {
        if (dates == null || dates.isEmpty()) return;
        dateTexts = new ArrayList<>();
        int size = dates.size();
        int middle = size % 2 == 0 ? size / 2 : size / 2 + 1;
        if (middle > size - 1) middle = size - 1;
        dateTexts.addAll(Arrays.asList(dates.get(0), dates.get(middle), dates.get(size - 1)));
        this.intervalDate = (int) ((price.ex - price.sx) / (double) (dateTexts.size() - 1) + 0.5d);
        this.unitDate = (float) (price.ex - price.sx) / (dates.size() - 1);
    }

    private void addDoubles(SortedSet<Double> doubles, Double d) {
        if (d != null) doubles.add(d);
    }

    private void fillQTexts() {
        String date = list.size() > index ? list.get(index) : null;
        Q q1 = qs.get(0);
        q1.hc = TextUtil.text(date);
        q1.fc = TextUtil.text(dateStart);
        for (int i = 1; i < qs.size(); i++) {
            Q q = qs.get(i);
            Line line = map.get(String.join(DELIMITER, date, q.h));
            if (line == null) {
                q.hc = "";
                q.bc = "";
                q.fc = "";
            } else {
                q.hc = TextUtil.net(line.getPrice());
                q.bc = TextUtil.hundred(line.getRate());
                q.fc = TextUtil.hundred(line.getRateTotal());
            }
        }
    }

    /**
     * 宽高固定4：3
     */
    private void fixWidthAndHeight() {
        int width = this.getWidth();
        int height = this.getHeight();
        if ((float) width > (float) height) {   // 宽高比保持4：3
            all.sy = 0;
            all.ey = height;
            all.sx = 0;
            all.ex = width;
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
        price.sx = (int) (all.sx / 20f * 18.5f + all.ex / 20f * 1.5f + 0.5f);
        price.ex = (int) (all.sx / 20f * 1f + all.ex / 20f * 19f + 0.5f);
        price.sy = (int) (all.sy / 20f * 17f + all.ey / 20f * 3f + 0.5f);
        price.ey = (int) (all.sy / 20f * 2f + all.ey / 20f * 18f + 0.5f);
    }

    private void fixTexts() {
        tx[0] = (int) (all.sx * 19.50f / 20f + price.ex * 0.50f / 20f + 0.5f);
        tx[1] = (int) (all.sx * 17.00f / 20f + price.ex * 3.00f / 20f + 0.5f);
        tx[2] = (int) (all.sx * 16.80f / 20f + price.ex * 3.20f / 20f + 0.5f);
        tx[3] = (int) (all.sx * 12.75f / 20f + price.ex * 7.25f / 20f + 0.5f);
        tx[4] = (int) (all.sx * 12.55f / 20f + price.ex * 7.45f / 20f + 0.5f);
        tx[5] = (int) (all.sx * 8.50f / 20f + price.ex * 11.50f / 20f + 0.5f);
        tx[6] = (int) (all.sx * 8.30f / 20f + price.ex * 11.70f / 20f + 0.5f);
        tx[7] = (int) (all.sx * 4.25f / 20f + price.ex * 15.75f / 20f + 0.5f);
        tx[8] = (int) (all.sx * 4.05f / 20f + price.ex * 15.95f / 20f + 0.5f);
        tx[9] = (int) (all.sx * 0.00f / 20f + price.ex * 20.00f / 20f + 0.5f);
        titleY = (int) (all.sy * 3f / 4f + price.sy * 1f / 4f + 0.5f);

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
        // y轴线
        canvas.drawLine(price.sx, price.sy, price.sx, price.ey, paint);
        // y轴线
        canvas.drawLine(price.ex, price.sy, price.ex, price.ey, paint);
        // x轴刻度
        this.drawXIndex(canvas);
        // y轴刻度平线
        this.drawPriceIndexLine(canvas);
        // y轴刻度
        this.drawPriceIndex(canvas);
        this.drawXIndexLine(canvas);
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(25f);
        // 顶端指标数据
        for (int i = 0; i < qs.size(); i++) this.doDrawText(canvas, i);
        paint.setStyle(Paint.Style.STROKE);
        for (int i = 1; i < qs.size(); i++) {
            Q q = qs.get(i);
            this.doDrawPriceLine(canvas, q.cr, q.h);
        }
    }

    private void drawXIndexLine(Canvas canvas) {
        paint.setColor(Color.DKGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        float x = price.sx + index * unitDate;
        canvas.drawLine(x, price.sy, x, price.ey, paint);
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

    private void drawXIndex(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        // x轴刻度
        for (int i = 0; i < dateTexts.size(); i++) {
            // x轴上的文字
            String text = dateTexts.get(i);
            if (text == null) continue;
            float x = price.sx + i * intervalDate - this.getTextWidth(paint, text) / 2f;
            float y = price.ey + this.getTextHeight(paint, text) / 2f * 3f + 15;
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

    private void doDrawPriceLine(Canvas canvas, int color, String title) {
        // 折线
        Path path = new Path();
        boolean moved = false;
        for (int i = 0; i < list.size(); i++) {
            String date = list.get(i);
            float x = i * unitDate + price.sx;
            Line line = this.map.get(String.join(DELIMITER, date, title));
            if (line == null) continue;
            Double d = line.getRateTotal();
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

    private void doDrawText(Canvas canvas, int i) {
        Q q = qs.get(i);
        int th = this.getTextHeight(paint, q.h);
        float y1 = titleY + th / 2f;
        float y2 = titleY + th / 2f * 4;
        float y3 = titleY + th / 2f * 7;
        paint.setColor(Color.BLACK);
        int sx = this.tx[i * 2], ex = this.tx[i * 2 + 1];
        canvas.drawText(TextUtil.text(q.h), sx, y1, paint);
        paint.setColor(q.cr);
        canvas.drawText(TextUtil.text(q.hc), ex - this.getTextWidth(paint, TextUtil.text(q.hc)), y1, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(TextUtil.text(q.b), sx, y2, paint);
        paint.setColor(q.cr);
        canvas.drawText(TextUtil.text(q.bc), ex - this.getTextWidth(paint, TextUtil.text(q.bc)), y2, paint);
        paint.setColor(Color.BLACK);
        canvas.drawText(TextUtil.text(q.f), sx, y3, paint);
        paint.setColor(q.cr);
        canvas.drawText(TextUtil.text(q.fc), ex - this.getTextWidth(paint, TextUtil.text(q.fc)), y3, paint);
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
        private final String b;
        private final String f;
        private final int cr;
        private String hc;
        private String bc;
        private String fc;

        private Q(String h, String b, String f, int cr) {
            this.h = h;
            this.b = b;
            this.f = f;
            this.cr = cr;
        }
    }
}
