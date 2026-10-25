package com.py.ipconfig.dto;

import lombok.Data;

@Data
public class IpAccount {
    private String userName;
    private String clientId;
    private String brokerType;
    private Boolean active;
    private Boolean isOwn;
}
