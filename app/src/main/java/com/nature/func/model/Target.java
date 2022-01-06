package com.nature.func.model;

import com.nature.item.model.Item;
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
