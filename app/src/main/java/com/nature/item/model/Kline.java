package com.nature.item.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Kline extends Item {

    private String date;
    private Double open;
    private Double latest;
    private Double high;
    private Double low;
    private Double share;
    private Double amount;
    private Double scale;
    private Double avgWeek;
    private Double avgMonth;
    private Double avgSeason;
    private Double avgYear;

}
