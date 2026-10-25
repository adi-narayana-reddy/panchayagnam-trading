package com.py.common.config;

import com.twilio.Twilio;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class TwilioConfig {

    private static final Logger log = LoggerFactory.getLogger(TwilioConfig.class);

    @Value("${twilio.auth-token:}")
    private String authToken;

    @Value("${twilio.api-key-sid:}")
    private String apiKeySid;

    @Value("${twilio.api-secret:}")
    private String apiSecret;

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    @PostConstruct
    public void initTwilio() {
        if (!twilioEnabled) {
            log.info("Twilio OTP service is disabled (twilio.enabled=false).");
            return;
        }

        if (isNotEmpty(accountSid) && isNotEmpty(authToken)) {
            // Standard Auth: Account SID + Auth Token
            log.info("Initializing Twilio with Account SID starting with: {} using Auth Token",
                    accountSid.substring(0, Math.min(accountSid.length(), 6)));
            Twilio.init(accountSid.trim(), authToken.trim());
            initialized = true;
            log.info("Twilio SDK initialized successfully using standard Account SID + Auth Token");
        } else if (isNotEmpty(apiKeySid) && isNotEmpty(apiSecret) && isNotEmpty(accountSid)) {
            // API Key architecture: Key SID + Secret + Account SID
            log.info("Initializing Twilio with Account SID starting with: {}, Key SID starting with: {}",
                    accountSid.substring(0, Math.min(accountSid.length(), 6)),
                    apiKeySid.substring(0, Math.min(apiKeySid.length(), 6)));
            Twilio.init(apiKeySid.trim(), apiSecret.trim(), accountSid.trim());
            initialized = true;
            log.info("Twilio SDK initialized successfully using API Key architecture");
        } else {
            log.warn("Twilio SDK NOT initialized - twilioEnabled={}, hasAccountSid={}, hasAuthToken={}, hasKeySid={}, hasSecret={}",
                    twilioEnabled, isNotEmpty(accountSid), isNotEmpty(authToken), isNotEmpty(apiKeySid), isNotEmpty(apiSecret));
        }
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}