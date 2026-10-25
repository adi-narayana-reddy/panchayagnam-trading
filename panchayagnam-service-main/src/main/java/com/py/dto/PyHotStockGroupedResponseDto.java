package com.py.dto;

import java.util.List;

public class PyHotStockGroupedResponseDto {

    private List<PyHotStockResponseDto> intraday;
    private List<PyHotStockResponseDto> shortTerm;
    private List<PyHotStockResponseDto> midTerm;
    private List<PyHotStockResponseDto> longTerm;

    private int intradayCount;
    private int shortTermCount;
    private int midTermCount;
    private int longTermCount;
    private int totalCount;

    public List<PyHotStockResponseDto> getIntraday() { return intraday; }
    public void setIntraday(List<PyHotStockResponseDto> intraday) {
        this.intraday = intraday;
        this.intradayCount = intraday != null ? intraday.size() : 0;
    }

    public List<PyHotStockResponseDto> getShortTerm() { return shortTerm; }
    public void setShortTerm(List<PyHotStockResponseDto> shortTerm) {
        this.shortTerm = shortTerm;
        this.shortTermCount = shortTerm != null ? shortTerm.size() : 0;
    }

    public List<PyHotStockResponseDto> getMidTerm() { return midTerm; }
    public void setMidTerm(List<PyHotStockResponseDto> midTerm) {
        this.midTerm = midTerm;
        this.midTermCount = midTerm != null ? midTerm.size() : 0;
    }

    public List<PyHotStockResponseDto> getLongTerm() { return longTerm; }
    public void setLongTerm(List<PyHotStockResponseDto> longTerm) {
        this.longTerm = longTerm;
        this.longTermCount = longTerm != null ? longTerm.size() : 0;
    }

    public int getIntradayCount() { return intradayCount; }
    public int getShortTermCount() { return shortTermCount; }
    public int getMidTermCount() { return midTermCount; }
    public int getLongTermCount() { return longTermCount; }

    public int getTotalCount() { return intradayCount + shortTermCount + midTermCount + longTermCount; }
}