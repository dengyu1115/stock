package com.nature.stock.item.model;

import com.nature.stock.common.model.BaseModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Quota extends BaseModel {

    private String code;

    private String status;

    private String date;

    private Double syl;

    private Double szZ;

    private Double gbZ;

    private Double szLt;

    private Double gbLt;

    private Double price;

    private Double count;

    private Double sylRate;

    private Double szZRate;

    private Double gbZRate;

    private Double szLtRate;

    private Double gbLtRate;

    private Double priceRate;

    private Double countRate;

}
