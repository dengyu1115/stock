package com.nature.func.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Line extends BaseModel {

    private String date;
    private Double price;
    private Double rate;
    private Double rateTotal;

}
