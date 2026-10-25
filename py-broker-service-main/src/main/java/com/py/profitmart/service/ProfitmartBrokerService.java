package com.py.profitmart.service;

import com.py.profitmart.dto.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfitmartBrokerService {

    private static final Logger log = LoggerFactory.getLogger(ProfitmartBrokerService.class);

    @Autowired
    private ProfitmartAccountService profitmartAccountService;

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public JSONObject login(ProfitmartLoginRequest request) {
        if (request == null) {
            JSONObject err = new JSONObject();
            err.put("stat", "Not_Ok");
            err.put("emsg", "Empty login request");
            return err;
        }

        // Check if OAuth code/checksum login
        if (request.getCode() != null && !request.getCode().isBlank()) {
            return profitmartAccountService.exchangeOAuthToken(
                    request.getUserId(),
                    request.getCode(),
                    request.getChecksum());
        }

        // Direct credentials login
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            JSONObject err = new JSONObject();
            err.put("stat", "Not_Ok");
            err.put("emsg", "Neither OAuth authorization code nor password was provided");
            return err;
        }

        return profitmartAccountService.login(
                request.getUserId(),
                request.getPassword(),
                request.getTwoFa(),
                request.getVendorCode(),
                request.getApiSecret(),
                request.getImei());
    }

    // ── LOGOUT ────────────────────────────────────────────────────────────────
    public JSONObject logout(ProfitmartBaseRequest request) {
        return profitmartAccountService.logout(
                request.getUserId(),
                request.getSusertoken());
    }

    // ── ORDER BOOK ────────────────────────────────────────────────────────────
    public JSONArray getOrderBook(ProfitmartBaseRequest request) {
        return profitmartAccountService.getOrderBook(
                request.getUserId(),
                request.getSusertoken());
    }

    // ── TRADE BOOK ────────────────────────────────────────────────────────────
    public JSONArray getTradeBook(ProfitmartBaseRequest request) {
        return profitmartAccountService.getTradeBook(
                request.getUserId(),
                request.getSusertoken());
    }

    // ── ORDER HISTORY ─────────────────────────────────────────────────────────
    public JSONArray getOrderHistory(ProfitmartOrderHistoryRequest request) {
        return profitmartAccountService.getOrderHistory(
                request.getUserId(),
                request.getSusertoken(),
                request.getOrderNo());
    }

    // ── PLACE ORDER ───────────────────────────────────────────────────────────
    public JSONObject placeOrder(ProfitmartPlaceOrderRequest request) {
        return profitmartAccountService.placeOrder(
                request.getUserId(),
                request.getSusertoken(),
                request.getExchange(),
                request.getTradingSymbol(),
                request.getBuyOrSell(),
                request.getQuantity(),
                request.getDiscloseQty(),
                request.getPriceType(),
                request.getPrice(),
                request.getTriggerPrice(),
                request.getProductType(),
                request.getRetention(),
                request.getRemarks(),
                request.getAmo());
    }

    // ── MODIFY ORDER ──────────────────────────────────────────────────────────
    public JSONObject modifyOrder(ProfitmartModifyOrderRequest request) {
        return profitmartAccountService.modifyOrder(
                request.getUserId(),
                request.getSusertoken(),
                request.getOrderNo(),
                request.getExchange(),
                request.getTradingSymbol(),
                request.getNewQuantity(),
                request.getNewPriceType(),
                request.getNewPrice(),
                request.getNewTriggerPrice(),
                request.getRetention(),
                request.getAmo());
    }

    // ── CANCEL ORDER ──────────────────────────────────────────────────────────
    public JSONObject cancelOrder(ProfitmartCancelOrderRequest request) {
        return profitmartAccountService.cancelOrder(
                request.getUserId(),
                request.getSusertoken(),
                request.getOrderNo());
    }

    // ── HOLDINGS ──────────────────────────────────────────────────────────────
    public JSONArray getHoldings(ProfitmartBaseRequest request) {
        return profitmartAccountService.getHoldings(
                request.getUserId(),
                request.getSusertoken());
    }

    // ── POSITIONS ─────────────────────────────────────────────────────────────
    public JSONArray getPositions(ProfitmartBaseRequest request) {
        return profitmartAccountService.getPositions(
                request.getUserId(),
                request.getSusertoken());
    }

    // ── LIMITS ────────────────────────────────────────────────────────────────
    public JSONObject getLimits(ProfitmartBaseRequest request) {
        return profitmartAccountService.getLimits(
                request.getUserId(),
                request.getSusertoken());
    }

    // ── SEARCH SCRIP ──────────────────────────────────────────────────────────
    public JSONObject searchScrip(ProfitmartSearchRequest request) {
        return profitmartAccountService.searchScrip(
                request.getUserId(),
                request.getSusertoken(),
                request.getExchange(),
                request.getSearchText());
    }

    // ── QUOTES ────────────────────────────────────────────────────────────────
    public JSONObject getQuotes(ProfitmartQuotesRequest request) {
        return profitmartAccountService.getQuotes(
                request.getUserId(),
                request.getSusertoken(),
                request.getExchange(),
                request.getToken());
    }

    // ── SECURITY INFO ─────────────────────────────────────────────────────────
    public JSONObject getSecurityInfo(ProfitmartQuotesRequest request) {
        return profitmartAccountService.getSecurityInfo(
                request.getUserId(),
                request.getSusertoken(),
                request.getExchange(),
                request.getToken());
    }
}
