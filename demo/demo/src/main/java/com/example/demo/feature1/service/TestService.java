package com.example.demo.feature1.service;

import com.example.demo.feature1.dto.TestRequest;
import com.example.demo.feature1.dto.TestResponse;

public interface TestService {
    TestResponse test(TestRequest testRequest);

}
