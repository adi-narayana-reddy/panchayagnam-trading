package com.py.angel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A wrapper class for margin params to get the margin using smartAPI margin url
 */
@Schema(description = "Margin calculation parameters")
public class MarginParams {

    @Schema(description = "Exchange in which instrument is listed", example = "NSE", allowableValues = {"NSE", "BSE", "NFO", "BFO", "CDS", "MCX"})
    public String exchange;

    @Schema(description = "Quantity to transact", example = "1")
    public Integer quantity;

    @Schema(description = "Price of the instrument", example = "850.50")
    public Double price;

    @Schema(description = "Product type code", example = "CNC", allowableValues = {"NRML", "MIS", "CNC", "MARGIN", "BO", "CO"})
    public String productType;

    @Schema(description = "Symbol token of the instrument", example = "3045")
    public String token;

    @Schema(description = "Trade type - Buy or Sell", example = "BUY", allowableValues = {"BUY", "SELL"})
    public String tradeType;

}

