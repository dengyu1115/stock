package com.nature.common.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nature.common.util.ClickUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressLint("DefaultLocale")
public class ExcelView<T> extends BasicView {

    private final int columns;
    private List<D<T>> ds;
    private List<T> list = new ArrayList<>();
    private float colWidth;
    private final float widthRate;
    private final LayoutParams param = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
    private final List<HorizontalScrollView> horizontalScrollViews = new ArrayList<>();
    private final AtomicInteger sc = new AtomicInteger(-1);
    private final AtomicBoolean canceled = new AtomicBoolean();
    private final AtomicBoolean running = new AtomicBoolean();
    private final Adapter adapter = new Adapter();
    private final Handler handler = new Handler(msg -> {
        adapter.notifyDataSetChanged();
        return false;
    });
    private HorizontalScrollView touchView;
    private int scrollX;
    private int oldScrollX;
    private int childCount;
    private Comparator<T> comparator;
    private int sortCol;
    private boolean sortClicked;
    private float clickX, clickY;
    private final OnScrollChangeListener scrollChangeListener = (v, x, y, ox, oy) -> this.scrollAll(this.scrollX = x);

    public ExcelView(Context context) {
        this(context, 3);
    }

    public ExcelView(Context context, int columns) {
        this(context, columns, 1);
    }

    public ExcelView(Context context, int columns, float widthRate) {
        super(context);
        this.widthRate = widthRate;
        this.context = context;
        this.columns = columns;
    }

    public void define(List<D<T>> ds) {
        this.ds = ds;
        this.init();
    }

    public void data(List<T> list) {
        this.list = list;
        handler.sendMessage(new Message());
    }

    private void init() {
        this.calculateColumnWidth();
        this.setBaselineAligned(true);
        this.setLayoutParams(param);
        this.setOrientation(LinearLayout.VERTICAL);
        this.addView(this.hDivider());
        this.addView(this.titleView());
        this.addView(this.hDivider());
        this.addView(this.listView());
    }

    private void calculateColumnWidth() {
        this.colWidth = context.getResources().getDisplayMetrics().widthPixels * widthRate / DENSITY / columns + 0.5f; //  - 2
    }

    private LinearLayout titleView() {
        ArrayList<TextView> titleViews = new ArrayList<>();
        LinearLayout layout = this.itemView(titleViews);
        for (int i = 0; i < ds.size(); i++) {
            TextView textView = titleViews.get(i);
            D<T> d = ds.get(i);
            textView.setText(d.title);
            this.addSortClickEvent(textView, d.sort, i);
            textView.setGravity(textAlign(d.titleAlign));
        }
        return layout;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void addSortClickEvent(TextView textView, Comparator<T> comparator, int col) {
        if (comparator == null) return;
        if (col == 0) textView.setOnClickListener((v) -> {
            this.comparator = comparator;
            this.sortCol = col;
            this.sortClick(v);
        });
        else textView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                this.comparator = comparator;
                this.sortCol = col;
                this.sortClicked = true;
            }
            return false;
        });
    }

    private void sortClick(View v) {
        ClickUtil.doClick(v, () -> {
            if (sc.get() == this.sortCol) {
                sc.set(-1);
                this.list.sort(this.comparator.reversed());
            } else {
                sc.set(this.sortCol);
                this.list.sort(this.comparator);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void scrollFix(HorizontalScrollView scrollView) {
        OnTouchListener listener = (v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) { // 手指点下时候事件处理
                clickX = event.getX();
                clickY = event.getY();
                synchronized (ExcelView.this) {
                    canceled.set(true);
                    if (touchView != null) {
                        touchView.fling(0);
                        touchView.setOnScrollChangeListener(null);
                    }
                    this.scrollAll(scrollView.getScrollX());
                    (touchView = scrollView).setOnScrollChangeListener(scrollChangeListener);
                }
            } else if (action == MotionEvent.ACTION_UP && sortClicked
                    && event.getX() == clickX && event.getY() == clickY) { // 手指放开事件处理
                this.sortClick(v);
                this.sortClicked = false;
            } else if (action != MotionEvent.ACTION_MOVE) { // 其他非移动情况处理
                synchronized (ExcelView.this) {
                    canceled.set(false);
                    if (running.get()) return false;
                    Timer timer = new Timer();
                    running.set(true);
                    timer.schedule(this.moveFixTask(scrollView, timer), 500, 100);
                }
                this.sortClicked = false;
            } else {
                (touchView = scrollView).setOnScrollChangeListener(scrollChangeListener);
            }
            return false;
        };
        scrollView.setOnTouchListener(listener);
    }

    private TimerTask moveFixTask(HorizontalScrollView scrollView, Timer timer) {
        return new TimerTask() {
            @Override
            public void run() {
                synchronized (ExcelView.this) {
                    if (canceled.get()) {
                        timer.cancel();
                        running.set(false);
                        return;
                    }
                    int x = scrollView.getScrollX();
                    int scrollX = calculateFixScroll(x);
                    if (oldScrollX != x) oldScrollX = x;
                    else {
                        scrollView.setOnScrollChangeListener(null);
                        scrollAll(scrollX);
                    }
                    if (scrollX == x) {
                        timer.cancel();
                        running.set(false);
                    }
                }
            }
        };
    }

    private int calculateFixScroll(int i) {
        float dp = pxToDp(i);
        int v = (int) (dp / colWidth);
        return dpToPx(colWidth * (dp - colWidth * v < colWidth / 2 ? v : v + 1)) + v; //
    }

    private LinearLayout line() {
        LinearLayout line = new LinearLayout(context);
        LayoutParams param = new LayoutParams(MATCH_PARENT, WRAP_CONTENT);
        line.setLayoutParams(param);
        return line;
    }

    private HorizontalScrollView horizontalScrollView() {
        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(context);
        horizontalScrollView.setLayoutParams(param);
        horizontalScrollView.setScrollBarSize(0);
        return horizontalScrollView;
    }

    private ListView listView() {
        ListView listView = new ListView(context);
        listView.setLayoutParams(param);
        listView.setScrollBarSize(0);
        listView.setAdapter(adapter);
        listView.setOnScrollChangeListener((v, x, y, ox, oy) -> {
            int count = listView.getChildCount();
            if (childCount < count) {
                childCount = count;
                this.scrollAll(this.scrollX);
            }
        });
        return listView;
    }

    public int getListSize() {
        return list.size();
    }

    private int textAlign(int textAlign) {
        if (textAlign == 1) return Gravity.START | Gravity.CENTER;
        else if (textAlign == 2) return Gravity.END | Gravity.CENTER;
        else return Gravity.CENTER;
    }

    private TextView textView() {
        TextView view = new TextView(context);
        view.setWidth(dpToPx(colWidth));
        view.setHeight(dpToPx(40));
        view.setPadding(dpToPx(8), 0, dpToPx(8), 0);
        return view;
    }

    private View hDivider() {
        return divider(MATCH_PARENT, 1);
    }

    private View vDivider() {
        return divider(1, MATCH_PARENT);
    }

    @SuppressLint("ResourceAsColor")
    private View divider(int w, int h) {
        View view = new View(context);
        LayoutParams param = new LayoutParams(w, h);
        view.setLayoutParams(param);
        view.setBackgroundColor(Color.LTGRAY);
        return view;
    }

    private LinearLayout itemView(List<TextView> textViews) {
        LinearLayout line = this.line();
        HorizontalScrollView scrollView = this.horizontalScrollView();
        scrollView.setOnScrollChangeListener(scrollChangeListener);
        LinearLayout innerLine = this.line();
        scrollView.addView(innerLine);
        horizontalScrollViews.add(scrollView);
        this.scrollFix(scrollView);
        int size = ds.size();
        for (int i = 0; i < size; i++) {
            TextView content = textView();
            textViews.add(content);
            if (i == 0) {
                line.addView(content);
                line.addView(vDivider());
                line.addView(scrollView);
            } else if (i < size - 1) {
                innerLine.addView(content);
                innerLine.addView(vDivider());
            } else {
                innerLine.addView(content);
            }
        }
        return line;
    }

    private void scrollAll(int x) {
        for (HorizontalScrollView v : horizontalScrollViews) v.scrollTo(x, 0);
    }

    public static class D<T> {

        private final String title;
        private final Function<T, String> content;
        private final int titleAlign;
        private final int contentAlign;
        private final Comparator<T> sort;
        private final Consumer<T> click;

        public D(String title, Function<T, String> content) {
            this(title, content, 0);
        }

        public D(String title, Function<T, String> content, int titleAlign) {
            this(title, content, titleAlign, 0);
        }

        public D(String title, Function<T, String> content, int titleAlign, int contentAlign) {
            this(title, content, titleAlign, contentAlign, null, null);
        }

        public D(String title, Function<T, String> content, int titleAlign, int contentAlign, Comparator<T> sort) {
            this(title, content, titleAlign, contentAlign, sort, null);
        }

        public D(String title, Function<T, String> content, int titleAlign, int contentAlign, Consumer<T> click) {
            this(title, content, titleAlign, contentAlign, null, click);
        }

        public D(String title, Function<T, String> content, int titleAlign, int contentAlign, Comparator<T> sort
                , Consumer<T> click) {
            this.title = title;
            this.content = content;
            this.titleAlign = titleAlign;
            this.contentAlign = contentAlign;
            this.sort = sort;
            this.click = click;
        }
    }

    class Adapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public T getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressWarnings("unchecked")
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                ArrayList<TextView> textViews = new ArrayList<>();
                convertView = itemView(textViews);
                convertView.setTag(textViews);
            }
            List<TextView> textViews = (List<TextView>) convertView.getTag();
            T item = getItem(position);
            for (int i = 0; i < textViews.size(); i++) {
                TextView textView = textViews.get(i);
                D<T> d = ds.get(i);
                textView.setText(d.content.apply(item));
                textView.setGravity(textAlign(d.contentAlign));
                if (d.click != null) textView.setOnClickListener(v -> d.click.accept(item));
            }
            return convertView;
        }
    }

}
