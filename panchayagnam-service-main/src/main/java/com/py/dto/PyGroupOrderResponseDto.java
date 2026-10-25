package com.py.dto;

import java.util.List;

public class PyGroupOrderResponseDto {

    private Long   groupId;
    private String groupName;
    private String transactionType;

    private int    stockCount;     // how many distinct stocks were ordered
    private int    totalOrders;    // stockCount × broker combinations attempted
    private long   successCount;
    private long   failedCount;
    private long   skippedCount;

    private List<PyGroupOrderResultDto> results;

    // Getters & Setters
    public Long getGroupId()  { return groupId; }
    public void setGroupId(Long groupId)  { this.groupId = groupId; }

    public String getGroupName()  { return groupName; }
    public void setGroupName(String groupName)  { this.groupName = groupName; }

    public String getTransactionType()  { return transactionType; }
    public void setTransactionType(String transactionType)  { this.transactionType = transactionType; }

    public int getStockCount()  { return stockCount; }
    public void setStockCount(int stockCount)  { this.stockCount = stockCount; }

    public int getTotalOrders()  { return totalOrders; }
    public void setTotalOrders(int totalOrders)  { this.totalOrders = totalOrders; }

    public long getSuccessCount()  { return successCount; }
    public void setSuccessCount(long successCount)  { this.successCount = successCount; }

    public long getFailedCount()  { return failedCount; }
    public void setFailedCount(long failedCount)  { this.failedCount = failedCount; }

    public long getSkippedCount()  { return skippedCount; }
    public void setSkippedCount(long skippedCount)  { this.skippedCount = skippedCount; }

    public List<PyGroupOrderResultDto> getResults()  { return results; }
    public void setResults(List<PyGroupOrderResultDto> results)  { this.results = results; }
}