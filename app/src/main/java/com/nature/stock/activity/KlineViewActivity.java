package com.nature.stock.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.alibaba.fastjson.JSON;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.util.TextUtil;
import com.nature.common.util.ViewUtil;
import com.nature.common.view.LView;
import com.nature.stock.manager.KlineManager;
import com.nature.stock.model.Item;
import com.nature.stock.model.Kline;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.nature.common.view.LView.C;
import static com.nature.common.view.LView.Q;

public class KlineViewActivity extends AppCompatActivity {

    private static final int[] COLORS = new int[]{0xFFFF0000, 0xFF1E90FF, 0xFF32CD32, 0xFFEEEE00, 0xFF8E388E};
    private static final List<List<Q<Kline>>> QS = Arrays.asList(
            Arrays.asList(
                    new Q<>("CODE:", d -> TextUtil.text(d.getCode()), Color.BLACK),
                    new Q<>("日期:", d -> TextUtil.text(d.getDate()), Color.BLACK),
                    new Q<>("开盘:", d -> TextUtil.price(d.getPriceOpen()), COLORS[1]),
                    new Q<>("最高:", d -> TextUtil.price(d.getPriceHigh()), COLORS[2]),
                    new Q<>("交易量:", d -> TextUtil.amount(d.getShare()), COLORS[0])
            ),
            Arrays.asList(
                    new Q<>("名称:", d -> TextUtil.text(d.getName()), Color.BLACK),
                    new Q<>("当前:", d -> TextUtil.price(d.getPriceLatest()), COLORS[0]),
                    new Q<>("", d -> "", Color.BLACK),
                    new Q<>("最低:", d -> TextUtil.price(d.getPriceLow()), COLORS[3]),
                    new Q<>("交易额:", d -> TextUtil.amount(d.getAmount()), COLORS[4])
            ),
            Arrays.asList(
                    new Q<>("总计:", d -> TextUtil.price(d.getLatest()), COLORS[0]),
                    new Q<>("周平均:", d -> TextUtil.price(d.getAvgWeek()), COLORS[1]),
                    new Q<>("月平均:", d -> TextUtil.price(d.getAvgMonth()), COLORS[2]),
                    new Q<>("季平均:", d -> TextUtil.price(d.getAvgSeason()), COLORS[3]),
                    new Q<>("年平均:", d -> TextUtil.price(d.getAvgYear()), COLORS[4])
            )
    );

    private static final List<C<Kline>> RS = Arrays.asList(
            new C<>(1000, 3, Arrays.asList(Kline::getPriceLatest, Kline::getPriceOpen, Kline::getPriceHigh, Kline::getPriceLow)),
            new C<>(1000, 3, Arrays.asList(Kline::getLatest, Kline::getAvgWeek, Kline::getAvgMonth, Kline::getAvgSeason, Kline::getAvgYear)),
            new C<>(1000, 1, Collections.singletonList(Kline::getAmount))
    );

    private final KlineManager klineManager = InstanceHolder.get(KlineManager.class);

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LView<Kline> view = new LView<>(this);
        view.init(COLORS, QS, RS, Kline::getDate, new Kline());
        view.data(this.data());
        this.setContentView(view);
        ViewUtil.initActivity(this);
    }

    private List<Kline> data() {
        Item item = JSON.parseObject(this.getIntent().getStringExtra("data"), Item.class);
        return klineManager.list(item.getCode(), item.getMarket());
    }

}
