package com.py.brokers.angel.dto;

public class AngelOneConvertPositionRequest {

    private String apiKey;
    private String accessToken;

    private String exchange;
    private String symboltoken;
    private String oldproducttype;
    private String newproducttype;
    private String tradingsymbol;
    private String symbolname;
    private String instrumenttype;
    private String priceden;
    private String pricenum;
    private String genden;
    private String gennum;
    private String precision;
    private String multiplier;
    private String boardlotsize;
    private String buyqty;
    private String sellqty;
    private String buyamount;
    private String sellamount;
    private String transactiontype;
    private Integer quantity;
    private String type;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getSymboltoken() {
        return symboltoken;
    }

    public void setSymboltoken(String symboltoken) {
        this.symboltoken = symboltoken;
    }

    public String getOldproducttype() {
        return oldproducttype;
    }

    public void setOldproducttype(String oldproducttype) {
        this.oldproducttype = oldproducttype;
    }

    public String getNewproducttype() {
        return newproducttype;
    }

    public void setNewproducttype(String newproducttype) {
        this.newproducttype = newproducttype;
    }

    public String getTradingsymbol() {
        return tradingsymbol;
    }

    public void setTradingsymbol(String tradingsymbol) {
        this.tradingsymbol = tradingsymbol;
    }

    public String getSymbolname() {
        return symbolname;
    }

    public void setSymbolname(String symbolname) {
        this.symbolname = symbolname;
    }

    public String getInstrumenttype() {
        return instrumenttype;
    }

    public void setInstrumenttype(String instrumenttype) {
        this.instrumenttype = instrumenttype;
    }

    public String getPriceden() {
        return priceden;
    }

    public void setPriceden(String priceden) {
        this.priceden = priceden;
    }

    public String getPricenum() {
        return pricenum;
    }

    public void setPricenum(String pricenum) {
        this.pricenum = pricenum;
    }

    public String getGenden() {
        return genden;
    }

    public void setGenden(String genden) {
        this.genden = genden;
    }

    public String getGennum() {
        return gennum;
    }

    public void setGennum(String gennum) {
        this.gennum = gennum;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    public String getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(String multiplier) {
        this.multiplier = multiplier;
    }

    public String getBoardlotsize() {
        return boardlotsize;
    }

    public void setBoardlotsize(String boardlotsize) {
        this.boardlotsize = boardlotsize;
    }

    public String getBuyqty() {
        return buyqty;
    }

    public void setBuyqty(String buyqty) {
        this.buyqty = buyqty;
    }

    public String getSellqty() {
        return sellqty;
    }

    public void setSellqty(String sellqty) {
        this.sellqty = sellqty;
    }

    public String getBuyamount() {
        return buyamount;
    }

    public void setBuyamount(String buyamount) {
        this.buyamount = buyamount;
    }

    public String getSellamount() {
        return sellamount;
    }

    public void setSellamount(String sellamount) {
        this.sellamount = sellamount;
    }

    public String getTransactiontype() {
        return transactiontype;
    }

    public void setTransactiontype(String transactiontype) {
        this.transactiontype = transactiontype;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
