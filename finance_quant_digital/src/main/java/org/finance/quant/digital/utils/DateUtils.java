/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author hanqing.zf
 * @version : DateUtils.java, v 0.1 2021年05月08日 11:43 上午 hanqing.zf Exp $
 */
public class DateUtils {
    public static final String           TIME_ZONE   = "Asia/Shanghai";
    /**
     * dateformate
     */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * format
     *
     * @param date
     * @return
     */
    public static String format(Date date) {
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            throw new RuntimeException("DateUtils.format exception:" + e.getMessage() + " date:" + date);
        }
    }

    /**
     * format
     *
     * @param date
     * @return
     */
    public static Date parse(String date) {
        try {
            return dateFormat.parse(date);
        } catch (Exception e) {
            throw new RuntimeException("DateUtils.parse exception:" + e.getMessage() + " date:" + date);
        }
    }

    /**
     * 获取某天的时间
     *
     * @param index 为正表示当前时间加天数，为负表示当前时间减天数
     * @return String
     */
    public static Long getDay(int index) {
        TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, index);
        return calendar.getTime().getTime();
    }

    public static Long getMinute(int index) {
        TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE));
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, index);
        return calendar.getTime().getTime();
    }

}