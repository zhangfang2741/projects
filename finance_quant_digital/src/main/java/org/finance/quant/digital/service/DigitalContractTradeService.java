/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.service;

import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.trade.Order;

/**
 * @author hanqing.zf
 * @version : DigitalContractTradeService.java, v 0.1 2021年05月10日 7:28 下午 hanqing.zf Exp $
 */
public interface DigitalContractTradeService {
    /**
     * 市价单
     *
     * @param symbol
     * @param side
     * @param quantity
     * @return
     */
    Order marketTrade(String symbol, OrderSide side, String quantity);
}