package com.nhom13.phonemart.model.interfaces;

public interface GeneralCallBack<T> {
    void onSuccess(T result);
    void onError(Throwable t);
}

