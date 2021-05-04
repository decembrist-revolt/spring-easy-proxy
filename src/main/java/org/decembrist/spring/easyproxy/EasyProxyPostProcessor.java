package org.decembrist.spring.easyproxy;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Scans bean definitions to find classes with {@link EasyProxy} and {@link EasyProxyInterface} annotations
 * registers it as proxies
 */
@Component
@ConditionalOnProperty(prefix = "spring", value = "easy-proxy", havingValue = "true", matchIfMissing = true)
public class EasyProxyPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private BeanDefinitionRegistry registry;

    private boolean init = false;

    private final EasyProxyScanner scanner = new EasyProxyScanner();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        loadProxies(beanFactory);
    }

    private void loadProxies(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (registry == null) {
            throw new EasyProxyException("BeanDefinitionRegistry must not be null");
        }
        if (!init) {
            List<String> basePackages = AutoConfigurationPackages.get(beanFactory);
            Set<ScannedGenericBeanDefinition> proxyCandidates = scanner.findCandidateComponents(basePackages);
            proxyCandidates.forEach(definition -> makeProxy(definition, beanFactory, proxyCandidates));

            init = true;
        }
    }

    private void makeProxy(ScannedGenericBeanDefinition definition,
                           ConfigurableListableBeanFactory beanFactory,
                           Set<ScannedGenericBeanDefinition> proxyCandidates) {
        AnnotationMetadata metadata = definition.getMetadata();

        boolean isMarkerInterface = metadata.hasAnnotation(EasyProxyInterface.class.getName());
        if (!isMarkerInterface) {
            makeProxy(definition.getBeanClassName(), beanFactory, proxyCandidates, metadata);
        }
    }

    private void makeProxy(String beanClassName,
                           ConfigurableListableBeanFactory beanFactory,
                           Set<ScannedGenericBeanDefinition> proxyCandidates,
                           AnnotationMetadata metadata) {
        List<EasyProxyHandler> handlers;
        if (metadata.hasAnnotation(EasyProxy.class.getName())) {
            EasyProxyHandler handler = extractAttributesAsHandler(metadata, EasyProxy.class.getName());
            handlers = Collections.singletonList(handler);
        } else {
            handlers = handleProxyInterfaces(proxyCandidates, metadata);
        }
        ProxyFactory proxy = new ProxyFactory();
        proxy.setTarget(this);
        proxy.setInterfaces(getDefinitionCLass(beanClassName));
        handlers.stream()
                .map(handler -> handler.getAdvices(beanFactory))
                .flatMap(Collection::stream)
                .forEach(proxy::addAdvice);
        beanFactory.registerSingleton(beanClassName, proxy.getProxy());
    }

    private List<EasyProxyHandler> handleProxyInterfaces(Set<ScannedGenericBeanDefinition> proxyCandidates,
                                                         AnnotationMetadata metadata) {
        List<String> interfaces = Arrays.asList(metadata.getInterfaceNames());
        return proxyCandidates.stream()
                .filter(candidate -> interfaces.contains(candidate.getBeanClassName()))
                .filter(BeanDefinitionUtils::hasEasyProxyInterfaceAnnotation)
                .map(ScannedGenericBeanDefinition::getMetadata)
                .map(currMetadata -> extractAttributesAsHandler(currMetadata, EasyProxyInterface.class.getName()))
                .filter(Objects::nonNull)
                .collect(toList());
    }

    private Class<?> getDefinitionCLass(String beanClassName) {
        try {
            return Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public EasyProxyHandler extractAttributesAsHandler(AnnotationMetadata metadata, String annotationClass) {
        Class<? extends Advice>[] advices = BeanDefinitionUtils.retrieveAdvices(metadata, annotationClass);
        return advices == null ? null : new EasyProxyHandler(advices);
    }

}
