package com.nhom13.phonemart.model.response;

public class ApiResponse {
    private String message;
    private Object data;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
