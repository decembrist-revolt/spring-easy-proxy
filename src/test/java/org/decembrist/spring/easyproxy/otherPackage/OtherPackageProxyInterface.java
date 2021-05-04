package org.decembrist.spring.easyproxy.otherPackage;

import org.decembrist.spring.easyproxy.EasyProxyInterface;
import org.decembrist.spring.easyproxy.correctTest.TestInterceptor2;

@EasyProxyInterface(TestInterceptor2.class)
public interface OtherPackageProxyInterface {
}
