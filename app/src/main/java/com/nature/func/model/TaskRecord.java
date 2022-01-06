package com.nature.func.model;

import com.nature.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskRecord extends BaseModel {

    private String code;
    private String date;
    private String startTime;
    private String endTime;
    private String status;
    private String exception;

}
