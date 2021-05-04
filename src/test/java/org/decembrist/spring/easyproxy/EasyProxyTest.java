package org.decembrist.spring.easyproxy;

import org.decembrist.spring.easyproxy.correctTest.*;
import org.decembrist.spring.easyproxy.otherPackage.OtherPackageProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

public class EasyProxyTest {

    @Test
    public void correctTest() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext("org.decembrist.spring.easyproxy.correctTest");
        ProxyInterface bean = context.getBean(ProxyInterface.class);
        String testParam = "test param";
        String result = bean.proxyMethod(testParam);
        Assertions.assertEquals("TestInterceptor2", result);
        TestInterceptor1 interceptor1 = context.getBean(TestInterceptor1.class);
        TestInterceptor2 interceptor2 = context.getBean(TestInterceptor2.class);
        Assertions.assertEquals(1, interceptor1.params.size());
        Assertions.assertEquals(testParam, interceptor1.params.get(0));
        Assertions.assertEquals(1, interceptor2.invokes.size());
        Assertions.assertEquals("proxyMethod", interceptor2.invokes.get(0));

        MarkedProxyInterface markedProxyInterface = context.getBean(MarkedProxyInterface.class);
        result = markedProxyInterface.proxyMethod2(testParam);
        Assertions.assertEquals("TestInterceptor2", result);
        Assertions.assertEquals(2, interceptor1.params.size());
        Assertions.assertEquals(testParam, interceptor1.params.get(1));
        Assertions.assertEquals(2, interceptor2.invokes.size());
        Assertions.assertEquals("proxyMethod2", interceptor2.invokes.get(1));
    }

    @Test
    public void otherPackageTest() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext("org.decembrist.spring.easyproxy.correctTest");
        OtherPackageSubInterface bean1 = context.getBean(OtherPackageSubInterface.class);
        String testParam = "test param";
        String result = bean1.proxyMethod(testParam);
        Assertions.assertEquals("TestInterceptor2", result);
        TestInterceptor2 interceptor = context.getBean(TestInterceptor2.class);
        Assertions.assertEquals(1, interceptor.invokes.size());
        Assertions.assertEquals("proxyMethod", interceptor.invokes.get(0));

        OtherPackageProxy bean2 = context.getBean(OtherPackageProxy.class);
        result = bean2.proxyMethod(testParam);
        Assertions.assertEquals("TestInterceptor2", result);
        Assertions.assertEquals(2, interceptor.invokes.size());
        Assertions.assertEquals("proxyMethod", interceptor.invokes.get(1));
    }

    @Test
    public void singletonTest() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext("org.decembrist.spring.easyproxy.correctTest");
        Object proxy1 = context.getBean(ProxyInterface.class);
        Object proxy2 = context.getBean(ProxyInterface.class);
        Assertions.assertEquals(proxy1, proxy2);
    }

    @Test
    public void markedInterfacesNotBeanTest() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext("org.decembrist.spring.easyproxy.correctTest");
        Object proxy1 = context.getBean(MarkerInterface1.class);
        Object proxy2 = context.getBean(MarkerInterface2.class);
        Object proxy3 = context.getBean(MarkedProxyInterface.class);
        Assertions.assertEquals(proxy1, proxy3);
        Assertions.assertEquals(proxy2, proxy3);
    }

    @Test
    public void propertyOkTest() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        MutablePropertySources propertySources = new MutablePropertySources();
        PropertySource propertySource = new PropertySource("config") {
            @Override
            public Object getProperty(String name) {
                if ("spring.easy-proxy".equals(name)) {
                    return "true";
                }
                return null;
            }
        };
        propertySources.addFirst(propertySource);
        StandardEnvironment spyEnv = new StandardEnvironment(propertySources) {
        };
        context.setEnvironment(spyEnv);
        context.scan("org.decembrist.spring.easyproxy.correctTest");
        context.refresh();
        context.getBean(ProxyInterface.class);
    }

    @Test
    public void propertyBadTest() {
        AnnotationConfigApplicationContext context
                = new AnnotationConfigApplicationContext();
        MutablePropertySources propertySources = new MutablePropertySources();
        PropertySource propertySource = new PropertySource("config") {
            @Override
            public Object getProperty(String name) {
                if ("spring.easy-proxy".equals(name)) {
                    return "false";
                }
                return null;
            }
        };
        propertySources.addFirst(propertySource);
        StandardEnvironment spyEnv = new StandardEnvironment(propertySources) {
        };
        context.setEnvironment(spyEnv);
        context.scan("org.decembrist.spring.easyproxy.correctTest");
        context.refresh();
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, () -> context.getBean(ProxyInterface.class));
    }

}
