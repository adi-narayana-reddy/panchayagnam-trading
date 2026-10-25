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

    // Changed property keys to standard lowercase kebab-case
    @Value("${twilio.api-key-sid:}")
    private String apiKeySid;

    @Value("${twilio.api-secret:}")
    private String apiSecret;

    @Value("${twilio.account-sid:}")
    private String accountSid;

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @PostConstruct
    public void initTwilio() {
        if (twilioEnabled && isNotEmpty(apiKeySid) && isNotEmpty(apiSecret) && isNotEmpty(accountSid)) {

            // Crucial Debug Log: Verify string format prefixes before initializing
            log.info("Initializing Twilio with Account SID starting with: {}, Key SID starting with: {}",
                    accountSid.substring(0, Math.min(accountSid.length(), 6)),
                    apiKeySid.substring(0, Math.min(apiKeySid.length(), 6)));

            // Explicitly map: Username (Key SID), Password (Secret Key), Account Context (Account SID)
            Twilio.init(apiKeySid, apiSecret, accountSid);

            log.info("Twilio SDK initialized successfully using API Key architecture");
        } else {
            log.warn("Twilio SDK NOT initialized — twilioEnabled={}, hasAccountSid={}, hasKeySid={}, hasSecret={}",
                    twilioEnabled, isNotEmpty(accountSid), isNotEmpty(apiKeySid), isNotEmpty(apiSecret));
        }
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
}