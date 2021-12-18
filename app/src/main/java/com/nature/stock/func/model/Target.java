package com.nature.stock.func.model;

import com.nature.stock.item.model.Item;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Target extends Item {

    private String date;

    private Double price;

    private Double rate;

    private Mark mark;

}
