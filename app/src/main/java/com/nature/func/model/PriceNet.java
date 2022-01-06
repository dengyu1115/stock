package com.nature.func.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PriceNet extends BaseModel {

    private String code;
    private String date;
    private String name;
    private Double priceLast;
    private Double priceLatest;
    private Double priceHigh;
    private Double priceLow;
    private Double netLast;
    private Double netLatest;
    private Double ratePrice;
    private Double rateNet;
    private Double rateDiff;
    private Double scale;
    private Double amount;
    private Double rateAmount;

}
