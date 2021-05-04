package org.decembrist.spring.easyproxy;

import org.aopalliance.aop.Advice;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

public class BeanDefinitionUtils {

    public static boolean hasEasyProxyInterfaceAnnotation(ScannedGenericBeanDefinition definition) {
        return definition.getMetadata().hasAnnotation(EasyProxyInterface.class.getName());
    }

    public static boolean hasEasyProxyAnnotation(ScannedGenericBeanDefinition definition) {
        return definition.getMetadata().hasAnnotation(EasyProxyInterface.class.getName());
    }

    /**
     * Retrieve advices annotation attribute
     */
    @SuppressWarnings("unchecked")
    public static Class<? extends Advice>[] retrieveAdvices(AnnotationMetadata metadata, String annotationClass) {
        Map<String, Object> easyProxyInterceptors = metadata.getAnnotationAttributes(annotationClass);
        return easyProxyInterceptors == null ? null : (Class<? extends Advice>[]) easyProxyInterceptors.get("value");
    }

}
