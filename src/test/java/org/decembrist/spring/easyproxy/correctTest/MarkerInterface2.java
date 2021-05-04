package org.decembrist.spring.easyproxy.correctTest;

import org.decembrist.spring.easyproxy.EasyProxyInterface;

@EasyProxyInterface(value = {TestInterceptor2.class})
public interface MarkerInterface2 {
}
