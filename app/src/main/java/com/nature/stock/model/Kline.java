package com.nature.stock.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Kline extends Net {

    private Double priceOpen;
    private Double priceLatest;
    private Double priceHigh;
    private Double priceLow;
    private Double share;
    private Double amount;

}
