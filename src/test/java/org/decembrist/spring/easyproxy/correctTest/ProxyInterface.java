package org.decembrist.spring.easyproxy.correctTest;

import org.decembrist.spring.easyproxy.EasyProxy;

@EasyProxy({TestInterceptor1.class, TestInterceptor2.class})
public interface ProxyInterface {

    String proxyMethod(String param);

}
