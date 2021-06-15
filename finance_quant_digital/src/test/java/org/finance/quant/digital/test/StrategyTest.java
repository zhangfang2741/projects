/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.test;

import com.binance.client.model.enums.OrderSide;
import com.binance.client.model.trade.Order;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalContractTradeService;
import org.finance.quant.digital.strategy.FinanceSarStrategy;
import org.finance.quant.digital.strategy.FinanceMultipleStrategy;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author hanqing.zf
 * @version : StrategyTest.java, v 0.1 2021年05月11日 1:31 下午 hanqing.zf Exp $
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyTest {
    @Autowired
    private DigitalContractTradeService digitalContractTradeService;
    @Autowired
    private FinanceSarStrategy          financeSarStrategy;
    @Autowired
    private FinanceMultipleStrategy     financeMultipleStrategy;

    @Test
    public void test1() throws InterruptedException {
        financeSarStrategy.execute(AssetsEnum.USDT, AssetsEnum.BTC);
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Test
    public void test2() throws InterruptedException {
        Order order = digitalContractTradeService.marketTrade(AssetsEnum.BTC.name() + AssetsEnum.USDT.name(), OrderSide.BUY, "0.002");
    }

    @Test
    public void test3() throws InterruptedException {
        financeMultipleStrategy.execute(AssetsEnum.USDT, AssetsEnum.BTC);
        Thread.sleep(Integer.MAX_VALUE);
    }

}