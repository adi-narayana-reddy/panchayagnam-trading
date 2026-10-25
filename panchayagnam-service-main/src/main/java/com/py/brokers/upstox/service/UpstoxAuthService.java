package com.py.brokers.upstox.service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class UpstoxAuthService {

    private static final Logger log = LoggerFactory.getLogger(UpstoxAuthService.class);

    private final String CLIENT_ID = "d401560b-7c2c-4e17-b0b9-b5f25c7cc875";
    private final String CLIENT_SECRET = "c03rtim2h4";
    private final String REDIRECT_URI = "http://localhost:8081/callback";

    private final String TOTP_SECRET = "K4FWOHAIDXEE2DZ2AHLRVXTVP4ZUDBEF";
    private final String MOBILE_NUMBER = "7093498038";
    private final String MPIN = "123916";

    private CompletableFuture<String> tokenFuture = new CompletableFuture<>();

    @GetMapping("/get-token-auto")
    public String runAutomation() {
        log.info("Upstox automation started — fetching token via browser automation");
        tokenFuture = new CompletableFuture<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            String authUrl = "https://api.upstox.com/v2/login/authorization/dialog?client_id=" + CLIENT_ID
                    + "&redirect_uri=" + REDIRECT_URI + "&response_type=code";
            driver.get(authUrl);

            WebElement mobileField = wait.until(ExpectedConditions.elementToBeClickable(By.id("mobileNum")));
            mobileField.sendKeys(MOBILE_NUMBER);
            js.executeScript("arguments[0].click();", driver.findElement(By.id("getOtp")));

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            String totpCode = String.format("%06d", gAuth.getTotpPassword(TOTP_SECRET));
            WebElement otpField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("otpNum")));
            otpField.sendKeys(totpCode);
            js.executeScript("arguments[0].click();", driver.findElement(By.id("continueBtn")));

            WebElement pinField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pinCode")));
            pinField.sendKeys(MPIN);
            js.executeScript("arguments[0].click();", driver.findElement(By.id("pinContinueBtn")));

            return tokenFuture.get(20, TimeUnit.SECONDS);

        } catch (Exception e) {
            log.error("Upstox automation failed: {}", e.getMessage(), e);
            return "FAILED: " + e.getMessage();
        } finally {
            driver.quit();
        }
    }

    @GetMapping("/callback")
    public void callback(@RequestParam String code) {
        log.info("Upstox callback received — exchanging code for token");
        String tokenUrl = "https://api.upstox.com/v2/login/authorization/token";
        RestTemplate rest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("grant_type", "authorization_code");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<java.util.Map> response = rest.postForEntity(tokenUrl, request, java.util.Map.class);

            String token = (String) response.getBody().get("access_token");

            if (tokenFuture != null) {
                tokenFuture.complete(token);
            }

        } catch (Exception e) {
            if (tokenFuture != null) {
                tokenFuture.completeExceptionally(e);
            }
        }
    }

    public String getUpstoxtoken(String apikey, String clientSecret, String redirectUrl) {
        tokenFuture = new CompletableFuture<>();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-blink-features=AutomationControlled");

        WebDriver driver = new ChromeDriver(options);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) driver;

        try {
            String authUrl = "https://api.upstox.com/v2/login/authorization/dialog?client_id=" + apikey
                    + "&redirect_uri=" + redirectUrl + "&response_type=code";
            driver.get(authUrl);

            WebElement mobileField = wait.until(ExpectedConditions.elementToBeClickable(By.id("mobileNum")));
            mobileField.sendKeys(MOBILE_NUMBER);
            js.executeScript("arguments[0].click();", driver.findElement(By.id("getOtp")));

            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            String totpCode = String.format("%06d", gAuth.getTotpPassword(TOTP_SECRET));
            WebElement otpField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("otpNum")));
            otpField.sendKeys(totpCode);
            js.executeScript("arguments[0].click();", driver.findElement(By.id("continueBtn")));

            WebElement pinField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("pinCode")));
            pinField.sendKeys(MPIN);
            js.executeScript("arguments[0].click();", driver.findElement(By.id("pinContinueBtn")));

            // Use dynamic clientSecret in the callback exchange
            String token = tokenFuture.get(20, TimeUnit.SECONDS);
            exchangeCodeForToken(token, apikey, clientSecret, redirectUrl);

            return tokenFuture.get(20, TimeUnit.SECONDS);

        } catch (Exception e) {
            return "FAILED: " + e.getMessage();
        } finally {
            driver.quit();
        }
    }

    private void exchangeCodeForToken(String code, String apikey, String clientSecret, String redirectUrl) {
        String tokenUrl = "https://api.upstox.com/v2/login/authorization/token";
        RestTemplate rest = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", apikey);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUrl);
        params.add("grant_type", "authorization_code");

        try {
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
            ResponseEntity<java.util.Map> response = rest.postForEntity(tokenUrl, request, java.util.Map.class);
            String token = (String) response.getBody().get("access_token");
            if (tokenFuture != null) {
                tokenFuture.complete(token);
            }
        } catch (Exception e) {
            if (tokenFuture != null) {
                tokenFuture.completeExceptionally(e);
            }
        }
    }

}
