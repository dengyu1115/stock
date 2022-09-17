package com.nature.stock.manager;

import com.nature.common.calculator.QuotaCalculator;
import com.nature.common.ioc.annotation.Component;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.model.Quota;
import com.nature.common.util.CommonUtil;
import com.nature.common.util.RemoteExeUtil;
import com.nature.stock.enums.RateDefType;
import com.nature.stock.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ItemQuotaManager {

    @Injection
    private RateDefManager rateDefManager;
    @Injection
    private StockManager stockManager;
    @Injection
    private NetManager netManager;

    public List<ItemQuota> list(String rateType, String group, String date, String keyword) {
        if (StringUtils.isBlank(rateType)) {
            throw new RuntimeException("type is blank");
        }
        if (StringUtils.isBlank(group)) {
            throw new RuntimeException("group is blank");
        }
        List<RateDef> list = this.listRateDef(rateType, date);
        List<Stock> items = this.listItem(group, keyword);
        return RemoteExeUtil.exec(() -> items, i -> itemHandle(list, i));
    }

    private ItemQuota itemHandle(List<RateDef> list, Item i) {
        ItemQuota iq = new ItemQuota();
        iq.setCode(i.getCode());
        iq.setMarket(i.getMarket());
        iq.setName(i.getName());
        List<Quota> qs = new ArrayList<>();
        iq.setList(qs);
        for (RateDef j : list) {
            List<Net> nets = netManager.listAsc(i.getCode(), i.getMarket(), j.getDateStart(), j.getDateEnd());
            qs.add(QuotaCalculator.calculate(nets, Net::getDate, Net::getLatest, Net::getLow, Net::getHigh));
        }
        return iq;
    }

    private List<Stock> listItem(String group, String keyword) {
        List<Stock> items = stockManager.list(group, keyword);
        if (CollectionUtils.isEmpty(items)) {
            throw new RuntimeException("stocks is empty");
        }
        return items;
    }

    private List<RateDef> listRateDef(String rateType, String date) {
        List<RateDef> list = rateDefManager.list(rateType);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("rate ref list is empty");
        }
        for (RateDef i : list) {
            if (RateDefType.PERIOD.getCode().equals(i.getType())) {
                i.setDateStart(CommonUtil.addDays(CommonUtil.formatDate(new Date()), -i.getDays()));
            } else if (StringUtils.isBlank(i.getDateStart())) {
                i.setDateStart(date);
            }
        }
        return list;
    }

}
