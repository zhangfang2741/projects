/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.utils;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MAType;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;

import java.util.Arrays;

/**
 * @author hanqing.zf
 * @version : TaLibUtils.java, v 0.1 2021年05月08日 9:53 上午 hanqing.zf Exp $
 */
public class TaLibUtils {
    /**
     * TA.LIB 核心库
     */
    private final static Core TAG_LIB = new Core();

    private static double[] trim0(double[] original) {
        double[] arr = Arrays.stream(original)
                .filter(value -> value != 0.0)
                .toArray();
        return arr;
    }

    /**
     * * 计算停损点转向指标（SAR）
     * *
     * * @param inHigh 最高价
     * * @param inLow 最低价
     * * @param optAF 加速因子AF（因子 0.02）
     * * @param optMaxAF 加速因子AF最大值（因子 0.2）
     * * @return SAR计算结果数据
     */
    public static double[] sar(double inHigh[], double inLow[], double optAF, double optMaxAF) {
        int startIdx = 0;
        int endIdx = inHigh.length - 1;
        double[] sarReal = new double[inHigh.length];
        RetCode retCode = TAG_LIB.sar(
                startIdx, endIdx, inHigh, inLow, optAF, optMaxAF, new MInteger(), new MInteger(), sarReal);
        if (retCode == RetCode.Success) {
            return trim0(sarReal);
        }
        return null;
    }

    /**
     * 计算布林线指标（BOLL：Bolinger Bands）
     *
     * @param inReal        收盘价
     * @param optTimePeriod 均线天数（因子）
     * @param optNbDevUp    上轨线标准差（因子 默认为2）
     * @param optNbDevDn    下轨线标准差（因子 默认为2）
     * @return BOLL计算结果
     */
    public static double[][] boll(double inReal[], int optTimePeriod, double optNbDevUp, double optNbDevDn) {
        int startIdx = 0;
        int endIdx = inReal.length - 1;
        MAType maType = MAType.Sma;
        double upperBand[] = new double[inReal.length];
        double middleBand[] = new double[inReal.length];
        double lowerBand[] = new double[inReal.length];
        RetCode retCode = TAG_LIB.bbands(startIdx, endIdx, inReal, optTimePeriod, optNbDevUp, optNbDevDn, maType, new MInteger(),
                new MInteger(), upperBand, middleBand, lowerBand);
        if (retCode == RetCode.Success) {
            return new double[][] {trim0(upperBand), trim0(middleBand), trim0(lowerBand)};
        }
        return null;
    }

    /**
     * 计算平滑异同移动平均线(MACD)
     *
     * @param inReal          收盘价
     * @param optFastPeriod   快速移动平均线（因子 12日EMA）
     * @param optSlowPeriod   慢速移动平均线（因子 26日EMA）
     * @param optSignalPeriod DEA移动平均线(因子 9日EMA)
     * @return RSI数据
     */
    public static double[][] macd(double inReal[], int optFastPeriod, int optSlowPeriod, int optSignalPeriod) {
        // 基础计算库
        int startIdx = 0;
        int endIdx = inReal.length - 1;
        if (endIdx + 1 < 120) {
            throw new RuntimeException("MACD inReal Length must bigger than 120!");
        }
        double outMACD[] = new double[inReal.length];
        double outMACDSignal[] = new double[inReal.length];
        double outMACDHist[] = new double[inReal.length];
        RetCode retCode = TAG_LIB.macd(startIdx, endIdx, inReal, optFastPeriod, optSlowPeriod, optSignalPeriod, new MInteger(),
                new MInteger(), outMACD, outMACDSignal, outMACDHist);
        if (retCode == RetCode.Success) {
            return new double[][] {trim0(outMACD), trim0(outMACDSignal), trim0(outMACDHist)};
        }
        return null;
    }

    /**
     * MA
     *
     * @param inReal
     * @param optInTimePeriod
     * @return
     */
    public static double[] ma(double inReal[], int optInTimePeriod) {
        // 基础计算库
        int startIdx = 0;
        int endIdx = inReal.length - 1;
        double[] outReal = new double[inReal.length - optInTimePeriod + 1];
        RetCode retCode = TAG_LIB.movingAverage(startIdx,
                endIdx,
                inReal,
                optInTimePeriod,
                MAType.Wma,
                new MInteger(),
                new MInteger(),
                outReal);
        if (retCode == RetCode.Success) {
            return trim0(outReal);
        }
        return null;
    }

    /**
     * 三重指数平滑移动平均线
     *
     * @param inReal
     * @param optInTimePeriod
     * @return
     */
    public static double[] trix(double inReal[], int optInTimePeriod) {
        int startIdx = 0;
        int endIdx = inReal.length - 1;
        double[] outReal = new double[inReal.length - optInTimePeriod + 1];
        RetCode retCode = TAG_LIB.trix(
                startIdx,
                endIdx,
                inReal,
                optInTimePeriod,
                new MInteger(),
                new MInteger(),
                outReal
        );
        if (retCode == RetCode.Success) {
            return trim0(outReal);
        }
        return null;
    }

    /**
     * 余弦函数
     *
     * @param inReal
     * @return
     */
    public static double[] cos(double inReal[]) {
        int startIdx = 0;
        int endIdx = inReal.length - 1;
        double[] outReal = new double[inReal.length];
        RetCode retCode = TAG_LIB.cos(
                startIdx,
                endIdx,
                inReal,
                new MInteger(),
                new MInteger(),
                outReal);
        if (retCode == RetCode.Success) {
            return trim0(outReal);
        }
        return null;
    }
}