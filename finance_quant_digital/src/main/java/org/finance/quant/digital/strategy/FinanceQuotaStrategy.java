/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.strategy;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.TickerStatistics;
import org.apache.log4j.Logger;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.DateUtils;
import org.finance.quant.digital.utils.MathUtils;
import org.finance.quant.digital.utils.TaLibUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hanqing.zf
 * @version : FinanceQuotaStrategy.java, v 0.1 2021年05月11日 9:34 上午 hanqing.zf Exp $
 */
@Service
public class FinanceQuotaStrategy extends BaseStrategy {
    private static final Logger                   LOGGER = Logger.getLogger(FinanceQuotaStrategy.class);
    @Autowired
    private              DigitalGoodsTradeService digitalGoodsTradeService;

    /**
     * 买:
     * 1、上一分钟的收盘价在99分线以上
     * 2、上一分钟SAR的值小于股价,且差值大于50
     * 3、上一分钟的收盘价大于开盘价
     * 4、macd快线大于慢线
     * <p>
     * 卖:
     * 1、上一分钟SAR的值大于股价
     * 2、资损控制在5%卖出
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
                    DateUtils.getMinute(-60 * 2), DateUtils.getMinute(0));
            //买股票
            double[] inClose = candlesticks.stream()
                    .map(candlestick -> Double.parseDouble(candlestick.getClose()))
                    .mapToDouble(candlestick -> candlestick.doubleValue())
                    .toArray();
            double[] inHigh = candlesticks.stream()
                    .map(candlestick -> Double.parseDouble(candlestick.getHigh()))
                    .mapToDouble(candlestick -> candlestick.doubleValue())
                    .toArray();
            double[] inLow = candlesticks.stream()
                    .map(candlestick -> Double.parseDouble(candlestick.getLow()))
                    .mapToDouble(candlestick -> candlestick.doubleValue())
                    .toArray();
            double[] inOpen = candlesticks.stream()
                    .map(candlestick -> Double.parseDouble(candlestick.getOpen()))
                    .mapToDouble(candlestick -> candlestick.doubleValue())
                    .toArray();
            //指标信息
            double[] ma99 = TaLibUtils.ma(inClose, 99);
            double[] sar = TaLibUtils.sar(inHigh, inLow, 0.02, 0.2);
            double[][] macd = TaLibUtils.macd(inOpen, 12, 26, 9);
            /**
             * 买入策略
             */
            TickerStatistics tickerStatistics = client.get24HrPriceStatistics(symbol);
            Candlestick previousCandlestick = candlesticks.get(candlesticks.size() - 2);
            if (Double.parseDouble(previousCandlestick.getClose()) > ma99[ma99.length - 2]) {
                if (Double.parseDouble(tickerStatistics.getLastPrice()) - sar[sar.length - 1] > 50) {
                    if (Double.parseDouble(previousCandlestick.getClose()) > Double.parseDouble(previousCandlestick.getOpen())) {
                        if (macd[0][macd[0].length - 1] > macd[1][macd[1].length - 1]) {
                            try {
                                //get order book
                                OrderBook orderBook = client.getOrderBook(symbol, 5);
                                AssetBalance assetBalance = client.getAccount().getAssetBalance(currency.name());
                                //get account free
                                double free = Double.parseDouble(assetBalance.getFree());
                                //calculate first sell price
                                String price = orderBook.getAsks().get(0).getPrice();
                                if (Double.parseDouble(price) <= 0.0) { return; }
                                //计算可以买入的数量，数量求6位精度,后续的截断（数字货币要求最小买入数量是：0.000001）,并对科学浮点数进行字符串转
                                double quantity = MathUtils.division(free, Double.parseDouble(price));
                                digitalGoodsTradeService.trade(symbol, OrderSide.BUY, quantity);
                            } catch (Exception e) {
                                LOGGER.error("ORDER CREATE FAILED:" + e.getMessage(), e);
                            }
                        }
                    }
                }
            }
            //卖股票
            //get order book
            OrderBook orderBook = client.getOrderBook(symbol, 5);
            AssetBalance assetBalance = client.getAccount().getAssetBalance(goods.name());
            //get account free
            double free = Double.parseDouble(assetBalance.getFree());
            //calculate first sell price
            String price = orderBook.getBids().get(0).getPrice();
            if (Double.parseDouble(price) <= 0.0) { return; }
            digitalGoodsTradeService.trade(symbol, OrderSide.SELL, free);
        }, 1, 5, TimeUnit.SECONDS);
    }
}