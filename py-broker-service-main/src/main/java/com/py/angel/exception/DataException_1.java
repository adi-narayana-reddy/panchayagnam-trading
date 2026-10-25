package com.py.angel.exception;

/**
 * Exceptions raised when invalid data is returned from Smart API trade.
 */

@SuppressWarnings("serial")
public class DataException_1 extends SmartAPIException {

    // initialize 2fa exception and call constructor of Base Exception
    public DataException_1(String message, String code){
        super(message, code);
    }
}


