package com.nature.stock.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RateDef extends BaseModel {

    private String code;
    private String typeCode;
    private String title;
    private String type;
    private Integer days;
    private String dateStart;
    private String dateEnd;

}
