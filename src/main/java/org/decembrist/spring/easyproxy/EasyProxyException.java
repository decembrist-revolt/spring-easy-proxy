package org.decembrist.spring.easyproxy;

import org.springframework.beans.BeansException;

class EasyProxyException extends BeansException {
    public EasyProxyException(String msg) {
        super(msg);
    }
}
