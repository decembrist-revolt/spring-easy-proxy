package org.decembrist.spring.easyproxy;

import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EasyProxyScanner extends ClassPathScanningCandidateComponentProvider {

    private final List<String> scannedPackages = new ArrayList<>();

    public EasyProxyScanner() {
        super(false);
        addIncludeFilter(new AnnotationTypeFilter(EasyProxy.class, true, true));
        addIncludeFilter(new AnnotationTypeFilter(EasyProxyInterface.class, true, true));
    }

    /**
     * Any independent interface could be proxied
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        AnnotationMetadata metadata = beanDefinition.getMetadata();
        return metadata.isIndependent() && metadata.isInterface();
    }

    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {
        Set<ScannedGenericBeanDefinition> definitions = super.findCandidateComponents(basePackage).stream()
                .filter(definition -> definition instanceof ScannedGenericBeanDefinition)
                .map(definition -> (ScannedGenericBeanDefinition) definition)
                .collect(Collectors.toSet());
        scannedPackages.add(basePackage);
        List<String> dependentPackages = retrieveDependentPackages(definitions);
        Set<ScannedGenericBeanDefinition> dependentDefinitions = findCandidateComponents(dependentPackages);
        definitions.addAll(dependentDefinitions);
        List<String> interceptorPackages = retrieveInterceptorPackages(definitions);
        Set<ScannedGenericBeanDefinition> interceptorDefinitions = findCandidateComponents(interceptorPackages);
        definitions.addAll(interceptorDefinitions);
        return new HashSet<>(definitions);
    }

    /**
     * @param basePackages  packages to scan
     * @return found candidates with easy proxy criteria
     */
    public Set<ScannedGenericBeanDefinition> findCandidateComponents(List<String> basePackages) {
        return basePackages.stream()
                .map(this::findCandidateComponents)
                .flatMap(Set::stream)
                .map(definition -> (ScannedGenericBeanDefinition) definition)
                .collect(Collectors.toSet());
    }

    private List<String> retrieveDependentPackages(Set<ScannedGenericBeanDefinition> definitions) {
        return definitions.stream()
                .map(definition -> definition.getMetadata().getInterfaceNames())
                .flatMap(Arrays::stream)
                .map(interfaceName -> interfaceName.substring(0, interfaceName.lastIndexOf(".")))
                .filter(this::isNotPackageScanned)
                .collect(Collectors.toList());
    }

    private boolean isNotPackageScanned(String interfaceName) {
        return scannedPackages.stream()
                .noneMatch(interfaceName::contains);
    }

    private List<String> retrieveInterceptorPackages(Set<ScannedGenericBeanDefinition> definitions) {
        return definitions.stream()
                .filter(this::hasEasyProxyAnnotations)
                .map(this::retrieveInterceptorPackages)
                .flatMap(List::stream)
                .filter(this::isNotPackageScanned)
                .collect(Collectors.toList());
    }

    private boolean hasEasyProxyAnnotations(ScannedGenericBeanDefinition definition) {
        return BeanDefinitionUtils.hasEasyProxyAnnotation(definition)
                || BeanDefinitionUtils.hasEasyProxyInterfaceAnnotation(definition);
    }

    private List<String> retrieveInterceptorPackages(ScannedGenericBeanDefinition definition) {
        Class<? extends Advice>[] easyProxyAdvices = BeanDefinitionUtils
                .retrieveAdvices(definition.getMetadata(), EasyProxy.class.getName());
        Class<? extends Advice>[] easyProxyInterfaceAdvices = BeanDefinitionUtils
                .retrieveAdvices(definition.getMetadata(), EasyProxyInterface.class.getName());
        return Stream.of(easyProxyAdvices, easyProxyInterfaceAdvices)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .map(Class::getPackage)
                .map(Package::getName)
                .collect(Collectors.toList());
    }
}
