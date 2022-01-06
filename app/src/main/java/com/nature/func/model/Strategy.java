package com.nature.func.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Strategy extends BaseModel {

    private String code;
    private String name;
    private String date;
    private List<LineDef> list;

}
