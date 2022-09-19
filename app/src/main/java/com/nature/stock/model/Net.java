package com.nature.stock.model;

import com.nature.base.model.ItemLine;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Net extends ItemLine {

    private Double open;
    private Double high;
    private Double low;
    private Double avgWeek;
    private Double avgMonth;
    private Double avgSeason;
    private Double avgYear;

}
