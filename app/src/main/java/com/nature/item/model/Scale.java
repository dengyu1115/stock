package com.nature.item.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Scale extends BaseModel {

    private String code;
    private String name;
    private String date;
    private Double amount;
    private Double change;

}
