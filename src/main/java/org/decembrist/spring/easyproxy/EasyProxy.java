package org.decembrist.spring.easyproxy;

import org.aopalliance.aop.Advice;
import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * Annotation creates proxy for interface type in context
 * Use value interceptors to define proxy behaviour
 */
@Indexed
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface EasyProxy {

    Class<? extends Advice>[] value();

}
