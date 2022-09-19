package com.nature.base.model;

import com.nature.common.model.BaseModel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class Item extends BaseModel {

    private String code;
    private String name;
    private String market;

}
