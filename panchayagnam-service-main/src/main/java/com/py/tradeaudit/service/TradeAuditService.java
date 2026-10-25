package com.py.tradeaudit.service;

import com.py.tradegroup.entity.TradeGroupOrderAuditEntity;
import com.py.tradegroup.entity.TradeGroupSellAuditEntity;
import com.py.tradegroup.entity.TradePositionEntity;
import com.py.tradegroup.repository.TradeGroupOrderAuditRepository;
import com.py.tradegroup.repository.TradeGroupSellAuditRepository;
import com.py.tradegroup.repository.TradePositionRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeAuditService {

    private static final Logger log = LoggerFactory.getLogger(TradeAuditService.class);

    @Autowired
    private TradeGroupOrderAuditRepository buyAuditRepository;

    @Autowired
    private TradeGroupSellAuditRepository sellAuditRepository;

    @Autowired
    private TradePositionRepository positionRepository;

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // BUY AUDIT
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public JSONArray getAllBuyAudits() {
        log.info("Fetching all buy audits");
        return mapBuyAudits(buyAuditRepository.findAllByOrderByExecutedAtDesc());
    }

    public JSONArray getBuyAuditsByGroup(Long groupId) {
        log.info("Fetching buy audits | groupId={}", groupId);
        return mapBuyAudits(buyAuditRepository.findByGroupIdOrderByExecutedAtDesc(groupId));
    }

    public JSONArray getBuyAuditsByGroupAndStatus(Long groupId, String status) {
        return mapBuyAudits(buyAuditRepository
            .findByGroupIdAndStatusOrderByExecutedAtDesc(groupId, status.toUpperCase()));
    }

    public JSONArray getBuyAuditsByClient(String clientId) {
        return mapBuyAudits(buyAuditRepository.findByClientIdOrderByExecutedAtDesc(clientId));
    }

    public JSONArray getBuyAuditsByDematAccount(Long dematAccountId) {
        return mapBuyAudits(buyAuditRepository
            .findByDematAccountIdOrderByExecutedAtDesc(dematAccountId));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // SELL AUDIT
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public JSONArray getAllSellAudits() {
        log.info("Fetching all sell audits");
        return mapSellAudits(sellAuditRepository.findAllByOrderByExecutedAtDesc());
    }

    public JSONArray getSellAuditsByGroup(Long groupId) {
        log.info("Fetching sell audits | groupId={}", groupId);
        return mapSellAudits(sellAuditRepository.findByGroupIdOrderByExecutedAtDesc(groupId));
    }

    public JSONArray getSellAuditsByGroupAndStatus(Long groupId, String status) {
        return mapSellAudits(sellAuditRepository
            .findByGroupIdAndStatusOrderByExecutedAtDesc(groupId, status.toUpperCase()));
    }

    public JSONArray getSellAuditsByClient(String clientId) {
        return mapSellAudits(sellAuditRepository.findByClientIdOrderByExecutedAtDesc(clientId));
    }

    public JSONArray getSellAuditsByDematAccount(Long dematAccountId) {
        return mapSellAudits(sellAuditRepository
            .findByDematAccountIdOrderByExecutedAtDesc(dematAccountId));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // POSITIONS
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public JSONArray getOpenPositionsByGroup(Long groupId) {
        return mapPositions(positionRepository.findByGroupIdAndStatus(groupId, "OPEN"));
    }

    public JSONArray getSoldPositionsByGroup(Long groupId) {
        return mapPositions(positionRepository.findByGroupIdAndStatus(groupId, "SOLD"));
    }

    public JSONArray getAllPositionsByGroup(Long groupId) {
        return mapPositions(positionRepository.findByGroupIdOrderByCreatedAtDesc(groupId));
    }

    public JSONArray getAllPositionsByDematAccount(Long dematAccountId) {
        return mapPositions(positionRepository
            .findByDematAccountIdOrderByCreatedAtDesc(dematAccountId));
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // COMBINED SUMMARY
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public JSONObject getGroupSummary(Long groupId) {
        log.info("Fetching group summary | groupId={}", groupId);
        List<TradeGroupOrderAuditEntity> buyAudits =
            buyAuditRepository.findByGroupIdOrderByExecutedAtDesc(groupId);
        List<TradeGroupSellAuditEntity> sellAudits =
            sellAuditRepository.findByGroupIdOrderByExecutedAtDesc(groupId);
        List<TradePositionEntity> openPositions =
            positionRepository.findByGroupIdAndStatus(groupId, "OPEN");
        List<TradePositionEntity> soldPositions =
            positionRepository.findByGroupIdAndStatus(groupId, "SOLD");

        long buySuccess  = buyAudits.stream().filter(a -> "SUCCESS".equals(a.getStatus())).count();
        long buyFailed   = buyAudits.stream().filter(a -> "FAILED".equals(a.getStatus())).count();
        long sellSuccess = sellAudits.stream().filter(a -> "SUCCESS".equals(a.getStatus())).count();
        long sellFailed  = sellAudits.stream().filter(a -> "FAILED".equals(a.getStatus())).count();

        JSONObject summary = new JSONObject();
        summary.put("groupId",          groupId);
        summary.put("totalBuyOrders",   buyAudits.size());
        summary.put("buySuccess",        buySuccess);
        summary.put("buyFailed",         buyFailed);
        summary.put("totalSellOrders",  sellAudits.size());
        summary.put("sellSuccess",       sellSuccess);
        summary.put("sellFailed",        sellFailed);
        summary.put("openPositions",    openPositions.size());
        summary.put("soldPositions",    soldPositions.size());
        summary.put("buyAudits",        mapBuyAudits(buyAudits));
        summary.put("sellAudits",       mapSellAudits(sellAudits));

        return summary;
    }

    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    // MAPPERS
    // â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private JSONArray mapBuyAudits(List<TradeGroupOrderAuditEntity> list) {
        JSONArray result = new JSONArray();
        for (TradeGroupOrderAuditEntity a : list) {
            JSONObject obj = new JSONObject();
            obj.put("id",            a.getId());
            obj.put("groupId",       a.getGroupId());
            obj.put("groupName",     a.getGroupName() != null ? a.getGroupName() : "");
            obj.put("clientId",      a.getClientId() != null ? a.getClientId() : "");
            obj.put("userName",      a.getUserName() != null ? a.getUserName() : "");
            obj.put("exchange",      a.getExchange() != null ? a.getExchange() : "");
            obj.put("tradingSymbol", a.getTradingSymbol() != null ? a.getTradingSymbol() : "");
            obj.put("symbolToken",   a.getSymbolToken() != null ? a.getSymbolToken() : "");
            obj.put("quantity",      a.getQuantity() != null ? a.getQuantity() : 0);
            obj.put("price",         a.getPrice() != null ? a.getPrice() : 0);
            obj.put("orderType",     a.getOrderType() != null ? a.getOrderType() : "");
            obj.put("productType",   a.getProductType() != null ? a.getProductType() : "");
            obj.put("status",        a.getStatus() != null ? a.getStatus() : "");
            obj.put("orderId",       a.getOrderId() != null ? a.getOrderId() : "");
            obj.put("remarks",       a.getRemarks() != null ? a.getRemarks() : "");
            obj.put("executedAt",    a.getExecutedAt() != null ? a.getExecutedAt().toString() : "");
            result.put(obj);
        }
        return result;
    }

    private JSONArray mapSellAudits(List<TradeGroupSellAuditEntity> list) {
        JSONArray result = new JSONArray();
        for (TradeGroupSellAuditEntity a : list) {
            JSONObject obj = new JSONObject();
            obj.put("id",            a.getId());
            obj.put("groupId",       a.getGroupId());
            obj.put("groupName",     a.getGroupName() != null ? a.getGroupName() : "");
            obj.put("positionId",    a.getPositionId() != null ? a.getPositionId() : "");
            obj.put("clientId",      a.getClientId() != null ? a.getClientId() : "");
            obj.put("userName",      a.getUserName() != null ? a.getUserName() : "");
            obj.put("exchange",      a.getExchange() != null ? a.getExchange() : "");
            obj.put("tradingSymbol", a.getTradingSymbol() != null ? a.getTradingSymbol() : "");
            obj.put("symbolToken",   a.getSymbolToken() != null ? a.getSymbolToken() : "");
            obj.put("quantity",      a.getQuantity() != null ? a.getQuantity() : 0);
            obj.put("buyOrderId",    a.getBuyOrderId() != null ? a.getBuyOrderId() : "");
            obj.put("buyPrice",      a.getBuyPrice() != null ? a.getBuyPrice() : 0);
            obj.put("sellOrderId",   a.getSellOrderId() != null ? a.getSellOrderId() : "");
            obj.put("sellPrice",     a.getSellPrice() != null ? a.getSellPrice() : 0);
            obj.put("productType",   a.getProductType() != null ? a.getProductType() : "");
            obj.put("status",        a.getStatus() != null ? a.getStatus() : "");
            obj.put("remarks",       a.getRemarks() != null ? a.getRemarks() : "");
            obj.put("executedAt",    a.getExecutedAt() != null ? a.getExecutedAt().toString() : "");
            result.put(obj);
        }
        return result;
    }

    private JSONArray mapPositions(List<TradePositionEntity> list) {
        JSONArray result = new JSONArray();
        for (TradePositionEntity p : list) {
            JSONObject obj = new JSONObject();
            obj.put("id",            p.getId());
            obj.put("groupId",       p.getGroupId());
            obj.put("groupName",     p.getGroupName() != null ? p.getGroupName() : "");
            obj.put("clientId",      p.getClientId() != null ? p.getClientId() : "");
            obj.put("userName",      p.getUserName() != null ? p.getUserName() : "");
            obj.put("exchange",      p.getExchange() != null ? p.getExchange() : "");
            obj.put("tradingSymbol", p.getTradingSymbol() != null ? p.getTradingSymbol() : "");
            obj.put("symbolToken",   p.getSymbolToken() != null ? p.getSymbolToken() : "");
            obj.put("quantity",      p.getQuantity() != null ? p.getQuantity() : 0);
            obj.put("buyPrice",      p.getBuyPrice() != null ? p.getBuyPrice() : 0);
            obj.put("buyOrderId",    p.getBuyOrderId() != null ? p.getBuyOrderId() : "");
            obj.put("sellOrderId",   p.getSellOrderId() != null ? p.getSellOrderId() : "");
            obj.put("sellPrice",     p.getSellPrice() != null ? p.getSellPrice() : 0);
            obj.put("productType",   p.getProductType() != null ? p.getProductType() : "");
            obj.put("orderType",     p.getOrderType() != null ? p.getOrderType() : "");
            obj.put("status",        p.getStatus() != null ? p.getStatus() : "");
            obj.put("boughtAt",      p.getBoughtAt() != null ? p.getBoughtAt().toString() : "");
            obj.put("soldAt",        p.getSoldAt() != null ? p.getSoldAt().toString() : "");
            result.put(obj);
        }
        return result;
    }
}
