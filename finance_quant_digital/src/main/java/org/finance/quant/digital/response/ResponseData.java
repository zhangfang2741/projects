/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package org.finance.quant.digital.response;

/**
 * @author huichi.wr
 * @version ResponseData.java, v 0.1 2019年09月25日 11:43 huichi Exp $
 */
public class ResponseData {
    /**
     * status code, default 200
     */
    private int statusCode = 200;

    /**
     * message
     */
    private String message;

    /**
     * response data
     */
    private Object data;

    /**
     * Getter method for property <tt>statusCode</tt>.
     *
     * @return property value of statusCode
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Setter method for property <tt>statusCode</tt>.
     *
     * @param statusCode value to be assigned to property statusCode
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Getter method for property <tt>message</tt>.
     *
     * @return property value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Setter method for property <tt>message</tt>.
     *
     * @param message value to be assigned to property message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Getter method for property <tt>data</tt>.
     *
     * @return property value of data
     */
    public Object getData() {
        return data;
    }

    /**
     * Setter method for property <tt>data</tt>.
     *
     * @param data value to be assigned to property data
     */
    public void setData(Object data) {
        this.data = data;
    }
}
