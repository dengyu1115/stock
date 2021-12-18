package com.nature.stock.common.model;

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
