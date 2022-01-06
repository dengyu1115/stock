package com.nature.func.manager;

import com.nature.common.ioc.annotation.Injection;
import com.nature.func.mapper.DefinitionMapper;
import com.nature.func.model.Definition;

import java.util.List;

/**
 * 定义
 * @author nature
 * @version 1.0.0
 * @since 2020/9/19 17:13
 */
public class DefinitionManager {

    @Injection
    private DefinitionMapper definitionMapper;


    public int merge(Definition d) {
        Definition exist = definitionMapper.find(d.getType(), d.getCode());
        if (exist != null) {
            exist.setTitle(d.getTitle());
        }
        return definitionMapper.merge(d);
    }

    public int delete(String type, String code) {
        return definitionMapper.delete(type, code);
    }

    public List<Definition> list(String type) {
        return definitionMapper.list(type);
    }

    public Definition find(String type, String code) {
        return definitionMapper.find(type, code);
    }
}
