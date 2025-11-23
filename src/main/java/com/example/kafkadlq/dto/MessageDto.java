package com.example.kafkadlq.dto;

import java.io.Serializable;

public class MessageDto implements Serializable {
    private String id;
    private String payload;

    public MessageDto() {}

    public MessageDto(String id, String payload) {
        this.id = id;
        this.payload = payload;
    }

    public String getId() { return id; }
    public String getPayload() { return payload; }

    public void setId(String id) { this.id = id; }
    public void setPayload(String payload) { this.payload = payload; }

    @Override
    public String toString() {
        return "MessageDto{id='" + id + "', payload='" + payload + "'}";
    }
}
