package org.decembrist.spring.easyproxy.otherPackage;

import org.decembrist.spring.easyproxy.EasyProxy;
import org.decembrist.spring.easyproxy.correctTest.TestInterceptor2;

@EasyProxy(TestInterceptor2.class)
public interface OtherPackageProxy {

    String proxyMethod(String param);

}
