package com.py.demataccount.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.py.common.enums.BrokerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "Request to add/update/delete a demat account")
@Data
public class DematAccountAddRequest {

    private String clientId;

    private String apiKey;

    private String mpin;

    private String totpReferenceKey;

    private BrokerType brokerType;

    private String dob;

    private String apiSecretKey;

    @JsonProperty("isAdmin")
    private boolean admin;

    private List<Long> childIds;

    private String nickName;

    @JsonProperty("isActive")
    private boolean active;

    private String vendorInfo;

    private String twoFa;

    private String mobileNumber;

    private String pin;

    private String redirectUri;

    private String ip;

    @JsonProperty("isOwn")
    private boolean own;

    private long pyUserId;
}
