package com.py.brokers.angel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Parameters for estimating order charges")
public class EstimateChargesParams {

    @Schema(description = "Product type", example = "CNC")
    public String product_type;
    @Schema(description = "Transaction type", example = "BUY")
    public String transaction_type;
    @Schema(description = "Quantity", example = "1")
    public String quantity;
    @Schema(description = "Price", example = "850.50")
    public String price;
    @Schema(description = "Exchange", example = "NSE")
    public String exchange;
    @Schema(description = "Symbol name", example = "SBIN-EQ")
    public String symbol_name;
    @Schema(description = "Symbol token", example = "3045")
    public String token;
}

