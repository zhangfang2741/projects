/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.utils;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * @author hanqing.zf
 * @version : MathUtils.java, v 0.1 2021年05月09日 3:07 下午 hanqing.zf Exp $
 */
public class MathUtils {

    /**
     * double除法
     *
     * @param a
     * @param b
     * @return
     */
    public static double division(double a, double b) {
        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.divide(b2).doubleValue();
    }
    /**
     * double乘法
     *
     * @param a
     * @param b
     * @return
     */
    public static double multiply(double a, double b) {
        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * double加法
     *
     * @param a
     * @param b
     * @return
     */
    public static double add(double a, double b) {
        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.add(b2).doubleValue();
    }

    /**
     * double减法
     *
     * @param a
     * @param b
     * @return
     */
    public static double subtraction(double a, double b) {
        BigDecimal b1 = new BigDecimal(a);
        BigDecimal b2 = new BigDecimal(b);
        return b1.subtract(b2).doubleValue();
    }
}