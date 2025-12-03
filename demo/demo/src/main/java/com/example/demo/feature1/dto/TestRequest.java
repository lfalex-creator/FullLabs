package com.example.demo.feature1.dto;

public class TestRequest {
    public String name;

    public TestRequest(String name) {
        this.name = name;
    }

    public TestRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
