package com.py.brokers.dhan.dto;

/**
 * Request params for converting an open position product type.
 * POST /v2/positions/convert
 *
 * Examples:
 *   INTRADAY → CNC  (MIS to delivery)
 *   CNC → INTRADAY  (delivery to MIS)
 */

public class DhanConvertPositionParams {

    /** Required – Dhan client ID */
    private String dhanClientId;

    /**
     * Required – Current product type of the open position.
     * Allowed: CNC | INTRADAY | MARGIN
     */
    private String fromProductType;

    /**
     * Required – Exchange segment in which position is held.
     * Allowed: NSE_EQ | NSE_FNO | NSE_CURRENCY | BSE_EQ | BSE_FNO | BSE_CURRENCY | MCX_COMM
     */
    private String exchangeSegment;

    /**
     * Required – Direction of the open position.
     * Allowed: LONG | SHORT | CLOSED
     */
    private String positionType;

    /** Required – Security ID of the scrip */
    private String securityId;

    /** Optional – Trading symbol (can be empty string) */
    private String tradingSymbol;

    /** Required – Number of shares to convert */
    private int convertQty;

    /**
     * Required – Target product type after conversion.
     * Allowed: CNC | INTRADAY | MARGIN
     */
    private String toProductType;

    /**
     * @return the dhanClientId
     */
    public String getDhanClientId() {
        return dhanClientId;
    }

    /**
     * @param dhanClientId the dhanClientId to set
     */
    public void setDhanClientId(String dhanClientId) {
        this.dhanClientId = dhanClientId;
    }

    /**
     * @return the fromProductType
     */
    public String getFromProductType() {
        return fromProductType;
    }

    /**
     * @param fromProductType the fromProductType to set
     */
    public void setFromProductType(String fromProductType) {
        this.fromProductType = fromProductType;
    }

    /**
     * @return the exchangeSegment
     */
    public String getExchangeSegment() {
        return exchangeSegment;
    }

    /**
     * @param exchangeSegment the exchangeSegment to set
     */
    public void setExchangeSegment(String exchangeSegment) {
        this.exchangeSegment = exchangeSegment;
    }

    /**
     * @return the positionType
     */
    public String getPositionType() {
        return positionType;
    }

    /**
     * @param positionType the positionType to set
     */
    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }

    /**
     * @return the securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * @param securityId the securityId to set
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * @return the tradingSymbol
     */
    public String getTradingSymbol() {
        return tradingSymbol;
    }

    /**
     * @param tradingSymbol the tradingSymbol to set
     */
    public void setTradingSymbol(String tradingSymbol) {
        this.tradingSymbol = tradingSymbol;
    }

    /**
     * @return the convertQty
     */
    public int getConvertQty() {
        return convertQty;
    }

    /**
     * @param convertQty the convertQty to set
     */
    public void setConvertQty(int convertQty) {
        this.convertQty = convertQty;
    }

    /**
     * @return the toProductType
     */
    public String getToProductType() {
        return toProductType;
    }

    /**
     * @param toProductType the toProductType to set
     */
    public void setToProductType(String toProductType) {
        this.toProductType = toProductType;
    }
}