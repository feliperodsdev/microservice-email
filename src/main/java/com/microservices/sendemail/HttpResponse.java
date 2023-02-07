package com.microservices.sendemail;

public class HttpResponse <T> {
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
