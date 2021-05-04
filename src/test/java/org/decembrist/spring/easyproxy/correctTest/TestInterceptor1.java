package org.decembrist.spring.easyproxy.correctTest;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TestInterceptor1 implements MethodInterceptor {

    public List<String> params = new ArrayList<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        params.add(invocation.getArguments()[0].toString());
        return invocation.proceed();
    }

}
