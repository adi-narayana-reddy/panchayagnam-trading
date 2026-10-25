package com.py.demataccount.dto;

import com.py.common.enums.BrokerType;

public interface AccountIpProjection {
    String getClientId();
    BrokerType getBrokerType();
    Boolean getActive();
    Boolean getIsOwn();
    String getBrokerRegisteredIp();
    String getUserName();
}
