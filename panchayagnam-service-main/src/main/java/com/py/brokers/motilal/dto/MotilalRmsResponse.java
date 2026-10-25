package com.py.brokers.motilal.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MotilalRmsResponse {

    private String clientId;
    private String userName;
    private String status;          // SUCCESS / FAILED / TOKEN_MISSING
    private String errorMessage;

    // ✅ Mapped from Motilal getreportmargindetail srno array
    private String availableMargin;       // srno 100 — Total Available Margin (A-B-C-D)
    private String creditLimit;           // srno 101 — Credit Limit
    private String availableCash;         // srno 102 — Available for Cash / SLBM Segment
    private String availableFO;           // srno 103 — Available for FO Segment
    private String availableCurrency;     // srno 104 — Available for Currency Segment
    private String availableCommodity;    // srno 105 — Available for Commodity Segment
    private String availableIPO;          // srno 106 — Available for IPO/MF/FD/BOND / Option Buy
    private String totalMargin;           // srno 200 — Total Margin (A)
    private String cashBalanceCashMargin; // srno 201 — Cash Balance (Cash Margin)
    private String cashBalanceLedger;     // srno 202 — Cash Balance (Ledger Balance)
    private String normalFundTransfer;    // srno 203 — Add: Normal Fund Transfer

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getAvailableMargin() {
        return availableMargin;
    }

    public void setAvailableMargin(String availableMargin) {
        this.availableMargin = availableMargin;
    }

    public String getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(String creditLimit) {
        this.creditLimit = creditLimit;
    }

    public String getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(String availableCash) {
        this.availableCash = availableCash;
    }

    public String getAvailableFO() {
        return availableFO;
    }

    public void setAvailableFO(String availableFO) {
        this.availableFO = availableFO;
    }

    public String getAvailableCurrency() {
        return availableCurrency;
    }

    public void setAvailableCurrency(String availableCurrency) {
        this.availableCurrency = availableCurrency;
    }

    public String getAvailableCommodity() {
        return availableCommodity;
    }

    public void setAvailableCommodity(String availableCommodity) {
        this.availableCommodity = availableCommodity;
    }

    public String getAvailableIPO() {
        return availableIPO;
    }

    public void setAvailableIPO(String availableIPO) {
        this.availableIPO = availableIPO;
    }

    public String getTotalMargin() {
        return totalMargin;
    }

    public void setTotalMargin(String totalMargin) {
        this.totalMargin = totalMargin;
    }

    public String getCashBalanceCashMargin() {
        return cashBalanceCashMargin;
    }

    public void setCashBalanceCashMargin(String cashBalanceCashMargin) {
        this.cashBalanceCashMargin = cashBalanceCashMargin;
    }

    public String getCashBalanceLedger() {
        return cashBalanceLedger;
    }

    public void setCashBalanceLedger(String cashBalanceLedger) {
        this.cashBalanceLedger = cashBalanceLedger;
    }

    public String getNormalFundTransfer() {
        return normalFundTransfer;
    }

    public void setNormalFundTransfer(String normalFundTransfer) {
        this.normalFundTransfer = normalFundTransfer;
    }
}
