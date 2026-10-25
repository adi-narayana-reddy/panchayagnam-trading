package com.py.ipconfig.dto;

import lombok.Data;
import java.util.List;

@Data
public class IpReport {
    private String ipAddress;
    private Integer portNo;
    private List<String> brokerTypes; // distinct broker types for this IP
    private List<IpAccount> accounts; // accounts assigned to this IP
}
