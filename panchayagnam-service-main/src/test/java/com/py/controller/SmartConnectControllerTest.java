package com.py.controller;//package com.et.controller;
//
//// ─── JUnit 5 ────────────────────────────────────────────────────────────────
//import com.et.exceptions.SmartAPIException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//// NOTE: @Order is used as fully-qualified @org.junit.jupiter.api.Order
////       to avoid clash with com.et.modal.Order
//
//// ─── Spring Boot Test ────────────────────────────────────────────────────────
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//
//// ─── Jackson ─────────────────────────────────────────────────────────────────
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//// ─── App modals (com.et.modal.Order is NOT imported to avoid ambiguity) ──────
//import com.et.modal.EstimateChargesParams;
//import com.et.modal.Gtt;
//import com.et.modal.GttParams;
//import com.et.modal.MarginParams;
//import com.et.modal.TokenSet;
//import com.et.modal.User;
//import com.et.utilities.SmartConnect;
//
//// ─── SmartConnect ─────────────────────────────────────────────────────────────
//
//// ─── JSON ─────────────────────────────────────────────────────────────────────
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//// ─── Java utils ───────────────────────────────────────────────────────────────
//import java.util.*;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(SmartConnectController.class)
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@WithMockUser  // bypasses Spring Security 401/403 for all tests
//class SmartConnectControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private SmartConnect smartConnect;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    // ─────────────────────────────────────────────
//    // 1. Generate Session
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(1)
//    @DisplayName("POST /api/smart/session/generate")
//    void testGenerateSession() throws Exception {
//        User mockUser = new User();
//        mockUser.setAccessToken("mock_jwt_token");
//        mockUser.setRefreshToken("mock_refresh_token");
//
//        when(smartConnect.generateSession("C123456", "password@123", "123456")).thenReturn(mockUser);
//
//        Map<String, String> body = new HashMap<>();
//        body.put("clientCode", "C123456");
//        body.put("password", "password@123");
//        body.put("totp", "123456");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/session/generate")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [1] Generate Session: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 2. Renew Access Token
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(2)
//    @DisplayName("POST /api/smart/session/renew")
//    void testRenewAccessToken() throws Exception {
//        TokenSet mockTokenSet = new TokenSet();
//        mockTokenSet.setUserId("C123456");
//        mockTokenSet.setAccessToken("new_jwt_token");
//        mockTokenSet.setRefreshToken("new_refresh_token");
//
//        when(smartConnect.renewAccessToken(anyString(), anyString())).thenReturn(mockTokenSet);
//
//        Map<String, String> body = new HashMap<>();
//        body.put("accessToken", "old_jwt_token");
//        body.put("refreshToken", "old_refresh_token");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/session/renew")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [2] Renew Token: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 3. Get Profile
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(3)
//    @DisplayName("GET /api/smart/user/profile")
//    void testGetProfile() throws Exception {
//        User mockUser = new User();
//        mockUser.setAccessToken("mock_jwt_token");
//
//        when(smartConnect.getProfile()).thenReturn(mockUser);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/user/profile"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [3] Get Profile: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 4. Logout
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(4)
//    @DisplayName("POST /api/smart/session/logout")
//    void testLogout() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("message", "SUCCESS");
//
//        when(smartConnect.logout()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(post("/api/smart/session/logout")
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [4] Logout: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 5. Place Order
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(5)
//    @DisplayName("POST /api/smart/order/place")
//    void testPlaceOrder() throws Exception {
//        com.et.modal.Order mockOrder = new com.et.modal.Order();
//        mockOrder.orderId       = "ORDER123456";
//        mockOrder.uniqueOrderId = "UNIQUE_ORDER_789";
//
//        when(smartConnect.placeOrder(any(com.et.modal.OrderParams.class), anyString()))
//                .thenReturn(mockOrder);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("exchange",       "NSE");
//        body.put("tradingsymbol",  "SBIN-EQ");
//        body.put("transactiontype","BUY");
//        body.put("quantity",       10);
//        body.put("price",          620.0);
//        body.put("producttype",    "DELIVERY");
//        body.put("ordertype",      "LIMIT");
//        body.put("duration",       "DAY");
//        body.put("symboltoken",    "3045");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/order/place")
//                        .param("variety", "regular")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [5] Place Order: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 6. Modify Order
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(6)
//    @DisplayName("PUT /api/smart/order/modify/{orderId}")
//    void testModifyOrder() throws Exception {
//        com.et.modal.Order mockOrder = new com.et.modal.Order();
//        mockOrder.orderId = "ORDER123456";
//
//        when(smartConnect.modifyOrder(anyString(), any(com.et.modal.OrderParams.class), anyString()))
//                .thenReturn(mockOrder);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("exchange",      "NSE");
//        body.put("tradingsymbol", "SBIN-EQ");
//        body.put("quantity",      20);
//        body.put("price",         625.0);
//        body.put("producttype",   "DELIVERY");
//        body.put("ordertype",     "LIMIT");
//        body.put("duration",      "DAY");
//        body.put("symboltoken",   "3045");
//
//        MvcResult result = mockMvc.perform(put("/api/smart/order/modify/ORDER123456")
//                        .param("variety", "regular")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [6] Modify Order: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 7. Cancel Order
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(7)
//    @DisplayName("DELETE /api/smart/order/cancel/{orderId}")
//    void testCancelOrder() throws Exception {
//        com.et.modal.Order mockOrder = new com.et.modal.Order();
//        mockOrder.orderId = "ORDER123456";
//
//        when(smartConnect.cancelOrder(anyString(), anyString())).thenReturn(mockOrder);
//
//        MvcResult result = mockMvc.perform(delete("/api/smart/order/cancel/ORDER123456")
//                        .param("variety", "regular"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [7] Cancel Order: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 8. Get Order Book
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(8)
//    @DisplayName("GET /api/smart/order/book/{clientId}")
//    void testGetOrderHistory() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data", new JSONArray()
//                .put(new JSONObject().put("orderid", "ORDER123").put("status", "complete")));
//
//        when(smartConnect.getOrderHistory(anyString())).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/book/C123456"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [8] Order History: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 9. Get Individual Order Details
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(9)
//    @DisplayName("GET /api/smart/order/{orderId}")
//    void testGetIndividualOrderDetails() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("orderid",       "ORDER123456");
//        mockResponse.put("status",        "complete");
//        mockResponse.put("tradingsymbol", "SBIN-EQ");
//
//        when(smartConnect.getIndividualOrderDetails(anyString())).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/ORDER123456"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [9] Individual Order: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 10. Get Trades
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(10)
//    @DisplayName("GET /api/smart/order/trades")
//    void testGetTrades() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status",  true);
//        mockResponse.put("message", "SUCCESS");
//
//        when(smartConnect.getTrades()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/trades"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [10] Trades: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 11. Get LTP
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(11)
//    @DisplayName("GET /api/smart/market/ltp")
//    void testGetLTP() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("exchange",      "NSE");
//        mockResponse.put("tradingsymbol", "SBIN-EQ");
//        mockResponse.put("ltp",           621.0);
//        mockResponse.put("open",          615.0);
//        mockResponse.put("high",          630.0);
//        mockResponse.put("low",           610.0);
//        mockResponse.put("close",         620.5);
//
//        when(smartConnect.getLTP(anyString(), anyString(), anyString())).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/market/ltp")
//                        .param("exchange",      "NSE")
//                        .param("tradingSymbol", "SBIN-EQ")
//                        .param("symboltoken",   "3045"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [11] LTP: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 12. Get RMS
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(12)
//    @DisplayName("GET /api/smart/order/rms")
//    void testGetRMS() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("net",             "500000.00");
//        mockResponse.put("availablecash",   "450000.00");
//        mockResponse.put("utiliseddebits",  "50000.00");
//
//        when(smartConnect.getRMS()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/rms"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [12] RMS: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 13. Get Holding
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(13)
//    @DisplayName("GET /api/smart/order/holding")
//    void testGetHolding() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data", new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "SBIN-EQ").put("quantity", 10)));
//
//        when(smartConnect.getHolding()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/holding"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [13] Holdings: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 14. Get All Holdings
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(14)
//    @DisplayName("GET /api/smart/order/all-holdings")
//    void testGetAllHolding() throws Exception, SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("totalholding", new JSONObject()
//                .put("totalprofitandloss", "12000.00")
//                .put("totalNetValue",      "120000.00"));
//
//        when(smartConnect.getAllHolding()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/all-holdings"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [14] All Holdings: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 15. Get Position
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(15)
//    @DisplayName("GET /api/smart/order/position")
//    void testGetPosition() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data", new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "NIFTY23APRFUT").put("netqty", "50")));
//
//        when(smartConnect.getPosition()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/order/position"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [15] Position: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 16. Convert Position
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(16)
//    @DisplayName("POST /api/smart/order/position/convert")
//    void testConvertPosition() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status",  true);
//        mockResponse.put("message", "SUCCESS");
//
//        when(smartConnect.convertPosition(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("exchange",       "NSE");
//        body.put("oldproducttype", "MARGIN");
//        body.put("newproducttype", "DELIVERY");
//        body.put("tradingsymbol",  "SBIN-EQ");
//        body.put("symboltoken",    "3045");
//        body.put("quantity",       10);
//
//        MvcResult result = mockMvc.perform(post("/api/smart/order/position/convert")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [16] Convert Position: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 17. Market Data
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(17)
//    @DisplayName("POST /api/smart/market/data")
//    void testMarketData() throws Exception, SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("fetched", new JSONArray()
//                .put(new JSONObject().put("tradingSymbol", "SBIN-EQ").put("ltp", 621.5)));
//
//        when(smartConnect.marketData(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        Map<String, List<String>> tokens = new HashMap<>();
//        tokens.put("NSE", Collections.singletonList("3045"));
//        body.put("mode",           "FULL");
//        body.put("exchangeTokens", tokens);
//
//        MvcResult result = mockMvc.perform(post("/api/smart/market/data")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [17] Market Data: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 18. Candle Data
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(18)
//    @DisplayName("POST /api/smart/market/candle")
//    void testCandleData() throws Exception {
//        JSONArray mockResponse = new JSONArray();
//        mockResponse.put(new JSONArray()
//                .put("2023-04-27 09:15").put(620.0).put(630.0).put(615.0).put(625.0).put(150000));
//
//        when(smartConnect.candleData(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("exchange",    "NSE");
//        body.put("symboltoken", "3045");
//        body.put("interval",    "ONE_MINUTE");
//        body.put("fromdate",    "2023-04-27 09:15");
//        body.put("todate",      "2023-04-27 15:30");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/market/candle")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [18] Candle Data: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 19. OI Data
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(19)
//    @DisplayName("POST /api/smart/market/oi")
//    void testOiData() throws Exception {
//        JSONArray mockResponse = new JSONArray();
//        mockResponse.put(new JSONObject().put("symboltoken", "35001").put("oi", "1200000"));
//
//        when(smartConnect.oiData(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("exchange",    "NFO");
//        body.put("symboltoken", "35001");
//        body.put("interval",    "ONE_MINUTE");
//        body.put("fromdate",    "2023-04-27 09:15");
//        body.put("todate",      "2023-04-27 15:30");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/market/oi")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [19] OI Data: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 20. Search Scrip
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(20)
//    @DisplayName("POST /api/smart/market/search-scrip")
//    void testGetSearchScrip() throws Exception, SmartAPIException {
//        String mockResponse = "[{\"exchange\":\"NSE\",\"tradingsymbol\":\"SBIN-EQ\",\"symboltoken\":\"3045\"}]";
//        when(smartConnect.getSearchScrip(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("exchange",   "NSE");
//        body.put("searchscrip","SBIN");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/market/search-scrip")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [20] Search Scrip: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 21. GTT Create Rule
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(21)
//    @DisplayName("POST /api/smart/gtt/create")
//    void testGttCreateRule() throws Exception {
//        Gtt mockGtt = new Gtt();
//        mockGtt.id = 1001;
//        when(smartConnect.gttCreateRule(any(GttParams.class))).thenReturn(mockGtt);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("tradingsymbol",  "SBIN-EQ");
//        body.put("symboltoken",    "3045");
//        body.put("exchange",       "NSE");
//        body.put("transactiontype","BUY");
//        body.put("producttype",    "DELIVERY");
//        body.put("price",          620.0);
//        body.put("qty",            10);
//        body.put("triggerprice",   615.0);
//        body.put("timeperiod",     30);
//
//        MvcResult result = mockMvc.perform(post("/api/smart/gtt/create")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [21] GTT Create: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 22. GTT Modify Rule
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(22)
//    @DisplayName("PUT /api/smart/gtt/modify/{id}")
//    void testGttModifyRule() throws Exception {
//        Gtt mockGtt = new Gtt();
//        mockGtt.id = 1001;
//        when(smartConnect.gttModifyRule(anyInt(), any(GttParams.class))).thenReturn(mockGtt);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("symboltoken",  "3045");
//        body.put("exchange",     "NSE");
//        body.put("price",        625.0);
//        body.put("qty",          15);
//        body.put("triggerprice", 620.0);
//        body.put("timeperiod",   30);
//
//        MvcResult result = mockMvc.perform(put("/api/smart/gtt/modify/1001")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [22] GTT Modify: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 23. GTT Cancel Rule
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(23)
//    @DisplayName("DELETE /api/smart/gtt/cancel/{id}")
//    void testGttCancelRule() throws Exception {
//        Gtt mockGtt = new Gtt();
//        mockGtt.id = 1001;
//        when(smartConnect.gttCancelRule(anyInt(), anyString(), anyString())).thenReturn(mockGtt);
//
//        MvcResult result = mockMvc.perform(delete("/api/smart/gtt/cancel/1001")
//                        .param("symboltoken", "3045")
//                        .param("exchange",    "NSE"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [23] GTT Cancel: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 24. GTT Rule Details
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(24)
//    @DisplayName("GET /api/smart/gtt/details/{id}")
//    void testGttRuleDetails() throws Exception {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("id",            1001);
//        mockResponse.put("tradingsymbol", "SBIN-EQ");
//        mockResponse.put("status",        "NEW");
//        mockResponse.put("triggerprice",  615.0);
//
//        when(smartConnect.gttRuleDetails(anyInt())).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/gtt/details/1001"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [24] GTT Details: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 25. GTT Rule List
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(25)
//    @DisplayName("POST /api/smart/gtt/list")
//    void testGttRuleList() throws Exception {
//        JSONArray mockResponse = new JSONArray();
//        mockResponse.put(new JSONObject().put("id", 1001).put("status", "NEW"));
//        mockResponse.put(new JSONObject().put("id", 1002).put("status", "CANCELLED"));
//
//        when(smartConnect.gttRuleList(anyList(), anyInt(), anyInt())).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("status", Arrays.asList("NEW", "CANCELLED", "ACTIVE"));
//        body.put("page",   1);
//        body.put("count",  10);
//
//        MvcResult result = mockMvc.perform(post("/api/smart/gtt/list")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [25] GTT List: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 26. Margin Details
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(26)
//    @DisplayName("POST /api/smart/margin/details")
//    void testGetMarginDetails() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONObject().put("charges", new JSONObject().put("totalcharge", "25.50")));
//
//        when(smartConnect.getMarginDetails(anyList())).thenReturn(mockResponse);
//
//        MarginParams param = new MarginParams();
//        param.exchange    = "NSE";
//        param.quantity    = 10;
//        param.price       = 620.0;
//        param.productType = "DELIVERY";
//        param.token       = "3045";
//        param.tradeType   = "BUY";
//
//        MvcResult result = mockMvc.perform(post("/api/smart/margin/details")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(Collections.singletonList(param))))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [26] Margin Details: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 27. Estimate Charges
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(27)
//    @DisplayName("POST /api/smart/order/estimate-charges")
//    void testEstimateCharges() throws Exception ,SmartAPIException{
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONObject().put("charges", new JSONObject().put("totalcharge", "45.00")));
//
//        when(smartConnect.estimateCharges(anyList())).thenReturn(mockResponse);
//
//        EstimateChargesParams param = new EstimateChargesParams();
//        param.product_type     = "DELIVERY";
//        param.transaction_type = "BUY";
//        param.quantity         = "10";
//        param.price            = "620.0";
//        param.exchange         = "NSE";
//        param.symbol_name      = "SBIN";
//        param.token            = "3045";
//
//        MvcResult result = mockMvc.perform(post("/api/smart/order/estimate-charges")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(Collections.singletonList(param))))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [27] Estimate Charges: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 28. Verify DIS
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(28)
//    @DisplayName("POST /api/smart/dis/verify")
//    void testVerifyDis() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status",  true);
//        mockResponse.put("message", "DIS verified successfully");
//
//        when(smartConnect.verifyDis(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("isin",     "INE062A01020");
//        body.put("quantity", 10);
//        body.put("tpin",     "123456");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/dis/verify")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [28] Verify DIS: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 29. Generate TPIN
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(29)
//    @DisplayName("POST /api/smart/dis/generate-tpin")
//    void testGenerateTPIN() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status",  true);
//        mockResponse.put("message", "TPIN sent to registered mobile/email");
//
//        when(smartConnect.generateTPIN(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("clientCode", "C123456");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/dis/generate-tpin")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [29] Generate TPIN: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 30. Get Transaction Status
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(30)
//    @DisplayName("POST /api/smart/dis/transaction-status")
//    void testGetTranStatus() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONObject().put("transactionStatus", "SUCCESS").put("edisStatus", "Y"));
//
//        when(smartConnect.getTranStatus(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("clientCode", "C123456");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/dis/transaction-status")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [30] Transaction Status: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 31. Option Greek
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(31)
//    @DisplayName("POST /api/smart/option/greek")
//    void testOptionGreek() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "NIFTY27APR23C18000")
//                        .put("delta","0.56").put("gamma","0.001").put("theta","-2.5").put("vega","5.2")));
//
//        when(smartConnect.optionGreek(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("name",       "NIFTY");
//        body.put("expirydate", "27APR2023");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/option/greek")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [31] Option Greek: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 32. Gainers / Losers
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(32)
//    @DisplayName("POST /api/smart/market/gainers-losers")
//    void testGainersLosers() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "TATAMOTORS-EQ").put("percentchange", "4.5")));
//
//        when(smartConnect.gainersLosers(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("datatype",   "PercPriceGainers");
//        body.put("expirytype", "NEAR");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/market/gainers-losers")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [32] Gainers/Losers: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 33. Put Call Ratio
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(33)
//    @DisplayName("GET /api/smart/market/put-call-ratio")
//    void testPutCallRatio() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONArray()
//                .put(new JSONObject().put("name", "NIFTY").put("pcroi", "1.20")));
//
//        when(smartConnect.putCallRatio()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/market/put-call-ratio"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [33] Put Call Ratio: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 34. NSE Intraday
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(34)
//    @DisplayName("GET /api/smart/market/nse-intraday")
//    void testNseIntraday() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "RELIANCE-EQ").put("volume", "5000000")));
//
//        when(smartConnect.nseIntraday()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/market/nse-intraday"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [34] NSE Intraday: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 35. BSE Intraday
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(35)
//    @DisplayName("GET /api/smart/market/bse-intraday")
//    void testBseIntraday() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "RELIANCE").put("volume", "3000000")));
//
//        when(smartConnect.bseIntraday()).thenReturn(mockResponse);
//
//        MvcResult result = mockMvc.perform(get("/api/smart/market/bse-intraday"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [35] BSE Intraday: " + result.getResponse().getContentAsString());
//    }
//
//    // ─────────────────────────────────────────────
//    // 36. OI Buildup
//    // ─────────────────────────────────────────────
//    @Test
//    @org.junit.jupiter.api.Order(36)
//    @DisplayName("POST /api/smart/market/oi-buildup")
//    void testOIBuildup() throws Exception,SmartAPIException {
//        JSONObject mockResponse = new JSONObject();
//        mockResponse.put("status", true);
//        mockResponse.put("data",   new JSONArray()
//                .put(new JSONObject().put("tradingsymbol", "NIFTY27APR23C18000")
//                        .put("builduptype","Long Build Up").put("changeoi","500000")));
//
//        when(smartConnect.oIBuildup(any(JSONObject.class))).thenReturn(mockResponse);
//
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("expirytype", "NEAR");
//        body.put("datatype",   "Long Build Up");
//
//        MvcResult result = mockMvc.perform(post("/api/smart/market/oi-buildup")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(body)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andReturn();
//
//        System.out.println("✅ [36] OI Buildup: " + result.getResponse().getContentAsString());
//    }
//}
