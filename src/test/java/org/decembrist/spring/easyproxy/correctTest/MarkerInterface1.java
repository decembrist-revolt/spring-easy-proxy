package org.decembrist.spring.easyproxy.correctTest;

import org.decembrist.spring.easyproxy.EasyProxyInterface;

@EasyProxyInterface(value = {TestInterceptor1.class})
public interface MarkerInterface1 {
}
