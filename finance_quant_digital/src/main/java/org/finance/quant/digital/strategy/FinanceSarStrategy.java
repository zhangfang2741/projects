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
import org.apache.log4j.Logger;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.DateUtils;
import org.finance.quant.digital.utils.MathUtils;
import org.finance.quant.digital.utils.TaLibUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author hanqing.zf
 * @version : FinanceQuotaStrategy.java, v 0.1 2021年05月11日 9:34 上午 hanqing.zf Exp $
 */
@Service
public class FinanceSarStrategy extends BaseStrategy {
    private static final Logger                   LOGGER = Logger.getLogger(FinanceSarStrategy.class);
    @Autowired
    private              DigitalGoodsTradeService digitalGoodsTradeService;

    /**
     * 买:
     * 1、sar<price
     * 卖:
     * 1、sar>price
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
            //get order book
            OrderBook orderBook = client.getOrderBook(symbol, 5);

            double[] inHigh = candlesticks.stream()
                    .map(candlestick -> Double.parseDouble(candlestick.getHigh()))
                    .mapToDouble(candlestick -> candlestick.doubleValue())
                    .toArray();
            double[] inLow = candlesticks.stream()
                    .map(candlestick -> Double.parseDouble(candlestick.getLow()))
                    .mapToDouble(candlestick -> candlestick.doubleValue())
                    .toArray();
            //SAR指标信息
            double[] sar = TaLibUtils.sar(inHigh, inLow, 0.09, 0.9);

            if (MathUtils.subtraction(Double.parseDouble(candlesticks.get(candlesticks.size() - 1).getClose()), sar[sar.length - 1])
                    > 0.0) {
                //买股票
                AssetBalance assetBalance = client.getAccount().getAssetBalance(currency.name());
                //get account free
                double free = Double.parseDouble(assetBalance.getFree());
                //calculate first sell price
                String price = orderBook.getAsks().get(0).getPrice();
                if (Double.parseDouble(price) <= 0.0) { return; }
                //计算可以买入的数量，数量求6位精度,后续的截断（数字货币要求最小买入数量是：0.000001）,并对科学浮点数进行字符串转
                double quantity = new BigDecimal(free/Double.parseDouble(price)).setScale(6, RoundingMode.DOWN)
                        .doubleValue();
                digitalGoodsTradeService.trade(symbol, OrderSide.BUY, quantity);
            } else {
                //卖股票
                AssetBalance goodsBalance = client.getAccount().getAssetBalance(goods.name());
                //get account free
                double free = Double.parseDouble(goodsBalance.getFree());
                //calculate first sell price
                String price = orderBook.getBids().get(0).getPrice();
                if (Double.parseDouble(price) <= 0.0) { return; }
                digitalGoodsTradeService.trade(symbol, OrderSide.SELL, free);
            }

        }, 0, 2, TimeUnit.SECONDS);
    }
}