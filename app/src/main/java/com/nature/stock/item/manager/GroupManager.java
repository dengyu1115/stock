package com.nature.stock.item.manager;

import com.nature.stock.common.ioc.annotation.Injection;
import com.nature.stock.item.mapper.GroupMapper;
import com.nature.stock.item.model.Group;
import com.nature.stock.item.model.ItemGroup;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * item
 * @author nature
 * @version 1.0.0
 * @since 2020/4/19 11:35
 */
public class GroupManager {

    @Injection
    private GroupMapper groupMapper;
    @Injection
    private ItemGroupManager itemGroupManager;

    /**
     * 批量保存
     * @param group group
     * @return int
     */
    public int merge(Group group) {
        return groupMapper.merge(group);
    }

    /**
     * 查询
     * @return list
     */
    public List<Group> list(String type) {
        List<Group> list = groupMapper.list(type);
        List<String> codes = list.stream().map(Group::getCode).collect(Collectors.toList());
        List<ItemGroup> igs = itemGroupManager.listByGroups(codes);
        Map<String, List<ItemGroup>> groups = igs.stream().collect(Collectors.groupingBy(ItemGroup::getGroup));
        for (Group group : list) {
            List<ItemGroup> itemGroups = groups.get(group.getCode());
            if (itemGroups == null) continue;
            group.setCodes(itemGroups.stream().map(ItemGroup::getCode).collect(Collectors.toSet()));
        }
        return list;
    }

    public int delete(String code, String type) {
        return groupMapper.delete(code, type);
    }

    public Group findByCode(String code, String type) {
        return groupMapper.findByCode(code, type);
    }
}
