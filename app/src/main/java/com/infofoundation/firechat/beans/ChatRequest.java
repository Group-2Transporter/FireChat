package com.infofoundation.firechat.beans;

public class ChatRequest {
    String requestType;

    public String getRequestType() {
        return requestType;
    }

    public ChatRequest() {
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public ChatRequest(String requestType) {
        this.requestType = requestType;
    }
}
