package com.py.demataccount.dto;


import com.py.common.enums.BrokerType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Request to add/update/delete a broker account")
public class PyBrokerAccountRequestDto {

    @Schema(description = "User ID who is adding this broker account", example = "10000")
    private Long pyId;

    @Schema(description = "Broker client ID", example = "D12345")
    private String clientId;

    @Schema(description = "Broker name", example = "ANGEL_ONE")
    private BrokerType brokerName;

    @Schema(description = "Nickname for the account", example = "MyTradingAccount")
    private String nickName;

    @Schema(description = "Whether account is admin", example = "false")
    private boolean isAdmin;

    @Schema(description = "Whether account is active", example = "true")
    private boolean isActive = true;

    // ── SECRET FIELDS ────────────────────────────────────────────
    @Schema(description = "API key for broker", example = "KQWg34zy")
    private String apiKey;

    @Schema(description = "API secret key", example = "secret-key-123")
    private String apiSecret;

    @Schema(description = "MPIN for broker login", example = "1234")
    private String mpin;

    @Schema(description = "TOTP reference key for 2FA", example = "ABCD1234EFGH5678")
    private String totpSecret;

    @Schema(description = "Password for broker login", example = "mypassword")
    private String password;

    @Schema(description = "App ID for broker", example = "APP123")
    private String appId;

    @Schema(description = "Vendor info for Motilal", example = "EBOM879345")
    private String vendorInfo;

    @Schema(description = "2FA answer for Motilal login", example = "Hyderabad")
    private String twoFa;

    @Schema(description = "List of child demat account IDs", example = "[2, 3]")
    private List<Long> childIds;

    // Getters and Setters
    public Long getPyId() { return pyId; }
    public void setPyId(Long pyId) { this.pyId = pyId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public BrokerType getBrokerName() { return brokerName; }
    public void setBrokerName(BrokerType brokerName) { this.brokerName = brokerName; }

    public String getNickName() { return nickName; }
    public void setNickName(String nickName) { this.nickName = nickName; }

    public boolean isIsAdmin() { return isAdmin; }
    public void setIsAdmin(boolean isAdmin) { this.isAdmin = isAdmin; }

    public boolean isIsActive() { return isActive; }
    public void setIsActive(boolean isActive) { this.isActive = isActive; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getApiSecret() { return apiSecret; }
    public void setApiSecret(String apiSecret) { this.apiSecret = apiSecret; }

    public String getMpin() { return mpin; }
    public void setMpin(String mpin) { this.mpin = mpin; }

    public String getTotpSecret() { return totpSecret; }
    public void setTotpSecret(String totpSecret) { this.totpSecret = totpSecret; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getVendorInfo() { return vendorInfo; }
    public void setVendorInfo(String vendorInfo) { this.vendorInfo = vendorInfo; }

    public String getTwoFa() { return twoFa; }
    public void setTwoFa(String twoFa) { this.twoFa = twoFa; }

    public List<Long> getChildIds() { return childIds; }
    public void setChildIds(List<Long> childIds) { this.childIds = childIds; }
}
