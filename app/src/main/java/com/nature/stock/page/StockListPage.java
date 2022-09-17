package com.nature.stock.page;

import android.widget.Button;
import android.widget.EditText;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.ioc.holder.InstanceHolder;
import com.nature.common.page.ListPage;
import com.nature.common.util.ClickUtil;
import com.nature.common.util.PopUtil;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.SearchBar;
import com.nature.common.view.Selector;
import com.nature.stock.enums.Market;
import com.nature.stock.manager.IndustryManager;
import com.nature.stock.manager.StockManager;
import com.nature.stock.model.Industry;
import com.nature.stock.model.Stock;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageView(name = "股票", group = "股票", col = 1, row = 2)
public class StockListPage extends ListPage<Stock> {
    @Injection
    private StockManager stockManager;
    @Injection
    private IndustryManager industryManager;
    private final List<ExcelView.D<Stock>> ds = Arrays.asList(
            new ExcelView.D<>("名称", d -> TextUtil.text(d.getName()), C, S, Sorter.nullsLast(Stock::getName)),
            new ExcelView.D<>("CODE", d -> TextUtil.text(d.getCode()), C, C, Sorter.nullsLast(Stock::getCode)),
            new ExcelView.D<>("市场", d -> TextUtil.text(d.getMarket()), C, E, Sorter.nullsLast(Stock::getMarket)),
            new ExcelView.D<>("交易所", d -> TextUtil.text(Market.codeToName(d.getExchange())), C, E, Sorter.nullsLast(Stock::getExchange)),
            new ExcelView.D<>("行业", d -> TextUtil.text(this.map.get(d.getIndustry())), C, E, Sorter.nullsLast(Stock::getIndustry))
    );
    private Selector<String> exchange, industry;
    private EditText keyword;
    private Button reload;
    private Map<String, String> map;

    protected List<Stock> listData() {
        return stockManager.list(this.exchange.getValue(), this.industry.getValue(), this.keyword.getText().toString());
    }

    @Override
    protected void initHeaderViews(SearchBar searchBar) {
        searchBar.addConditionView(reload = template.button("加载最新", 80, 30));
        searchBar.addConditionView(exchange = template.selector(80, 30));
        searchBar.addConditionView(industry = template.selector(80, 30));
        searchBar.addConditionView(keyword = template.editText(80, 30));
    }

    @Override
    protected void initHeaderBehaviours() {
        reload.setOnClickListener(v ->
                PopUtil.confirm(context, "重新加载数据", "确定重新加载吗？",
                        () -> ClickUtil.asyncClick(v, () -> {
                            String s = String.format("加载完成,共%s条", stockManager.reload());
                            this.refreshData();
                            return s;
                        })));
        List<String> exchanges = Arrays.stream(Market.values()).map(Market::getCode).collect(Collectors.toList());
        exchanges.add(0, null);
        exchange.mapper(this::getExchangeName).init().refreshData(exchanges);
        List<Industry> list = industryManager.list();
        this.map = list.stream().collect(Collectors.toMap(Industry::getCode, Industry::getName, (o, n) -> n));
        List<String> industries = list.stream().map(Industry::getCode).collect(Collectors.toList());
        industries.add(0, null);
        industry.mapper(this::getIndustryName).init().refreshData(industries);
    }

    @Override
    protected List<ExcelView.D<Stock>> define() {
        return ds;
    }

    private String getExchangeName(String code) {
        if (code == null) {
            return "--请选择--";
        }
        return Market.codeToName(code);
    }

    private String getIndustryName(String code) {
        if (code == null) {
            return "--请选择--";
        }
        return this.map.get(code);
    }

}
