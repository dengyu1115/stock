package com.nature.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import com.nature.common.util.TextUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LView<T> extends View {

    private int sizeDefault = 90, sizeMin = 30, sizeMax = 1800;
    private int[] colors;
    private List<List<Q<T>>> qs;
    private List<C<T>> rs;
    private Function<T, String> xText;
    private T empty;

    private final XY all = new XY(), rect = new XY();

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<T> data, list;
    private T curr;
    private List<String> dateTexts;
    private int intervalDate, index, listSize = sizeDefault, listStart, listEnd;
    private float unitDate, px, lx, ly;
    private boolean longPressed, moving;

    public LView(Context context) {
        super(context);
    }

    public void sizeDefault(int sizeDefault, int sizeMin, int sizeMax) {
        this.sizeDefault = sizeDefault;
        this.sizeMin = sizeMin;
        this.sizeMax = sizeMax;
    }

    public void init(int[] colors, List<List<Q<T>>> qs, List<C<T>> rs, Function<T, String> xText, T empty) {
        this.colors = colors;
        this.qs = qs;
        this.rs = rs;
        this.xText = xText;
        this.empty = empty;
    }

    public void data(List<T> data) {
        this.data = data;
        if (data == null) {
            throw new RuntimeException("data is null");
        }
        int size = data.size();
        if (listSize < size) {  // 数量超出的截取尾部展示
            this.list = data.subList(listStart = size - listSize, listEnd = size);
        } else if (size >= sizeMin) { // 数量超过最小size全部展示
            this.list = data;
            listStart = 0;
            listEnd = size;
        } else {    // 数据量不足，补全后展示
            this.list = new ArrayList<>();
            for (int i = 0; i < sizeMin - size; i++) {
                list.add(this.empty);
            }
            list.addAll(data);
            this.data = list;
            listStart = 0;
            listEnd = list.size();
        }
        this.listSize = list.size();
        this.index = list.size() - 1;
        this.curr = this.list.get(this.index);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            ly = event.getY();
            lx = event.getX();
        } else if (action == MotionEvent.ACTION_POINTER_DOWN) {
            px = event.getX();
            if (px > lx) {
                float x = lx;
                lx = px;
                px = x;
            }
        } else if (action == MotionEvent.ACTION_UP) {
            longPressed = false;
            moving = false;
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (event.getPointerCount() == 2) { // 双指操作放大缩小
                if (this.doScaleList(event)) {
                    return true;
                }
            } else {
                if (this.doLongPress(event)) {
                    return true;    // 长按处理
                }
                if (longPressed) {  // 长按移动下标
                    if (this.doMoveIndex(event)) {
                        return true;
                    }
                } else if (this.doMoveList(event)) {
                    return true;  // 非长按滑动列表
                }
            }
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            this.fixAll();
            this.fixRect();
            this.fixTexts();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        this.calculateParams(list);
        this.drawBase(canvas);
    }

    private boolean doLongPress(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        float mx = Math.abs(x - lx);
        float my = Math.abs(y - ly);
        if (mx < 10 && my < 10) {
            if (!moving && !longPressed) {
                longPressed = (event.getEventTime() - event.getDownTime()) > 500L;
            }
            if (longPressed) {
                this.doMoveIndex(event);
            }
            return true;
        } else {
            moving = !longPressed;
        }
        return false;
    }

    private boolean doMoveList(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        if (x < rect.sx - unitDate / 2f || x > rect.ex + unitDate / 2f || y < rect.sy || y > rect.ey) {
            return true;
        }
        float diff = x - this.lx;
        if (diff == 0) {
            return true;
        }
        this.lx = x;
        int size = this.data.size();
        if (diff < 0) {
            if (this.listEnd == size) {
                return true;
            }
            int unit = this.moveUnit(-diff);
            if (unit == 0) {
                return true;
            }
            this.listEnd += unit;
            if (this.listEnd > size) {
                unit -= (this.listEnd - size);
                this.listEnd = size;
            }
            this.listStart += unit;
        } else {
            if (this.listStart == 0) {
                return true;
            }
            int unit = this.moveUnit(diff);
            if (unit == 0) {
                return true;
            }
            this.listStart -= unit;
            if (this.listStart < 0) {
                unit += this.listStart;
                this.listStart = 0;
            }
            this.listEnd -= unit;
        }
        this.list = this.data.subList(this.listStart, this.listEnd);
        this.index = this.list.size() - 1;
        this.curr = this.list.get(this.index);
        this.invalidate();
        return false;
    }

    private boolean doScaleList(MotionEvent event) {
        float x1 = event.getX(0), y1 = event.getY(0),
                x2 = event.getX(1), y2 = event.getY(1);
        if (x1 > x2) {
            float temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        if (this.doScaleLeft(x1, y1) && this.doScaleRight(x2, y2)) {
            return true;
        }
        this.list = this.data.subList(this.listStart, this.listEnd);
        this.index = this.list.size() - 1;
        this.curr = this.list.get(this.index);
        this.invalidate();
        return false;
    }

    private boolean doScaleLeft(float x, float y) {
        if (x < rect.sx - unitDate / 2f || x > rect.ex + unitDate / 2f || y < rect.sy || y > rect.ey) {
            return true;
        }
        float diff = x - px;
        if (diff == 0) {
            return true;
        }
        this.px = x;
        int size = this.data.size();
        if (diff < 0) {
            if (this.listStart >= (size - sizeMin)) {
                return true;
            }
            int unit = this.moveUnit(-diff);
            if (unit == 0) {
                return true;
            }
            this.listStart += unit;
            if (this.listStart > (size - sizeMin)) {
                unit -= (this.listStart - size + sizeMin);
                this.listStart = size - sizeMin;
            }
            this.listSize -= unit;
        } else {
            if (this.listStart == 0) {
                return true;
            }
            int unit = this.moveUnit(diff);
            if (unit == 0) {
                return true;
            }
            this.listStart -= unit;
            if (this.listStart < 0) {
                unit += this.listStart;
                this.listStart = 0;
            }
            this.listSize += unit;
        }
        return false;
    }


    private boolean doScaleRight(float x, float y) {
        if (x < rect.sx - unitDate / 2f || x > rect.ex + unitDate / 2f || y < rect.sy || y > rect.ey) {
            return true;
        }
        float diff = x - lx;
        if (diff == 0) {
            return true;
        }
        this.lx = x;
        int size = this.data.size();
        if (diff < 0) {
            if (this.listEnd >= size) {
                return true;
            }
            int unit = this.moveUnit(-diff);
            if (unit == 0) {
                return true;
            }
            this.listEnd += unit;
            if (this.listEnd > size) {
                unit -= (size - this.listEnd);
                this.listEnd = size;
            }
            this.listSize += unit;
        } else {
            if (this.listEnd <= this.listStart + sizeMin) {
                return true;
            }
            int unit = this.moveUnit(diff);
            if (unit == 0) {
                return true;
            }
            this.listEnd -= unit;
            if (this.listEnd < this.listStart + sizeMin) {
                unit -= (this.listStart + sizeMin - this.listEnd);
                this.listEnd = this.listStart + sizeMin;
            }
            this.listSize -= unit;
        }
        return false;
    }

    private boolean doMoveIndex(MotionEvent event) {
        float x = event.getX(), y = event.getY();
        if (x < rect.sx - unitDate / 2f || x > rect.ex + unitDate / 2f || y < rect.sy || y > rect.ey) {
            return true;
        }
        int index = Math.round((x - rect.sx) / unitDate);
        if (this.index == index) {
            return true;
        }
        this.index = index;
        this.curr = this.list.get(this.index);
        this.invalidate();
        return false;
    }

    private int moveUnit(float diff) {
        return (int) (this.listSize / (float) (this.rect.ex - this.rect.sx) * diff + 0.5f);
    }

    private void calculateParams(List<T> data) {
        this.calculateDateParams(data);
        for (C<T> r : rs) {
            r.calculate(data);
        }
    }


    private void calculateDateParams(List<T> data) {
        List<String> dates = data.stream().map(this.xText).collect(Collectors.toList());
        this.dateTexts = new ArrayList<>();
        int size = dates.size();
        int middle = size % 2 == 0 ? size / 2 : size / 2 + 1;
        if (middle > size - 1) middle = size - 1;
        this.dateTexts.addAll(Arrays.asList(dates.get(0), dates.get(middle), dates.get(size - 1)));
        this.intervalDate = (int) ((rect.ex - rect.sx) / (double) (dateTexts.size() - 1) + 0.5d);
        this.unitDate = (float) (rect.ex - rect.sx) / (data.size() - 1);
    }

    private void fixAll() {
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

    private void fixRect() {
        rect.sx = (int) (all.sx / 20f * 18f + all.ex / 20f * 2f + 0.5f);
        rect.ex = (int) (all.sx / 20f + all.ex / 20f * 19f + 0.5f);
        rect.sy = (int) (all.sy / 20f * 17f + all.ey / 20f * 3f + 0.5f);
        rect.ey = (int) (all.sy / 20f + all.ey / 20f * 19f + 0.5f);
        double total = rs.stream().mapToDouble(d -> d.weight).sum();
        int unit = (int) ((rect.ey - rect.sy) / total + 0.5d);
        C<T> r = rs.get(0);
        int sy = rect.sy, ey = sy;
        r.fix(sy, ey);
        for (int i = 0; i < rs.size() - 1; i++) {
            r = rs.get(i);
            ey = ey + r.weight * unit;
            r.fix(sy, ey);
            sy = ey;
        }
        ey = rect.ey;
        rs.get(rs.size() - 1).fix(sy, ey);
    }

    private void fixTexts() {
        int[] sxs = new int[5];
        int[] exs = new int[5];
        int[] ys = new int[3];
        int sx = (int) (all.sx * 19f / 20f + all.ex * 1f / 20f + 0.5f);
        int ex = (int) (all.sx * 1f / 20f + all.ex * 19f / 20f + 0.5f);
        float diff = (ex - sx) / 5f;
        for (int i = 0; i < 5; i++) {
            sxs[i] = (int) (sx / 5 * (5 - i) + ex / 5 * i + 3.5f);
            exs[i] = (int) (sx / 5 * (5 - i) + ex / 5 * i + diff - 3.5f);
        }
        int sy = (int) (all.sy / 10f * 8f + rect.sy / 10f * 2f + 0.5f);
        int ey = (int) (all.sy / 10f + rect.sy / 10f * 9f + 0.5f);
        int dif = (ey - sy) / 3;
        for (int i = 0; i < 3; i++) {
            ys[i] = sy + dif * i;
        }
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                qs.get(i).get(j).fix(sxs[j], exs[j], ys[i]);
            }
        }
    }

    private void drawBase(Canvas canvas) {
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0f);
        // x轴线
        canvas.drawLine(rect.sx, rect.sy, rect.ex, rect.sy, paint);
        // x轴线
        canvas.drawLine(rect.sx, rect.ey, rect.ex, rect.ey, paint);
        for (int i = 1; i < rs.size(); i++) {
            C<T> r = rs.get(i);
            canvas.drawLine(rect.sx, r.sy, rect.ex, r.sy, paint);
        }
        // y轴线
        canvas.drawLine(rect.sx, rect.sy, rect.sx, rect.ey, paint);
        // y轴线
        canvas.drawLine(rect.ex, rect.sy, rect.ex, rect.ey, paint);
        // x轴刻度
        this.drawXIndex(canvas);
        for (C<T> r : rs) {
            // y轴刻度平线
            this.drawYIndexLine(canvas, r);
            this.drawYIndex(canvas, r);
        }
        this.drawXIndexLine(canvas);
        paint.setTextSize(25f);
        // 顶端指标数据
        for (List<Q<T>> arr : qs) {
            for (Q<T> q : arr) {
                this.doDrawText(canvas, q);
            }
        }
        paint.setColor(Color.DKGRAY);
        paint.setStyle(Paint.Style.STROKE);
        for (C<T> r : rs) {
            this.doDrawLine(canvas, r);
        }
    }

    private void drawXIndexLine(Canvas canvas) {
        paint.setColor(Color.DKGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        float x = rect.sx + index * unitDate;
        canvas.drawLine(x, rect.sy, x, rect.ey, paint);
        paint.setPathEffect(null);
    }

    private void drawYIndex(Canvas canvas, C<T> r) {
        paint.setColor(Color.BLACK);
        paint.setTextSize(20f);
        List<String> texts = r.texts;
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            float x = rect.sx - this.getTextWidth(paint, text) - 20;
            float y;
            if (i == texts.size() - 1) {
                y = r.ey - i * r.interval + this.getTextHeight(paint, text) + 3;
            } else if (i == 0) {
                y = r.ey - 3;
            } else {
                y = r.ey - i * r.interval + this.getTextHeight(paint, text) / 2f;
            }
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
            float x = rect.sx + i * intervalDate - this.getTextWidth(paint, text) / 2f;
            float y = rect.ey + this.getTextHeight(paint, text) / 2f * 3f + 15;
            canvas.drawText(text, x, y, paint);
        }
    }

    private void drawYIndexLine(Canvas canvas, C<T> r) {
        paint.setColor(Color.LTGRAY);
        paint.setPathEffect(new DashPathEffect(new float[]{8, 10, 8, 10}, 0));
        for (int i = 1; i < r.texts.size() - 1; i++) {
            int indexY = r.ey - i * r.interval;
            canvas.drawLine(rect.sx, indexY, rect.ex, indexY, paint);
        }
        paint.setPathEffect(null);
    }

    private void doDrawLine(Canvas canvas, C<T> r) {
        Double min = r.min;
        float unit = r.unit;
        int ey = r.ey;
        int color = 0;
        for (Function<T, Double> f : r.fs) {
            this.doDrawLine(canvas, min, unit, ey, colors[color++], f);
        }
    }

    private void doDrawLine(Canvas canvas, double min, float unit, int ey, int color, Function<T, Double> func) {
        // 折线
        Path path = new Path();
        boolean moved = false;
        for (int i = 0; i < list.size(); i++) {
            T k = list.get(i);
            float x = i * unitDate + rect.sx;
            Double d = func.apply(k);
            if (d == null) continue;
            float y = (float) ((min - d) * unit + ey);
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

    private void doDrawText(Canvas canvas, Q<T> q) {
        paint.setColor(Color.DKGRAY);
        String title = q.title;
        String content = q.content(curr);
        float y = q.y + this.getTextHeight(paint, title) / 2f;
        canvas.drawText(title, q.sx, y, paint);
        paint.setColor(q.color);
        canvas.drawText(content, q.ex - this.getTextWidth(paint, content), y, paint);
    }

    private int getTextWidth(Paint paint, String text) {
        return (int) paint.measureText(text);
    }

    private int getTextHeight(Paint paint, String text) {
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        return rect.height();
    }

    private static class XY {
        private int sx;
        private int sy;
        private int ex;
        private int ey;
    }

    public static class C<T> {

        private final int scale;
        private final int weight;
        private final List<Function<T, Double>> fs;

        private int sy;
        private int ey;
        private int interval;
        private float unit;
        private Double min;
        private List<String> texts;

        public C(int scale, int weight, List<Function<T, Double>> fs) {
            this.scale = scale;
            this.weight = weight;
            this.fs = fs;
        }

        public void fix(int sy, int ey) {
            this.sy = sy;
            this.ey = ey;
        }

        @SuppressLint("DefaultLocale")
        public void calculate(List<T> data) {
            SortedSet<Double> amounts = new TreeSet<>();
            for (T d : data) {
                for (Function<T, Double> f : fs) {
                    this.addDoubles(amounts, f.apply(d));
                }
            }
            double min = amounts.first() * scale, max = amounts.last() * scale, count = 2d;
            if (max == min) { // 最大值与最小值相等，特殊处理
                max = max * 2;
                min = 0;
            }
            double v = Math.ceil((max - min) / count), first = Math.floor(min), last = first + v * (count - 1);
            if (last < max) {
                count = 3d;
            }
            this.texts = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                this.texts.add(TextUtil.amount((first + v * i) / scale));
            }
            this.min = first / scale;
            max = (first + (count - 1) * v) / scale;
            this.interval = (int) ((this.ey - this.sy) / (double) (this.texts.size() - 1) + 0.5d);
            this.unit = (float) ((this.ey - this.sy) / (max - this.min));
        }

        private void addDoubles(SortedSet<Double> doubles, Double d) {
            if (d != null) {
                doubles.add(d);
            }
        }

    }

    public static class Q<T> {
        private final String title;
        private final Function<T, String> text;
        private final int color;
        private int sx;
        private int ex;
        private int y;

        public Q(String title, Function<T, String> text, int color) {
            this.title = title;
            this.text = text;
            this.color = color;
        }

        public String content(T d) {
            if (d == null) {
                return "";
            }
            return text.apply(d);
        }

        public void fix(int sx, int ex, int y) {
            this.sx = sx;
            this.ex = ex;
            this.y = y;
        }

    }

}
