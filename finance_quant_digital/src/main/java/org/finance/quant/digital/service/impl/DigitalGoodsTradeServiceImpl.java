/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.service.impl;

import com.alibaba.fastjson.JSON;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.OrderStatus;
import com.binance.api.client.domain.account.AssetBalance;
import static com.binance.api.client.domain.account.NewOrder.marketBuy;
import static com.binance.api.client.domain.account.NewOrder.marketSell;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.account.NewOrderResponseType;
import com.binance.api.client.domain.market.TickerStatistics;
import org.apache.log4j.Logger;
import org.finance.quant.digital.constants.ApiKeyConstants;
import org.finance.quant.digital.constants.TradeConstants;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.MathUtils;
import org.finance.quant.digital.utils.RetryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hanqing.zf
 * @version : DigitalTradeServiceImpl.java, v 0.1 2021年05月10日 7:30 下午 hanqing.zf Exp $
 */
@Service
public class DigitalGoodsTradeServiceImpl implements DigitalGoodsTradeService, InitializingBean {
    private static final Logger               LOGGER = Logger.getLogger(DigitalGoodsTradeServiceImpl.class);
    /**
     * client
     */
    private              BinanceApiRestClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(ApiKeyConstants.API_KEY, ApiKeyConstants.SECRET_KEY);
        client = factory.newRestClient();
    }

    @Override
    public BinanceApiRestClient getClient() {
        return this.client;
    }

    @Override
    public NewOrderResponse trade(String symbol, OrderSide side, double quantity) {
        NewOrderResponse orderResponse = null;
        try {
            orderResponse = RetryUtils.retry(() -> {
                if (side == OrderSide.BUY) {
                    return call(symbol, quantity);
                } else {
                    return put(symbol, quantity);
                }
            }, 1, 3);
            List<String> asssets = Arrays.stream(AssetsEnum.values()).map(assetsEnum -> assetsEnum.name()).collect(Collectors.toList());
            List<AssetBalance> balances = client.getAccount().getBalances().stream().filter(
                    balance -> asssets.contains(balance.getAsset())).collect(
                    Collectors.toList());
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("ACCOUNT:[" + JSON.toJSONString(balances) + "]");
            }
        } catch (Exception e) {
            LOGGER.error("Trade Buy Exception:" + e.getMessage(), e);
        }
        return orderResponse;
    }

    @Override
    public TickerStatistics getTicker(String symbol) {
        TickerStatistics hrPriceStatistics = client.get24HrPriceStatistics(symbol);
        return hrPriceStatistics;
    }

    @Override
    public double assetBanlance(String asset, int scala) {
        AssetBalance assetBalance = client.getAccount().getAssetBalance(asset);
        BigDecimal free = new BigDecimal(assetBalance.getFree()).setScale(6, RoundingMode.DOWN);
        return free.doubleValue();
    }

    /**
     * 市价做多交易
     *
     * @param symbol
     * @param quantity
     * @return
     */
    private NewOrderResponse call(String symbol, double quantity) {
        if (quantity > TradeConstants.QUANTITY_MIN_SIZE) {
            //取精度
            String accuracy = new BigDecimal(quantity).setScale(6, BigDecimal.ROUND_DOWN).toPlainString();
            TickerStatistics hrPriceStatistics = client.get24HrPriceStatistics(symbol);
            double latestPrice = Double.parseDouble(hrPriceStatistics.getLastPrice());
            if (MathUtils.multiply(quantity, latestPrice) >= TradeConstants.AMOUNT_MIN_SIZE) {
                NewOrderResponse response = client.newOrder(
                        marketBuy(symbol, accuracy).newOrderRespType(NewOrderResponseType.FULL));
                if (response.getStatus() != OrderStatus.FILLED) {
                    throw new RuntimeException("Buy Order Failed,Response:" + JSON.toJSONString(response));
                }
                response.getFills().forEach(trade -> {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("DIRECTION：" + "BUY" + "   PRICE:" + trade.getPrice() + "   NUM:" + trade.getQty());
                    }
                });
                return response;
            }
        }
        return null;
    }

    /**
     * 市价做空交易
     *
     * @param symbol
     * @param quantity
     * @return
     */
    private NewOrderResponse put(String symbol, double quantity) {
        if (quantity > TradeConstants.QUANTITY_MIN_SIZE) {
            //取精度
            String accuracy = new BigDecimal(quantity).setScale(6, BigDecimal.ROUND_DOWN).toPlainString();
            NewOrderResponse response = client.newOrder(marketSell(symbol, accuracy).newOrderRespType(NewOrderResponseType.FULL));
            if (response.getStatus() != OrderStatus.FILLED) {
                throw new RuntimeException("Sell Order Failed,Response:" + JSON.toJSONString(response));
            }
            response.getFills().forEach(trade -> {
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("DIRECTION：" + "SELL" + "   PRICE:" + trade.getPrice() + "   NUM:" + trade.getQty());
                }
            });
            return response;
        }
        return null;
    }
}