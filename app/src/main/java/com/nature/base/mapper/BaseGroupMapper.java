package com.nature.base.mapper;

import com.nature.base.model.Group;

import java.util.List;

public interface BaseGroupMapper {

    int merge(Group group);

    List<Group> list();

    int delete(String code);

    Group findByCode(String code);
}
