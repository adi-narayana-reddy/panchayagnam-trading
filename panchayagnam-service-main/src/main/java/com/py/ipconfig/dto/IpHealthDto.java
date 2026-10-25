package com.py.ipconfig.dto;

import lombok.Data;

@Data
public class IpHealthDto {
    private String ip;
    private String status;
    private String message;
    private String timestamp;
}
