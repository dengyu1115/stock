package com.nature.common.page;

import android.content.Context;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class BasicPage extends LinearLayout {

    public static final int C = 0, S = 1, E = 2;

    private final Stack<Page> views;

    private final Map<Class<?>, Page> map;

    public BasicPage(Context context) {
        super(context);
        this.views = new Stack<>();
        this.map = new HashMap<>();
    }

    public <T extends Page> void show(Class<T> clz) {
        this.show(clz, null);
    }

    public <T extends Page, P> void show(Class<T> clz, P param) {
        Page page = map.get(clz);
        if (page != null) {
            page.setParam(param);
            this.show(page);
        } else {
            try {
                page = clz.newInstance();
                page.doCreate(this);
                page.setParam(param);
                if (!page.isProtocol()) {
                    map.put(clz, page);
                }
                this.show(page);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void show(Page page) {
        views.push(page);
        this.removeAllViews();
        this.addView(page.get());
        page.onShow();
    }

    public int viewSize() {
        return views.size();
    }

    public void dispose() {
        views.pop();
        this.removeAllViews();
        Page page = views.peek();
        if (page != null) {
            this.addView(page.get());
        }
    }

}
