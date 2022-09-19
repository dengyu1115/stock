package com.nature.stock.model;

import com.nature.base.model.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Target extends Item {

    private String date;
    private Double price;
    private Integer daysInc;
    private Double rateInc;
    private Double priceInc;
    private Integer daysDec;
    private Double rateDec;
    private Double priceDec;

}
