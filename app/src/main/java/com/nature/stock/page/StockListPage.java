package com.nature.stock.page;

import com.nature.base.manager.BaseItemManager;
import com.nature.base.page.BaseItemListPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
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
public class StockListPage extends BaseItemListPage<Stock> {
    @Injection
    private StockManager stockManager;
    @Injection
    private IndustryManager industryManager;
    private Selector<String> exchange, industry;
    private Map<String, String> map;

    protected List<Stock> listData() {
        return stockManager.list(this.exchange.getValue(), this.industry.getValue(), this.keyword.getText().toString());
    }

    @Override
    protected BaseItemManager<Stock> manager() {
        return this.stockManager;
    }

    @Override
    protected void extraColumns(List<ExcelView.D<Stock>> list) {
        list.add(new ExcelView.D<>("交易所", d -> TextUtil.text(Market.codeToName(d.getExchange())), C, E, Sorter.nullsLast(Stock::getExchange)));
        list.add(new ExcelView.D<>("行业", d -> TextUtil.text(this.map.get(d.getIndustry())), C, E, Sorter.nullsLast(Stock::getIndustry)));
    }

    @Override
    protected void extraViews(SearchBar searchBar) {
        searchBar.addConditionView(exchange = template.selector(80, 30));
        searchBar.addConditionView(industry = template.selector(80, 30));
    }

    @Override
    protected void extraBehaviours() {
        List<String> exchanges = Arrays.stream(Market.values()).map(Market::getCode).collect(Collectors.toList());
        exchanges.add(0, null);
        exchange.mapper(this::getExchangeName).init().refreshData(exchanges);
        List<Industry> list = industryManager.list();
        this.map = list.stream().collect(Collectors.toMap(Industry::getCode, Industry::getName, (o, n) -> n));
        List<String> industries = list.stream().map(Industry::getCode).collect(Collectors.toList());
        industries.add(0, null);
        industry.mapper(this::getIndustryName).init().refreshData(industries);
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
