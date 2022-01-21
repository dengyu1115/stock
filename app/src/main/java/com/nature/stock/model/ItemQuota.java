package com.nature.stock.model;

import com.nature.common.model.Quota;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ItemQuota extends Item {

    private List<Quota> list;

}
