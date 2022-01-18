package com.nature.stock.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateType extends BaseModel {

    private String code;
    private String title;
    private String json;

}
