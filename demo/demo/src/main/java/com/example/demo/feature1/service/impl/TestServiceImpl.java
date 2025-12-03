package com.example.demo.feature1.service.impl;

import com.example.demo.feature1.dto.TestRequest;
import com.example.demo.feature1.dto.TestResponse;
import com.example.demo.feature1.service.TestService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {
    /*
    Le instantiaza in functie de nevoie
    public void a(){
        A a=new A();
        B b= new B();
        C c = new C(a,b);D d= new D(c)
    }*/
    @Override
    public TestResponse test(TestRequest testRequest) {
        return new TestResponse(testRequest.getName());
    }

}
