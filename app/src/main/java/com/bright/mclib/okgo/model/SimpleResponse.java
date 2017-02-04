package com.bright.mclib.okgo.model;

import java.io.Serializable;

public class SimpleResponse implements Serializable {

    public int code;
    public String message;
    public boolean success;

    public LzyResponse toLzyResponse() {
        LzyResponse lzyResponse = new LzyResponse();
        lzyResponse.code = code;
        lzyResponse.message = message;
        lzyResponse.success = success;
        return lzyResponse;
    }
}