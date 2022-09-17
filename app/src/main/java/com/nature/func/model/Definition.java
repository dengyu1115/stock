package com.nature.func.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Definition extends BaseModel {

    private String title;
    private String code;
    private String type;
    private String desc;
    private String json;

}
