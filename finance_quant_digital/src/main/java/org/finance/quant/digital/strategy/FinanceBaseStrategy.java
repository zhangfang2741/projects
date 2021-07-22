/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.strategy;

import org.finance.quant.digital.enums.AssetsEnum;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author hanqing.zf
 * @version : BaseStrategy.java, v 0.1 2021年05月11日 9:51 上午 hanqing.zf Exp $
 */
public abstract class FinanceBaseStrategy {
    /**
     * init Scheduled Executor
     */
    public ScheduledExecutorService MONITOR_SCHEDULER = Executors.newScheduledThreadPool(10);

    /**
     * 具体策略执行
     *
     * @param currency
     * @param goods
     */
    public abstract void execute(AssetsEnum currency, AssetsEnum goods);
}