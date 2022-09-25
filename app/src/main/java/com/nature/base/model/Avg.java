package com.nature.base.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Avg extends BaseModel {

    private Double week;
    private Double month;
    private Double season;
    private Double year;

}
