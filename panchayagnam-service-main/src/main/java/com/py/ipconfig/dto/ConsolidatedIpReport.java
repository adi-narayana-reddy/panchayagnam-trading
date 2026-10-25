package com.py.ipconfig.dto;

import lombok.Data;
import java.util.List;

@Data
public class ConsolidatedIpReport {
    private long totalIpCount;
    private long usedIpCount;
    private long unusedIpCount;
    private List<IpReport> details;
}
