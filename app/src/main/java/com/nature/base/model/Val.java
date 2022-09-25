package com.nature.base.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Val extends BaseModel {

    private Double open;
    private Double latest;
    private Double high;
    private Double low;

}
