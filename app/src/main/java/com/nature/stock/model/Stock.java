package com.nature.stock.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stock extends Item {

    private String exchange;
    private String industry;

}
