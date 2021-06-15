/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.service.impl;

import com.binance.client.RequestOptions;
import com.binance.client.SyncRequestClient;
import com.binance.client.model.enums.*;
import com.binance.client.model.trade.Order;
import org.apache.log4j.Logger;
import org.finance.quant.digital.constants.ApiKeyConstants;
import org.finance.quant.digital.service.DigitalContractTradeService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

/**
 * 合约交易类
 *
 * @author hanqing.zf
 * @version : DigitalContractTradeServiceImpl.java, v 0.1 2021年05月12日 9:15 下午 hanqing.zf Exp $
 */
@Service
public class DigitalContractTradeServiceImpl implements DigitalContractTradeService, InitializingBean {
    /**
     * LOGGER
     */
    private static final Logger            LOGGER = Logger.getLogger(DigitalContractTradeServiceImpl.class);
    /**
     * SyncRequestClient
     */
    private              SyncRequestClient syncRequestClient;

    @Override
    public void afterPropertiesSet() throws Exception {
        syncRequestClient = SyncRequestClient.create(ApiKeyConstants.API_KEY, ApiKeyConstants.SECRET_KEY,
                new RequestOptions());
    }

    @Override
    public Order marketTrade(String symbol, OrderSide side, String quantity) {
        Order order = syncRequestClient.postOrder(symbol, side, PositionSide.BOTH, OrderType.MARKET, null,
                quantity, null, null, null, null, null, NewOrderRespType.RESULT);
        return order;
    }

}