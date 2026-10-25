package com.py.angel.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SearchScripResponseDTO_1 {

    @JsonProperty("tradingsymbol")
    private String tradingSymbol;

    @JsonProperty("exchange")
    private String exchange;

    @JsonProperty("symboltoken")
    private String symbolToken;

}

