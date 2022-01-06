package com.nature.func.model;

import com.nature.common.model.BaseModel;
import com.nature.func.model.Line;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LineDef extends BaseModel {

    private String title;
    private int color;
    private String sql;
    private List<Line> list;

}
