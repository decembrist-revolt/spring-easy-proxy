package org.decembrist.spring.easyproxy;

import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class EasyProxyHandler {

    private final Class<? extends Advice>[] adviceClasses;

    public EasyProxyHandler(Class<? extends Advice>[] adviceClasses) {
        this.adviceClasses = adviceClasses;
    }

    public List<? extends Advice> getAdvices(ConfigurableListableBeanFactory beanFactory) {
        return Arrays.stream(adviceClasses).map(beanFactory::getBean).collect(Collectors.toList());
    }
}
