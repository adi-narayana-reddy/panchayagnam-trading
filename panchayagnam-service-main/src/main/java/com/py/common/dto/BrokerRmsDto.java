package com.py.common.dto;

public class BrokerRmsDto {

    // ── Single field frontend reads — maps from all brokers ───────────────
    private Double availableCash;        // AngelOne: availablecash
    // Motilal:  srno 102 (cash margin)
    // Dhan:     availableBalance

    private Double collateral;           // AngelOne: collateral
    // Motilal:  -
    // Dhan:     collateralAmount

    private Double utilizedAmount;       // AngelOne: utilisedDebits
    // Motilal:  srno 301
    // Dhan:     utilizedAmount

    private Double withdrawableBalance;  // AngelOne: net
    // Motilal:  -
    // Dhan:     withdrawableBalance

    private Double totalPnl;             // AngelOne: m2mRealized + m2mUnrealized
    // Motilal:  srno 600
    // Dhan:     -

    private Double availableFno;         // AngelOne: availableLimitMargin
    // Motilal:  srno 103
    // Dhan:     -

    // getters & setters
    public Double getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(Double availableCash) {
        this.availableCash = availableCash;
    }

    public Double getCollateral() {
        return collateral;
    }

    public void setCollateral(Double collateral) {
        this.collateral = collateral;
    }

    public Double getUtilizedAmount() {
        return utilizedAmount;
    }

    public void setUtilizedAmount(Double utilizedAmount) {
        this.utilizedAmount = utilizedAmount;
    }

    public Double getWithdrawableBalance() {
        return withdrawableBalance;
    }

    public void setWithdrawableBalance(Double withdrawableBalance) {
        this.withdrawableBalance = withdrawableBalance;
    }

    public Double getTotalPnl() {
        return totalPnl;
    }

    public void setTotalPnl(Double totalPnl) {
        this.totalPnl = totalPnl;
    }

    public Double getAvailableFno() {
        return availableFno;
    }

    public void setAvailableFno(Double availableFno) {
        this.availableFno = availableFno;
    }
}
