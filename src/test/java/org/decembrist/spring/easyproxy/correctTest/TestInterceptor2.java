package org.decembrist.spring.easyproxy.correctTest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestInterceptor2 implements MethodInterceptor {

    public List<String> invokes = new ArrayList<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        invokes.add(invocation.getMethod().getName());
        return "TestInterceptor2";
    }

}
