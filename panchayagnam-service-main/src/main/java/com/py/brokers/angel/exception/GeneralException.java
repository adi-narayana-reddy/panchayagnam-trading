package com.py.brokers.angel.exception;

/**
 * An unclassified, general error. Default code is 500
 */
@SuppressWarnings("serial")
public class GeneralException extends SmartAPIException {
    // initialize and call the base class
    public GeneralException(String message, String code){
        super(message, code);
    }
}

