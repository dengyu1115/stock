package com.nature.stock.model;

import com.nature.base.model.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Price extends Item {

    private String date;
    private Double open;
    private Double latest;
    private Double high;
    private Double low;
    private Double share;
    private Double amount;

}
