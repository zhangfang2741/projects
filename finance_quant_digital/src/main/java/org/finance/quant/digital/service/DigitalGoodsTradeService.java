/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.service;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.NewOrderResponse;
import com.binance.api.client.domain.market.TickerStatistics;

/**
 * @author hanqing.zf
 * @version : DigitalTradeService.java, v 0.1 2021年05月10日 7:28 下午 hanqing.zf Exp $
 */
public interface DigitalGoodsTradeService {
    /**
     * 获取客户端
     *
     * @return
     */
    BinanceApiRestClient getClient();

    /**
     * call && put
     *
     * @param symbol
     * @param side
     * @param quantity
     * @return
     */
    NewOrderResponse trade(String symbol, OrderSide side, double quantity);

    /**
     * Get Ticker
     *
     * @param symbol
     * @return
     */
    TickerStatistics getTicker(String symbol);

    /**
     * 获取指定精度的资产数量
     *
     * @param symbol
     * @param scala
     * @return
     */
    double assetBanlance(String symbol, int scala);
}