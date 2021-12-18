package com.nature.stock.common.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class Definition extends BaseModel {

    private String title;

    private String code;

    private String type;

    private String desc;

    private String json;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Definition that = (Definition) o;
        return Objects.equals(title, that.title) &&
                Objects.equals(code, that.code) &&
                Objects.equals(type, that.type) &&
                Objects.equals(desc, that.desc) &&
                Objects.equals(json, that.json);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, code, type, desc, json);
    }
}
