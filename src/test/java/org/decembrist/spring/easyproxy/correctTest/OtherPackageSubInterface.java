package org.decembrist.spring.easyproxy.correctTest;

import org.decembrist.spring.easyproxy.otherPackage.OtherPackageProxyInterface;

public interface OtherPackageSubInterface extends OtherPackageProxyInterface {

    String proxyMethod(String param);

}
