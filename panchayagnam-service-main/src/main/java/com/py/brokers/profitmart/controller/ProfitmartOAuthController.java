package com.py.brokers.profitmart.controller;

import com.py.brokers.profitmart.dto.ProfitMartAccessTokenGenerateDto;
import com.py.brokers.profitmart.service.ProfitmartOAuthService;
import com.py.common.util.TotpUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.py.demataccount.repository.PyBrokerAccountRepository;
import java.io.IOException;
import org.json.JSONException;

/**
 * Profitmart OAuth Controller
 *
 * Two endpoints:
 *
 * 1. GET /profitmart/oauth/login?clientId=PM12345 → Server redirects browser
 * directly to Profitmart login page (302) → Use this when opening from a
 * browser tab / webview
 *
 * 2. GET /profitmart/oauth/login-url?clientId=PM12345 → Returns the login URL
 * as JSON → Use this when frontend needs the URL to open in a new tab
 */
@RestController
@RequestMapping("/profitmart/oauth")
public class ProfitmartOAuthController {

    private static final Logger log = LoggerFactory.getLogger(ProfitmartOAuthController.class);

    @Autowired
    private ProfitmartOAuthService profitmartOAuthService;

    @Autowired
    private PyBrokerAccountRepository pyBrokerAccountRepository;

    @Autowired
    private TotpUtil totpUtil;

    // ─────────────────────────────────────────────────────────────────────────
    // Option A — Direct browser redirect (302)
    // Call this from browser / Flutter webview / mobile app
    // GET /profitmart/oauth/login?clientId=PM12345
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/login")
    public void redirectToLogin(
            @RequestParam("clientId") String clientId,
            HttpServletResponse httpResponse) throws IOException {

        log.info("🔄 Profitmart login redirect | clientId={}", clientId);

        try {
            JSONObject result = profitmartOAuthService.returnLoginUrl(clientId);
            String loginUrl = result.getString("loginUrl");

            log.info("✅ Redirecting to Profitmart login | clientId={} url={}",
                    clientId, loginUrl);

            // 302 redirect — browser navigates to Profitmart login page
            httpResponse.sendRedirect(loginUrl);

        } catch (Exception e) {
            log.error("❌ Failed to build Profitmart login URL | clientId={} error={}",
                    clientId, e.getMessage());
            httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Failed to build login URL: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Option B — Return login URL as JSON
    // Call this from React / Flutter to open URL manually in a new tab
    // GET /profitmart/oauth/login-url?clientId=PM12345
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping({"/login-url", "/getProfitLoginUrl"})
    public ResponseEntity<String> getLoginUrl(
            @RequestParam("clientId") String clientId) {

        log.info("🔗 Profitmart login URL request | clientId={}", clientId);

        try {
            JSONObject result = profitmartOAuthService.returnLoginUrl(clientId);
            return ResponseEntity.ok(result.toString());

        } catch (Exception e) {
            log.error("❌ Failed to get Profitmart login URL | clientId={} error={}",
                    clientId, e.getMessage());

            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error.toString());
        }
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestParam("code") String code) {

        log.info("Profitmart callback received | code={}", code);

        try {
            // 1. Use code to get access token from Profitmart
            // 2. Save token to pybrokertoken table
//            JSONObject result = profitmartOAuthService.fetchAndSaveToken(code);

            return ResponseEntity.ok("");

        } catch (Exception e) {
            log.error("Callback failed | code={} error={}", code, e.getMessage(), e);

            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.internalServerError().body(error.toString(2));
        }
    }

    @GetMapping("/generate-totp")
    public ResponseEntity<String> generateTotp(
            @RequestParam(value = "totpKey", required = false) String totpKey,
            @RequestParam(value = "clientId", required = false) String clientId) {

        log.info("Profitmart generate TOTP request | clientId={} totpKey=[REDACTED]",
                (clientId != null && !clientId.isBlank()) ? clientId : "N/A");

        try {
            String keyToUse = totpKey;
            if ((keyToUse == null || keyToUse.isBlank()) && (clientId != null && !clientId.isBlank())) {
                keyToUse = pyBrokerAccountRepository.findByClientId(clientId)
                        .filter(acc -> acc.getSecret() != null)
                        .map(acc -> acc.getSecret().getTotpSecret())
                        .orElse(null);
            }

            if (keyToUse == null || keyToUse.isBlank()) {
                JSONObject error = new JSONObject();
                error.put("status", "error");
                error.put("message", "No TOTP secret available for the specified account");
                return ResponseEntity.badRequest().body(error.toString());
            }

            String totp = totpUtil.generate(keyToUse);

            JSONObject result = new JSONObject();
            result.put("totp", totp);

            log.info("Profitmart TOTP generated successfully");
            return ResponseEntity.ok(result.toString());

        } catch (Exception e) {
            log.error("Profitmart TOTP generation failed | error={}", e.getMessage());
            JSONObject error = new JSONObject();
            error.put("status", "error");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error.toString());
        }
    }

    @PostMapping({"/login", "/getAckTkn"})
    public ResponseEntity<String> login(@RequestBody ProfitMartAccessTokenGenerateDto dto) {

        log.info("Profitmart OAuth login | userId={}", dto.getUserId());

        try {
            JSONObject result = profitmartOAuthService.getAccessToken(dto);
            return ResponseEntity.ok(result.toString());

        } catch (Exception e) {
            log.error("Profitmart OAuth login failed | userId={} | error={}", dto.getUserId(), e.getMessage());
            JSONObject error = new JSONObject();
            error.put("status", "failed");
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error.toString());
        }
    }

}
