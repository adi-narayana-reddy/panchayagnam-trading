package com.py.dto;

import com.py.common.enums.BrokerType;

public class PyGroupOrderResultDto {

    // Stock context
    private String tradingSymbol;
    private String symbolToken;
    private String exchange;
    private double price;

    // Broker context
    private Long   brokerId;
    private String clientId;
    private String userName;
    private BrokerType brokerType;

    // Order outcome
    private Integer quantity;
    private String  status;    // SUCCESS | FAILED | SKIPPED
    private String  orderId;
    private String  remarks;

    // Getters & Setters
    public String getTradingSymbol()  { return tradingSymbol; }
    public void setTradingSymbol(String tradingSymbol)  { this.tradingSymbol = tradingSymbol; }

    public String getSymbolToken()  { return symbolToken; }
    public void setSymbolToken(String symbolToken)  { this.symbolToken = symbolToken; }

    public String getExchange()  { return exchange; }
    public void setExchange(String exchange)  { this.exchange = exchange; }

    public Long getBrokerId()  { return brokerId; }
    public void setBrokerId(Long brokerId)  { this.brokerId = brokerId; }

    public String getClientId()  { return clientId; }
    public void setClientId(String clientId)  { this.clientId = clientId; }

    public String getUserName()  { return userName; }
    public void setUserName(String userName)  { this.userName = userName; }

    public BrokerType getBrokerType()  { return brokerType; }
    public void setBrokerType(BrokerType brokerType)  { this.brokerType = brokerType; }

    public Integer getQuantity()  { return quantity; }
    public void setQuantity(Integer quantity)  { this.quantity = quantity; }

    public String getStatus()  { return status; }
    public void setStatus(String status)  { this.status = status; }

    public String getOrderId()  { return orderId; }
    public void setOrderId(String orderId)  { this.orderId = orderId; }

    public String getRemarks()  { return remarks; }
    public void setRemarks(String remarks)  { this.remarks = remarks; }

    /**
     * @return the price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }
}