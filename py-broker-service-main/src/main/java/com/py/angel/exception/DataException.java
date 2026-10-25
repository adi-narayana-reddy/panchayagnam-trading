/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.py.angel.exception;

/**
 * Exceptions raised when invalid data is returned from Smart API trade.
 */

@SuppressWarnings("serial")
public class DataException extends SmartAPIException {

    // initialize 2fa exception and call constructor of Base Exception
    public DataException(String message, String code){
        super(message, code);
    }
}


