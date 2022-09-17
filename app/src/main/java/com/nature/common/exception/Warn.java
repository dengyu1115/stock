package com.nature.common.exception;

public class Warn extends RuntimeException {

    private String msg;

    public Warn(String msg) {
        super(msg);
    }

}
