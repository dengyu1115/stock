package com.nature.item.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Net extends Item {

    private String date;

    private Double net;

    private Double rate;

    private Double netTotal;

    private Double rateTotal;

}
