package com.nature.stock.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Net extends Item {

    private String date;
    private Double latest;
    private Double open;
    private Double high;
    private Double low;
    private Double avgWeek;
    private Double avgMonth;
    private Double avgSeason;
    private Double avgYear;

}
