package com.py.controller;

import com.py.brokers.angel.dto.Order;
import com.py.brokers.motilal.entity.MotilalTokenRefreshAuditEntity;
import com.py.brokers.motilal.repository.MotilalTokenRefreshAuditRepository;
import com.py.tokenrefresh.entity.TokenRefreshAuditEntity;
import com.py.common.enums.AccountActionType;
import com.py.common.enums.BrokerType;
import com.py.brokers.angel.exception.SmartAPIException;
import com.py.brokers.angel.dto.CancelOrderRequest;
import com.py.brokers.angel.dto.ModifyOrderRequest;
import com.py.common.dto.ApiResponse;
import com.py.controller.utils.ControllerResponseBuilder;
import com.py.demataccount.dto.DematAccountAddRequest;
import com.py.demataccount.dto.DematAccountFetchResponse;
import com.py.tokenrefresh.repository.TokenRefreshAuditRepository;
import com.py.demataccount.service.LiveHoldingsWebSocketManager;
import com.py.demataccount.service.PyBrokerAccountService;
import com.py.dto.ConvertPositionRequest;
import com.py.dto.PyBrokerAccountSummaryDto;
import com.py.easytrade.dto.IndividualOrderRequest;
import java.io.IOException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/pyBrokerApi")
@CrossOrigin(origins = "*")
@Tag(name = "PyBroker", description = "Unified broker operations — AngelOne & Motilal Oswal")
public class PyBrokerController {

    private static final Logger log = LogManager.getLogger(PyBrokerController.class);

    @Autowired
    private PyBrokerAccountService pyBrokerAccountService;

    @Autowired
    private TokenRefreshAuditRepository tokenRefreshAuditRepository;

    @Autowired
    private MotilalTokenRefreshAuditRepository motilalTokenRefreshAuditRepository;

    @Autowired
    private LiveHoldingsWebSocketManager liveHoldingsWebSocketManager;

    @Autowired
    private ControllerResponseBuilder responseBuilder;

    // =========================================================================
    // ✅ ACCOUNT OPERATIONS — unified for both brokers
    //    brokerType is inside DematAccountAddRequest
    // =========================================================================
    @Operation(summary = "Broker Account Operations",
            description = "Add / Update / Delete a demat account. "
            + "Set brokerType = ANGELONE or MOTILAL in the request body.")
    @PostMapping("/brokerAccOperations")
    public ResponseEntity<?> dematAccountOperations(
            @RequestBody DematAccountAddRequest request,
            @RequestParam AccountActionType actionType) {
        log.info("dematAccountOperations | actionType={}, BrokerType={}, PyUserId={}",
                actionType, request.getBrokerType(), request.getPyUserId());
        try {
            ApiResponse<?> response = pyBrokerAccountService.dematAccountOperations(request, actionType);
            return responseBuilder.buildResponse(response);

        } catch (SmartAPIException | IOException e) {
            log.error("Broker API error: {}", e.getMessage(), e);
            return responseBuilder.buildError(502, "Broker API error");
        } catch (Exception e) {
            log.error("Internal error in broker account operation: {}", e.getMessage(), e);
            return responseBuilder.buildError(500, "Internal server error");
        }
    }

    // =========================================================================
    // ✅ FETCH ACCOUNTS
    // =========================================================================
    @Operation(summary = "Fetch Broker Accounts without Balance",
            description = "Fetches user Broker accounts without balance")
    @PostMapping("/accounts")
    public ResponseEntity<ApiResponse<List<DematAccountFetchResponse>>> fetchAccountsByPyId(
            @Parameter(description = "Fetch All Accounts by PY_ID", example = "1")
            @RequestParam Long pyId) {

        ApiResponse<List<DematAccountFetchResponse>> response = pyBrokerAccountService.fetchAccountsByPyId(pyId, false);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

     @Operation(summary = "Fetch Broker Account with Balance",
            description = "Fetches Broker account with balance")
    @PostMapping("/account")
    public ResponseEntity<ApiResponse<List<DematAccountFetchResponse>>> fetchAccountByPyBrokerId(
            @Parameter(description = "Fetch account by Py_Broker_Id", example = "1")
            @RequestParam Long pyBrokerId) {

        ApiResponse<List<DematAccountFetchResponse>> response = pyBrokerAccountService.fetchAccountsByPyBrokerId(pyBrokerId, true);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
    

    // =========================================================================
    // ✅ TOKEN AUDIT — brokerType param routes to AngelOne or Motilal
    // =========================================================================
    @Operation(summary = "Get Token Refresh Audit Log",
            description = "Retrieve the audit log. "
            + "brokerType = ANGELONE (default) or MOTILAL")
    @GetMapping("/tokens/audit")
    public ResponseEntity<?> getAuditLog(
            @Parameter(description = "Broker type", example = "ANGELONE")
            @RequestParam(required = false, defaultValue = "ANGELONE") BrokerType brokerType) {

        if (brokerType == BrokerType.MOTILAL) {
            List<MotilalTokenRefreshAuditEntity> audits = motilalTokenRefreshAuditRepository
                    .findAll(Sort.by(Sort.Direction.DESC, "attemptedAt"));
            JSONArray auditJson = responseBuilder.buildMotilalAuditJson(audits);
            return ResponseEntity.ok(auditJson.toString());
        }

        List<TokenRefreshAuditEntity> audits = tokenRefreshAuditRepository
                .findAll(Sort.by(Sort.Direction.DESC, "attemptedAt"));
        JSONArray auditJson = responseBuilder.buildAngelOneAuditJson(audits);
        return ResponseEntity.ok(auditJson.toString());
    }

    // =========================================================================
    // ✅ ORDER OPERATIONS — unchanged (AngelOne only for now)
    // =========================================================================
    @Operation(summary = "Place Individual Buy",
            description = "Place a buy order directly via broker API")
    @PostMapping("/order/buy")
    public ResponseEntity<?> placeIndividualBuy(@RequestBody IndividualOrderRequest request) {
        log.info("POST /order/buy | accId={}, symbol={}", request.getAccId(), request.getTradingSymbol());
        try {
            Order order = pyBrokerAccountService.placeIndividualOrder(request, "BUY");
            JSONObject response = responseBuilder.buildOrderOperationResponse(
                    order.orderId, order.uniqueOrderId);
            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            log.error("BUY order failed | accId={}: {}", request.getAccId(), e.getMessage(), e);
            return responseBuilder.buildError(400, "Buy order failed");
        }
    }

    @Operation(summary = "Place Individual Sell",
            description = "Place a sell order directly via broker API")
    @PostMapping("/order/sell")
    public ResponseEntity<?> placeIndividualSell(@RequestBody IndividualOrderRequest request) {
        log.info("POST /order/sell | accId={}, symbol={}", request.getAccId(), request.getTradingSymbol());
        try {
            Order order = pyBrokerAccountService.placeIndividualOrder(request, "SELL");
            JSONObject response = responseBuilder.buildOrderOperationResponse(
                    order.orderId, order.uniqueOrderId);
            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            log.error("SELL order failed | accId={}: {}", request.getAccId(), e.getMessage(), e);
            return responseBuilder.buildError(400, "Sell order failed");
        }
    }

    @Operation(summary = "Get Order History",
            description = "Fetch all orders for the day for a demat account")
    @GetMapping("/order/history")
    public ResponseEntity<?> getOrderHistory(
            @Parameter(description = "Demat account ID", example = "1")
            @RequestParam Long accId) {
        log.info("GET /order/history | accId={}", accId);
        try {
            JSONObject response = pyBrokerAccountService.getOrderHistory(accId);
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            log.error("Fetch order history failed | accId={}: {}", accId, e.getMessage(), e);
            return responseBuilder.buildError(400, "Failed to fetch order history");
        }
    }

    @Operation(summary = "Start Live Holdings Feed",
            description = "Fetches holdings from broker and starts live WS feed")
    @PostMapping("/order/live-holdings/start")
    public ResponseEntity<?> startLiveHoldings(@RequestParam Long accId) {
        log.info("POST /order/live-holdings/start | accId={}", accId);
        try {
            liveHoldingsWebSocketManager.startLiveFeed(accId);
            return ResponseEntity.ok(
                    ApiResponse.success("Live feed started for accId: " + accId));
        } catch (Exception e) {
            log.error("Start live holdings failed | accId={}: {}", accId, e.getMessage(), e);
            return responseBuilder.buildError(400, "Failed to start live holdings feed");
        }
    }

    @Operation(summary = "Stop Live Holdings Feed",
            description = "Stop live WS feed for an account")
    @PostMapping("/order/live-holdings/stop")
    public ResponseEntity<?> stopLiveHoldings(@RequestParam Long accId) {
        log.info("POST /order/live-holdings/stop | accId={}", accId);
        try {
            liveHoldingsWebSocketManager.stopLiveFeed(accId);
            return ResponseEntity.ok(
                    ApiResponse.success("Live feed stopped for accId: " + accId));
        } catch (Exception e) {
            log.error("Stop live holdings failed | accId={}: {}", accId, e.getMessage(), e);
            return responseBuilder.buildError(400, "Failed to stop live holdings feed");
        }
    }

    @Operation(
            summary = "Get Broker Accounts Grouped By Broker Name",
            description = "Fetch broker accounts grouped by broker name. "
            + "SUPER_ADMIN → all accounts. "
            + "ADMIN/USER → only their accounts.")
    @GetMapping("/broker-accounts/grouped/{pyId}")
    public ResponseEntity<ApiResponse<Map<String, List<PyBrokerAccountSummaryDto>>>> getBrokerAccountsGrouped(
            @PathVariable Long pyId) {
        try {
            ApiResponse<Map<String, List<PyBrokerAccountSummaryDto>>> response
                    = pyBrokerAccountService.getBrokerAccountsGrouped(pyId);
            return ResponseEntity.status(response.getStatusCode()).body(response);
    } catch (RuntimeException e) {
        log.error("Runtime exception in getBrokerAccountsGrouped: {}", e.getMessage(), e);
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(400, "Bad request"));
    }
    }

    @Operation(
            summary = "Get Positions",
            description = "Fetch open positions for a broker account. Supports AngelOne, Motilal, Dhan.")
    @GetMapping("/order/positions")
    public ResponseEntity<?> getPositions(
            @Parameter(description = "Broker account ID", example = "1")
            @RequestParam Long accId) {
        log.info("GET /order/positions | accId={}", accId);
        try {
            JSONObject response = pyBrokerAccountService.getPositions(accId);
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            log.error("Fetch positions failed | accId={}: {}", accId, e.getMessage(), e);
            return responseBuilder.buildError(400, "Failed to fetch positions");
        }
    }

    @PostMapping("/modifyOrder")
    public ResponseEntity<ApiResponse<Order>> modifyOrder(
            @RequestBody ModifyOrderRequest request) {
        try {
            Order order = pyBrokerAccountService.modifyIndividualOrder(request);
            return ResponseEntity.ok(ApiResponse.success("Order modified successfully", order));
        } catch (Exception e) {
            log.error("Modify order failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Modify order failed"));
        }
    }

    @PostMapping("/cancelOrder")
    public ResponseEntity<ApiResponse<Order>> cancelOrder(
            @RequestBody CancelOrderRequest request) {
        try {
            Order order = pyBrokerAccountService.cancelIndividualOrder(request);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
        } catch (Exception e) {
            log.error("Cancel order failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Cancel order failed"));
        }
    }

    @Operation(summary = "Get LTP", description = "Get LTP by broker account ID")
    @GetMapping("/market/ltp")
    public ResponseEntity<?> getLTP(
            @RequestParam Long brokerId,
            @RequestParam String exchange,
            @RequestParam String tradingSymbol,
            @RequestParam String symbolToken) {
        log.info("GET /market/ltp | brokerId={} exchange={} symbol={} token={}",
                brokerId, exchange, tradingSymbol, symbolToken);
        try {
            JSONObject response = pyBrokerAccountService.getLTP(
                    brokerId, exchange, tradingSymbol, symbolToken);
            return response != null
                    ? ResponseEntity.ok(response.toMap())
                    : ResponseEntity.badRequest().body("Failed to get LTP");
        } catch (Exception e) {
            log.error("LTP failed | brokerId={}: {}", brokerId, e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error(400, "Failed to get LTP"));
        }
    }

    @Operation(summary = "Convert Position",
            description = "Convert a position's margin product type e.g. DELIVERY → INTRADAY")
    @PostMapping("/order/convertPosition")
    public ResponseEntity<?> convertPosition(
            @RequestBody ConvertPositionRequest request) {
        log.info("POST /order/convertPosition | accId={} symbol={} {} -> {}",
                request.getAccId(), request.getTradingsymbol(),
                request.getOldproducttype(), request.getNewproducttype());
        try {
            JSONObject response = pyBrokerAccountService.convertPosition(request.getAccId(), request);
            return ResponseEntity.ok(response.toMap());
        } catch (Exception e) {
            log.error("Convert position failed | accId={}: {}", request.getAccId(), e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body("Convert position failed: " + e.getMessage());
        }
    }
}
