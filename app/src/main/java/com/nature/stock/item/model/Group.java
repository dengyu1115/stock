package com.nature.stock.item.model;

import com.nature.stock.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class Group extends BaseModel {

    private String code;

    private String name;

    private String type;

    private String remark;

    private Set<String> codes;

}
