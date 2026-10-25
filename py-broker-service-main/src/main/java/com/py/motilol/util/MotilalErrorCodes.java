package com.py.motilol.util;

import java.util.Map;

public final class MotilalErrorCodes {

    private MotilalErrorCodes() {
    }

    private static final Map<String, String> CODES = Map.ofEntries(
            // ── Technical / Token ──────────────────────────────────────────────
            Map.entry("MO8000", "Technical error on Motilal server"),
            Map.entry("MO8001", "Invalid token — re-login required"),
            Map.entry("MO8002", "Token expired — re-login required"),
            Map.entry("MO8003", "Token missing in request"),
            Map.entry("MO8050", "Invalid refresh token"),
            Map.entry("MO8051", "Refresh token expired"),
            Map.entry("MO8052", "Invalid dashboard session"),
            // ── Authentication ─────────────────────────────────────────────────
            Map.entry("MO1000", "Invalid user ID or password"),
            Map.entry("MO1001", "Invalid user ID"),
            Map.entry("MO1002", "Invalid password length"),
            Map.entry("MO1003", "Client already exists"),
            Map.entry("MO1004", "Invalid API key"),
            Map.entry("MO1005", "API key not found"),
            Map.entry("MO1006", "User not authorized for live trading"),
            Map.entry("MO1007", "Invalid 2FA"),
            Map.entry("MO1008", "Invalid request checksum"),
            Map.entry("MO1011", "Client not logged in"),
            Map.entry("MO1012", "Invalid product type"),
            Map.entry("MO1092", "Invalid consent check"),
            Map.entry("MO1093", "Invalid TOTP"),
            Map.entry("MO1094", "Invalid TOTP key"),
            Map.entry("MO1095", "Invalid OTP session"),
            Map.entry("MO1096", "Invalid OTP attempt count"),
            Map.entry("MO1097", "Invalid authorization verification"),
            Map.entry("MO1098", "Invalid dealer access"),
            Map.entry("MO1099", "Invalid OTP format"),
            Map.entry("MO1100", "Invalid login access"),
            // ── Order / Trade State ────────────────────────────────────────────
            Map.entry("MO1013", "Order not found"),
            Map.entry("MO1014", "Trade not found"),
            Map.entry("MO1015", "Holding not found"),
            Map.entry("MO1016", "Position not found"),
            Map.entry("MO1065", "Order status is unknown"),
            Map.entry("MO1066", "Order is already cancelled"),
            Map.entry("MO1067", "Order is already traded"),
            Map.entry("MO1068", "Order is already rejected"),
            Map.entry("MO1069", "Order status is error"),
            // ── Order Input Validation ─────────────────────────────────────────
            Map.entry("MO1050", "Invalid input parameter"),
            Map.entry("MO1051", "Invalid exchange input parameter"),
            Map.entry("MO1052", "Invalid buy/sell input parameter"),
            Map.entry("MO1053", "Invalid book type input parameter"),
            Map.entry("MO1054", "Invalid order duration parameter"),
            Map.entry("MO1055", "Invalid quantity parameter"),
            Map.entry("MO1056", "Invalid disclosed quantity parameter"),
            Map.entry("MO1057", "Invalid product type parameter — use DELIVERY/NORMAL/INTRADAY etc."),
            Map.entry("MO1058", "Invalid scrip code input parameter"),
            Map.entry("MO1059", "Invalid quantity — must be multiple of market lot"),
            Map.entry("MO1060", "Invalid order ID input parameter"),
            Map.entry("MO1061", "Invalid AMO order input parameter"),
            Map.entry("MO1062", "Client code is required in input parameter"),
            Map.entry("MO1063", "Invalid good-till-date in input parameter"),
            Map.entry("MO1064", "Invalid algo ID input parameter"),
            Map.entry("MO1070", "UniqueOrderId is required in input parameter"),
            Map.entry("MO1071", "Invalid disclosed quantity + IOC combination"),
            Map.entry("MO1072", "Market order not allowed for this product type"),
            Map.entry("MO1073", "Limit price is below lower circuit limit (LCL)"),
            Map.entry("MO1074", "Limit price is above upper circuit limit (UCL)"),
            Map.entry("MO1075", "Trigger price is required for this order type"),
            Map.entry("MO1076", "Invalid trigger price"),
            Map.entry("MO1077", "Trigger price must be less than order price"),
            Map.entry("MO1078", "Trigger price must be more than order price"),
            Map.entry("MO1079", "Invalid trigger price and order type combination"),
            Map.entry("MO1080", "Tag field exceeds maximum allowed length"),
            Map.entry("MO1081", "Tag field contains invalid special characters"),
            Map.entry("MO1082", "Invalid exchange and disclosed quantity combination"),
            Map.entry("MO1083", "Invalid order type and price combination"),
            Map.entry("MO1084", "Invalid exchange and product type combination"),
            Map.entry("MO1085", "Invalid order duration and GTD combination"),
            Map.entry("MO1086", "Invalid exchange and GTC combination"),
            Map.entry("MO1087", "Client code cannot be a dealer ID"),
            Map.entry("MO1088", "Client code cannot be a vendor ID"),
            Map.entry("MO1089", "Invalid last modified time in modify order request"),
            Map.entry("MO1090", "Invalid quantity traded today in modify order request"),
            Map.entry("MO1091", "Invalid URL for modify order"),
            Map.entry("MO1101", "Invalid time format"),
            Map.entry("MO1102", "Invalid participant code input"),
            Map.entry("MO1103", "Bracket order cannot be modified"),
            Map.entry("MO1104", "Bracket order cannot be cancelled"),
            // ── Session ────────────────────────────────────────────────────────
            Map.entry("MO2000", "Maximum active sessions exceeded"),
            Map.entry("MO2001", "Method not allowed"),
            Map.entry("MO2002", "Invalid JSON format in request body"),
            Map.entry("MO2035", "Unauthorized IP address — whitelist your server IP"),
            // ── Header Validation ──────────────────────────────────────────────
            Map.entry("MO2003", "Invalid Accept header"),
            Map.entry("MO2004", "Invalid User-Agent header"),
            Map.entry("MO2005", "Invalid API key in header"),
            Map.entry("MO2006", "Invalid API secret key in header"),
            Map.entry("MO2007", "Invalid Authorization in header"),
            Map.entry("MO2008", "Invalid MAC address in header"),
            Map.entry("MO2009", "Invalid client local IP in header"),
            Map.entry("MO2010", "Invalid client public IP in header"),
            Map.entry("MO2011", "Invalid source ID in header"),
            Map.entry("MO2012", "Invalid vendor tag in header"),
            Map.entry("MO2013", "Invalid version number in header"),
            Map.entry("MO2014", "Invalid OS name in header"),
            Map.entry("MO2015", "Invalid OS version in header"),
            Map.entry("MO2016", "Invalid device model in header"),
            Map.entry("MO2017", "Invalid manufacturer in header"),
            Map.entry("MO2018", "Invalid product name in header"),
            Map.entry("MO2019", "Invalid product version in header"),
            Map.entry("MO2020", "Invalid installed app ID in header"),
            Map.entry("MO2021", "Invalid IMEI number in header"),
            Map.entry("MO2022", "Invalid browser name in header"),
            Map.entry("MO2023", "Invalid browser version in header"),
            Map.entry("MO2024", "Invalid latitude in header"),
            Map.entry("MO2025", "Invalid longitude in header"),
            Map.entry("MO2026", "Invalid SDK version in header"),
            // ── Network / Server ───────────────────────────────────────────────
            Map.entry("MO5000", "Motilal server connectivity failed"),
            Map.entry("MO5001", "Request failed due to network issue"),
            Map.entry("MO5002", "Response receive failed due to network issue"),
            Map.entry("MO5003", "Invalid order input parameter"),
            // ── Misc ───────────────────────────────────────────────────────────
            Map.entry("MO3001", "Multiple records found")
    );

    /**
     * Returns a descriptive message for the given Motilal error code. Falls
     * back to the raw API message if the code is unknown.
     */
    public static String describe(String errorCode, String fallbackMessage) {
        if (errorCode == null || errorCode.isBlank()) {
            return fallbackMessage;
        }
        String mapped = CODES.get(errorCode.toUpperCase().trim());
        if (mapped != null) {
            return "[" + errorCode + "] " + mapped;
        }
        // Unknown code — still include it for traceability
        return "[" + errorCode + "] " + (fallbackMessage != null ? fallbackMessage : "Unknown error");
    }
}
