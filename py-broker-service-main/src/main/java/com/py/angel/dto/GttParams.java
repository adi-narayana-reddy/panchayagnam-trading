package com.py.angel.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "GTT (Good Till Triggered) rule parameters")
public class GttParams {
	@Schema(description = "GTT rule ID (used for modify/cancel)", example = "1001")
	public Integer id;
	@Schema(description = "Trading symbol", example = "SBIN-EQ")
	public String tradingsymbol;
	@Schema(description = "Exchange", example = "NSE", allowableValues = {"NSE", "BSE", "NFO"})
	public String exchange;
	@Schema(description = "Transaction type", example = "BUY", allowableValues = {"BUY", "SELL"})
	public String transactiontype;
	@Schema(description = "Product type", example = "CNC", allowableValues = {"NRML", "MIS", "CNC"})
	public String producttype;
	@Schema(description = "Order price", example = "850.00")
	public Double price;
	@Schema(description = "Order quantity", example = "1")
	public Integer qty;
	@Schema(description = "Trigger price", example = "845.00")
	public Double triggerprice;
	@Schema(description = "Disclosed quantity", example = "0")
	public Integer disclosedqty;
	@Schema(description = "Time period in days", example = "365")
	public Integer timeperiod;
	@Schema(description = "Symbol token", example = "3045")
	public String symboltoken;

}

