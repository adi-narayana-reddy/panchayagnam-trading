/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.py.brokers.profitmart.dto;

/**
 *
 * @author abcom
 */
public class ProfitMartAccessTokenGenerateDto {
    private String code;
    private String ip;
    private String seceretKey;
    private String userId;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the ip
     */
    public String getIp() {
        return ip;
    }

    /**
     * @param ip the ip to set
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * @return the seceretKey
     */
    public String getSeceretKey() {
        return seceretKey;
    }

    /**
     * @param seceretKey the seceretKey to set
     */
    public void setSeceretKey(String seceretKey) {
        this.seceretKey = seceretKey;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
}
