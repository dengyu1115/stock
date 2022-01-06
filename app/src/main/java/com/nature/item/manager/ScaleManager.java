package com.nature.item.manager;

import com.nature.common.db.BaseDB;
import com.nature.common.ioc.annotation.Injection;
import com.nature.common.util.ExeUtil;
import com.nature.item.http.ScaleHttp;
import com.nature.item.mapper.ScaleMapper;
import com.nature.item.model.Item;
import com.nature.item.model.Scale;

import java.util.List;

/**
 * 折线
 * @author nature
 * @version 1.0.0
 * @since 2020/8/30 22:40
 */
public class ScaleManager {

    @Injection
    private ScaleMapper scaleMapper;
    @Injection
    private ScaleHttp scaleHttp;
    @Injection
    private ItemGroupManager itemGroupManager;

    public int reloadAll() {
        return ExeUtil.exec(itemGroupManager::listAllFunds, this::reload);
    }

    private int reload(Item item) {
        return this.batchMerge(scaleHttp.list(item.getCode(), 1000000));
    }

    private int batchMerge(List<Scale> list) {
        if (list == null || list.isEmpty()) return 0;
        return BaseDB.create().batchExec(list, 200, l -> scaleMapper.batchMerge(l));
    }

    public List<Scale> listLast(String date) {
        return scaleMapper.listLast(date);
    }

    public List<Scale> listLatest(String keyword) {
        return scaleMapper.listLatest(keyword);
    }

    public List<Scale> listByCode(String code) {
        return scaleMapper.listByCode(code);
    }
}
