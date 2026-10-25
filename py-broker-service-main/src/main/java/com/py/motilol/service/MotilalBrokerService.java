package com.py.motilol.service;

import com.py.motilol.dto.MotilalBaseRequest;
import com.py.motilol.dto.MotilalCancelOrderRequest;
import com.py.motilol.dto.MotilalConvertPositionRequest;
import com.py.motilol.dto.MotilalLoginRequest;
import com.py.motilol.dto.MotilalLtpRequest;
import com.py.motilol.dto.MotilalModifyOrderParams;
import com.py.motilol.dto.MotilalModifyOrderRequest;
import com.py.motilol.dto.MotilalPlaceOrderRequest;
import com.py.motilol.dto.MotilalTokenRequest;
import com.py.motilol.dto.MotilolOrderParams;
import com.py.motilol.http.MotilalAccountService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MotilalBrokerService {

    private static final Logger log = LoggerFactory.getLogger(MotilalBrokerService.class);

    @Autowired
    private MotilalAccountService motilalAccountService;

    // ── LOGIN ─────────────────────────────────────────────────────────────────
    public JSONObject login(MotilalLoginRequest request) {
        return motilalAccountService.login(
                request.getUserId(),
                request.getHashedPassword(),
                request.getTwoFa(),
                request.getTotp(),
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo());
    }

    // ── GET ACCESS TOKEN ──────────────────────────────────────────────────────
    public JSONObject getAccessToken(MotilalTokenRequest request) {
        return motilalAccountService.getAccessToken(
                request.getAuthToken(),
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo());
    }

    // ── RMS ───────────────────────────────────────────────────────────────────
    public JSONObject getRMS(MotilalBaseRequest request) {
        return motilalAccountService.getRMS(
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── ORDER HISTORY ─────────────────────────────────────────────────────────
    public JSONObject getOrderBook(MotilalBaseRequest request) {
        return motilalAccountService.getOrderBook(
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── LTP ───────────────────────────────────────────────────────────────────
    public JSONObject getLtp(MotilalLtpRequest request) {
        return motilalAccountService.getLtp(
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken(),
                request.getExchange(),
                request.getScripCode());
    }

    // ── PLACE ORDER ───────────────────────────────────────────────────────────
    public JSONObject placeOrder(MotilalPlaceOrderRequest request) {
        MotilolOrderParams params = new MotilolOrderParams();
        params.setExchange(request.getExchange());
        params.setSymbolToken(request.getSymbolToken());
        params.setBuyOrSell(request.getBuyOrSell());
        params.setOrderType(request.getOrderType());
        params.setProductType(request.getProductType());
        params.setOrderDuration(request.getOrderDuration());
        params.setPrice(request.getPrice());
        params.setTriggerPrice(request.getTriggerPrice());
        params.setQuantityInLot(request.getQuantityInLot());
        params.setDisclosedQuantity(request.getDisclosedQuantity());
        params.setAmoOrder(request.getAmoOrder());
        params.setGoodTillDate(request.getGoodTillDate());
        params.setAlgoId(request.getAlgoId());
        params.setParticipantCode(request.getParticipantCode());

        return motilalAccountService.placeOrder(
                params,
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── MODIFY ORDER ──────────────────────────────────────────────────────────
    public JSONObject modifyOrder(MotilalModifyOrderRequest request) {
        MotilalModifyOrderParams params = new MotilalModifyOrderParams();
        params.setUniqueOrderId(request.getUniqueOrderId());
        params.setClientCode(request.getClientCode());
        params.setNewOrderType(request.getNewOrderType());
        params.setNewOrderDuration(request.getNewOrderDuration());
        params.setNewPrice(request.getNewPrice());
        params.setNewTriggerPrice(request.getNewTriggerPrice());
        params.setNewQuantityInLot(request.getNewQuantityInLot());
        params.setNewDisclosedQuantity(request.getNewDisclosedQuantity());
        params.setNewGoodTillDate(request.getNewGoodTillDate());
        params.setLastModifiedTime(request.getLastModifiedTime());
        params.setQtyTradedToday(request.getQtyTradedToday());

        return motilalAccountService.modifyOrder(
                params,
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── CANCEL ORDER ──────────────────────────────────────────────────────────
    public JSONObject cancelOrder(MotilalCancelOrderRequest request) {
        MotilalModifyOrderParams params = new MotilalModifyOrderParams();
        params.setUniqueOrderId(request.getUniqueOrderId());
        params.setClientCode(request.getClientCode());

        return motilalAccountService.cancelOrder(
                params,
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── HOLDINGS ──────────────────────────────────────────────────────────────
    public JSONObject getHoldings(MotilalBaseRequest request) {
        return motilalAccountService.getHoldings(
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── POSITIONS ─────────────────────────────────────────────────────────────
    public JSONObject getPositions(MotilalBaseRequest request) {
        return motilalAccountService.getPositions(
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken());
    }

    // ── CONVERT POSITION ──────────────────────────────────────────────────────
    public JSONObject convertPosition(MotilalConvertPositionRequest request) {
        return motilalAccountService.convertPosition(
                request.getApiKey(),
                request.getApiSecret(),
                request.getVendorInfo(),
                request.getAuthToken(),
                request.getAccessToken(),
                request.getClientcode(),
                request.getExchange(),
                request.getScripcode(),
                request.getQuantity(),
                request.getOldproduct(),
                request.getNewproduct());
    }
}
