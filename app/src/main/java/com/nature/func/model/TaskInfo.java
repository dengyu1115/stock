package com.nature.func.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskInfo extends BaseModel {

    private String code;

    private String name;

    private String startTime;

    private String endTime;

    private String type;

    private String status;

}
