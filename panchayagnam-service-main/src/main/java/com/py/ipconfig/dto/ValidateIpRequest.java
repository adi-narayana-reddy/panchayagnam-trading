package com.py.ipconfig.dto;

import com.py.common.enums.BrokerType;
import lombok.Data;

@Data
public class ValidateIpRequest {
    private String ipAddress;
    private BrokerType brokerType;
}
