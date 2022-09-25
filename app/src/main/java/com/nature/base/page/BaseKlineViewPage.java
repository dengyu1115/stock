package com.nature.base.page;

import android.content.Context;
import android.graphics.Color;
import android.widget.LinearLayout;
import com.nature.base.manager.BaseKlineManager;
import com.nature.base.model.Item;
import com.nature.base.model.Kline;
import com.nature.common.page.Page;
import com.nature.common.util.TextUtil;
import com.nature.common.view.LView;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static com.nature.common.view.LView.C;
import static com.nature.common.view.LView.Q;

public abstract class BaseKlineViewPage extends Page {

    public static final List<Function<Kline, Double>> FUNC_PRICE = Arrays.asList(
            i -> i.getPrice().getLatest(),
            i -> i.getPrice().getOpen(),
            i -> i.getPrice().getHigh(),
            i -> i.getPrice().getLow());
    public static final List<Function<Kline, Double>> FUNC_NET = Arrays.asList(
            i -> i.getNet().getLatest(),
            i -> i.getAvg().getWeek(),
            i -> i.getAvg().getMonth(),
            i -> i.getAvg().getSeason(),
            i -> i.getAvg().getYear());
    public static final List<Function<Kline, Double>> FUNC_AMOUNT = Collections.singletonList(Kline::getAmount);
    private static final int[] COLORS = new int[]{0xFFFF0000, 0xFF1E90FF, 0xFF32CD32, 0xFFEEEE00, 0xFF8E388E};
    private static final List<List<Q<Kline>>> QS = Arrays.asList(
            Arrays.asList(
                    new Q<>("CODE:", d -> TextUtil.text(d.getCode()), Color.BLACK),
                    new Q<>("日期:", d -> TextUtil.text(d.getDate()), Color.BLACK),
                    new Q<>("开盘:", d -> TextUtil.price(d.getPrice().getOpen()), COLORS[1]),
                    new Q<>("最高:", d -> TextUtil.price(d.getPrice().getHigh()), COLORS[2]),
                    new Q<>("交易量:", d -> TextUtil.amount(d.getShare()), COLORS[0])
            ),
            Arrays.asList(
                    new Q<>("名称:", d -> TextUtil.text(d.getName()), Color.BLACK),
                    new Q<>("当前:", d -> TextUtil.price(d.getPrice().getLatest()), COLORS[0]),
                    new Q<>("", d -> "", Color.BLACK),
                    new Q<>("最低:", d -> TextUtil.price(d.getPrice().getLow()), COLORS[3]),
                    new Q<>("交易额:", d -> TextUtil.amount(d.getAmount()), COLORS[4])
            ),
            Arrays.asList(
                    new Q<>("总计:", d -> TextUtil.price(d.getNet().getLatest()), COLORS[0]),
                    new Q<>("周平均:", d -> TextUtil.price(d.getAvg().getWeek()), COLORS[1]),
                    new Q<>("月平均:", d -> TextUtil.price(d.getAvg().getMonth()), COLORS[2]),
                    new Q<>("季平均:", d -> TextUtil.price(d.getAvg().getSeason()), COLORS[3]),
                    new Q<>("年平均:", d -> TextUtil.price(d.getAvg().getYear()), COLORS[4])
            )
    );
    private static final List<C<Kline>> RS = Arrays.asList(
            new C<>(1000, 3, FUNC_PRICE, TextUtil::price),
            new C<>(1000, 3, FUNC_NET, TextUtil::price),
            new C<>(1000, 1, FUNC_AMOUNT, TextUtil::amount)
    );

    private LView<Kline> view;

    private List<Kline> data() {
        Item item = this.getParam();
        return this.manager().list(item.getCode(), item.getMarket());
    }

    @Override
    protected void makeStructure(LinearLayout page, Context context) {
        page.addView(view = new LView<>(context));
        view.init(COLORS, QS, RS, Kline::getDate, new Kline());
    }

    @Override
    protected void onShow() {
        view.data(this.data());
    }

    protected abstract BaseKlineManager manager();
}
