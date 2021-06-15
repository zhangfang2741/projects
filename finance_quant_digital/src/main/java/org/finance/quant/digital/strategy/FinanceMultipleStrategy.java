/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.strategy;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.apache.log4j.Logger;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.DateUtils;
import org.finance.quant.digital.utils.TaLibUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hanqing.zf
 * @version : FinanceWmaStrategy.java, v 0.1 2021年06月13日 4:20 下午 hanqing.zf Exp $
 */
@Service
public class FinanceMultipleStrategy extends BaseStrategy {
    private static final Logger                   LOGGER = Logger.getLogger(FinanceMultipleStrategy.class);
    @Autowired
    private              DigitalGoodsTradeService digitalGoodsTradeService;

    /**
     * 综合策略
     * 买入:
     * Sar 点在股价下方
     * and
     * 7分线wma在25分线wma上方且大于5
     * <p>
     * <p>
     * <p>
     * 卖出:
     * Sar 点在股价上方
     * and
     * 7分线wma在25分线wma下方
     * and
     * 股价跌破7分线wma
     *
     * @param currency
     * @param goods
     */
    @Override
    public void execute(AssetsEnum currency, AssetsEnum goods) {
        BinanceApiRestClient client = digitalGoodsTradeService.getClient();
        String symbol = goods.name() + currency.name();
        MONITOR_SCHEDULER.scheduleAtFixedRate(() -> {
            List<Candlestick> candlesticks = client.getCandlestickBars(symbol, CandlestickInterval.ONE_MINUTE, Integer.MAX_VALUE,
                    DateUtils.getMinute(-300), DateUtils.getMinute(0));
            /**
             * 执行买入策略
             */
            buy(candlesticks, currency, goods);
            /**
             * 执行卖出策略
             */
            sell(candlesticks, currency, goods);

        }, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * * 买入:
     * * Sar 点在股价下方
     * * and
     * * 7分线wma在25分线wma上方且大于5
     */
    private void buy(List<Candlestick> candlesticks, AssetsEnum currency, AssetsEnum goods) {
        //1、计算当前股价
        double price = Double.parseDouble(candlesticks.get(candlesticks.size() - 1).getClose());
        //2、SAR计算
        double[] inHigh = candlesticks.stream()
                .map(candlestick -> Double.parseDouble(candlestick.getHigh()))
                .mapToDouble(candlestick -> candlestick.doubleValue())
                .toArray();
        double[] inLow = candlesticks.stream()
                .map(candlestick -> Double.parseDouble(candlestick.getLow()))
                .mapToDouble(candlestick -> candlestick.doubleValue())
                .toArray();
        double[] sar = TaLibUtils.sar(inHigh, inLow, 0.09, 0.9);

        //3、7分线wma、25分线wma计算
        double[] inClose = candlesticks.stream()
                .map(candlestick -> Double.parseDouble(candlestick.getClose()))
                .mapToDouble(candlestick -> candlestick.doubleValue())
                .toArray();
        double[] wma7 = TaLibUtils.ma(inClose, 7);
        double[] wma25 = TaLibUtils.ma(inClose, 25);

        //4、Sar 点在股价下方
        if (sar[sar.length - 1] < price && sar[sar.length - 2] >= price) {
            double free = digitalGoodsTradeService.assetBanlance(currency.name(), 6);
            //计算最大买入量
            double quantity = new BigDecimal(free / price).setScale(6, RoundingMode.DOWN)
                    .doubleValue();
            String symbol = goods.name() + currency.name();
            digitalGoodsTradeService.trade(symbol, OrderSide.BUY, quantity);
        }
    }

    /**
     * * 卖出:
     * * Sar 点在股价上方
     * * and
     * * 7分线wma在25分线wma下方
     * * and
     * * 股价跌破7分线wma
     */
    private void sell(List<Candlestick> candlesticks, AssetsEnum currency, AssetsEnum goods) {
        //1、计算当前股价
        double price = Double.parseDouble(candlesticks.get(candlesticks.size() - 1).getClose());
        //2、SAR计算
        double[] inHigh = candlesticks.stream()
                .map(candlestick -> Double.parseDouble(candlestick.getHigh()))
                .mapToDouble(candlestick -> candlestick.doubleValue())
                .toArray();
        double[] inLow = candlesticks.stream()
                .map(candlestick -> Double.parseDouble(candlestick.getLow()))
                .mapToDouble(candlestick -> candlestick.doubleValue())
                .toArray();
        double[] sar = TaLibUtils.sar(inHigh, inLow, 0.09, 0.9);

        //3、7分线wma、25分线wma计算
        double[] inClose = candlesticks.stream()
                .map(candlestick -> Double.parseDouble(candlestick.getClose()))
                .mapToDouble(candlestick -> candlestick.doubleValue())
                .toArray();
        double[] wma7 = TaLibUtils.ma(inClose, 7);
        double[] wma25 = TaLibUtils.ma(inClose, 25);

        //4、Sar 点在股价上方 and 7分线wma在25分线wma下方 and 股价跌破7分线wma
        if (sar[sar.length - 1] > price || wma7[wma7.length - 1] < wma7[wma7.length - 2]-20) {
            double free = digitalGoodsTradeService.assetBanlance(goods.name(), 6);
            String symbol = goods.name() + currency.name();
            digitalGoodsTradeService.trade(symbol, OrderSide.SELL, free);
        }

    }
}