package com.nature.stock.item.model;

import com.nature.stock.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Item extends BaseModel {

    private String code;

    private String name;

    private String type;

    private String market;

}
