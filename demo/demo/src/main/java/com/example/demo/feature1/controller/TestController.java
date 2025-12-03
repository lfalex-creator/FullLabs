package com.example.demo.feature1.controller;

import com.example.demo.feature1.dto.TestRequest;
import com.example.demo.feature1.dto.TestResponse;
import com.example.demo.feature1.service.TestService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
public class TestController {
    public final TestService testService;

    @GetMapping("/test")
    public TestResponse testGet(@RequestBody TestRequest testRequest) {
        return testService.test(testRequest);
    }

    @PostMapping("/test")
    public TestResponse testPost(@RequestBody TestRequest testRequest) {
        return testService.test(testRequest);
    }

    @PostConstruct
    public void init(){
        System.out.println("TestController initialized");
    }
}
