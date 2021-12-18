package com.nature.stock.func.model;

import com.nature.stock.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateDef extends BaseModel {

    private String title;

    private String code;

    private String type;

    private Integer count;

    private String dateStart;

    private String dateEnd;

}
