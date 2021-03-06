**Spring Easy Proxy**  
Library gives an ability to make spring interface proxy beans in easy way (Like spring data repositories do)

[![Java CI](https://github.com/decembrist-revolt/spring-easy-proxy/actions/workflows/test.yaml/badge.svg)](https://github.com/decembrist-revolt/spring-easy-proxy/actions/workflows/test.yaml)

**Getting started**  
_Maven:_  

    <dependency>
        <groupId>org.decembrist.spring</groupId>
        <artifactId>spring-easy-proxy</artifactId>
        <version>1.0.0</version>
    </dependency>
_Gradle:_  

    implementation "org.decembrist.spring:spring-easy-proxy:1.0.0"
_Example:_  
@EasyProxy  


    //1. Define interceptor bean (any org.aopalliance.aop.Advice class)
    @Component
    public class Interceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            // do some staff
            return invocation.proceed();
        }
    }

    //2. Use @EasyProxy annotation to create singleton proxy bean
    @EasyProxy({Interceptor.class}) //Interceptor array here
    public interface ProxyInterface {
        String proxyMethod(String param);
    }
    
    @Component
    class AnotherClass {
        //3. ProxyInterface above will be injected with Interceptor.class handler
        @Autowired private ProxyInterface proxy;
    }

@EasyProxyInterface  


    //1. Define interceptor bean (any org.aopalliance.aop.Advice class)
    @Component
    public class Interceptor implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            // do some staff
            return invocation.proceed();
        }
    }

    //2. Use @EasyProxyInterface annotation to create proxy marker
    @EasyProxyInterface({Interceptor.class}) //Interceptor array here
    public interface ProxyInterfaceMarker {
        String proxyMethod(String param);
    }

    //3. Extend marker interface to create singleton proxy bean
    public interface MarkedProxyInterface extends ProxyInterfaceMarker {
        String proxyMethod(String param);
    }

    @Component
    class AnotherClass {
        //4. MarkedProxyInterface above will be injected with Interceptor.class handler
        @Autowired private MarkedProxyInterface proxy;
    }

_Interceptor injection:_

    @Component
    public class TestInterceptor1 implements MethodInterceptor, ApplicationContextAware {
    
        private ApplicationContext applicationContext;
    
        //The only way to use another beans from interceptor for now
        @Override
        public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.applicationContext = applicationContext;
        }

_Checked:_
1. Works for kotlin
2. @EasyProxyInterface interfaces work as .jar spring boot library component
3. Tested with graalvm-ce-java11-21.0.0.2
4. Tested with java 8

_Properties:_

    #disable easy proxies
    spring.easy-proxy=false