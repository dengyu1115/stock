package com.nature.common.model;

import com.nature.common.page.Page;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageInfo extends BaseModel {

    private String name;
    private Class<? extends Page> cls;
    private int order;

}
