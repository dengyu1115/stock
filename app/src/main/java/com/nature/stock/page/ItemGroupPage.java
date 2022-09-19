package com.nature.stock.page;

import android.widget.LinearLayout;
import com.nature.base.manager.BaseItemGroupManager;
import com.nature.base.manager.BaseItemManager;
import com.nature.base.page.BaseItemGroupPage;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.ioc.annotation.PageView;
import com.nature.common.util.Sorter;
import com.nature.common.util.TextUtil;
import com.nature.common.view.ExcelView;
import com.nature.common.view.Selector;
import com.nature.stock.enums.Market;
import com.nature.stock.manager.IndustryManager;
import com.nature.stock.manager.ItemGroupManager;
import com.nature.stock.manager.StockManager;
import com.nature.stock.model.Industry;
import com.nature.stock.model.Stock;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@PageView(name = "项目分组", group = "股票", col = 0, row = 0)
public class ItemGroupPage extends BaseItemGroupPage<Stock> {

    @Injection
    private IndustryManager industryManager;
    @Injection
    private StockManager stockManager;
    @Injection
    private ItemGroupManager itemGroupManager;
    private Selector<String> exchange, industry;
    private Map<String, String> map;


    @Override
    protected BaseItemGroupManager manager() {
        return this.itemGroupManager;
    }

    @Override
    protected BaseItemManager<Stock> itemManager() {
        return this.stockManager;
    }

    @Override
    protected void extraViews(LinearLayout left) {
        left.addView(exchange = template.selector(100, 30));
        left.addView(industry = template.selector(100, 30));
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

    @Override
    protected void extraColumns(List<ExcelView.D<Stock>> list) {
        list.add(1, new ExcelView.D<>("交易所", d -> TextUtil.text(Market.codeToName(d.getExchange())), C, E, Sorter.nullsLast(Stock::getExchange)));
        list.add(2, new ExcelView.D<>("行业", d -> TextUtil.text(this.map.get(d.getIndustry())), C, E, Sorter.nullsLast(Stock::getIndustry)));
    }

    @Override
    protected boolean leftFilter(Stock i) {
        String exchange = this.exchange.getValue();
        String industry = this.industry.getValue();
        boolean match = true;
        if (StringUtils.isNotBlank(exchange)) {
            match = exchange.equals(i.getExchange());
        }
        if (StringUtils.isNotBlank(industry)) {
            match = match && industry.equals(i.getIndustry());
        }
        return match && (i.getCode().contains(keyword) || i.getName().contains(keyword));
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
