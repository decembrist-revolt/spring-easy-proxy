package org.decembrist.spring.easyproxy;

import org.aopalliance.aop.Advice;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * Annotation marks interface and for every sub interface creates proxy in context
 * Use value interceptors to define proxy behaviour
 */
@Indexed
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EasyProxyInterface {

    Class<? extends Advice>[] value();

}
