/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.strategy;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.account.AssetBalance;
import com.binance.api.client.domain.market.TickerStatistics;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.log4j.Logger;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.MathUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

/**
 * @author hanqing.zf
 * @version : FinanceGridStrategy.java, v 0.1 2021年05月11日 9:54 上午 hanqing.zf Exp $
 */
@Service
public class FinanceGridStrategy extends FinanceBaseStrategy {
    private static final Logger                   LOGGER = Logger.getLogger(FinanceGridStrategy.class);
    @Autowired
    private              DigitalGoodsTradeService digitalGoodsTradeService;

    private static final AtomicDouble benchmark = new AtomicDouble(12.0);
    private static final AtomicDouble lattice   = new AtomicDouble(0.01);

    /**
     * 步骤:
     * 1、设置goods的currency市值基准大小benchmark=10usdt和网格区间lattice=1%;
     * 2、使用currency买入对应大小的goods
     * 3、每秒钟轮询,
     * 4、当goods的市值超过网格区间时(即大于benchmark*(benchmark+lattice)),计算网格部分的goods大小并卖出
     * 5、当goods的市值小于网格区间时(即大于benchmark*(benchmark-lattice)),计算网格部分的goods大小并买入
     * <p>
     * 优化:
     * 1、买入的时候越买越少
     *
     * @param currency
     * @param goods
     */
    @Override
    public void execute(AssetsEnum currency, AssetsEnum goods) {
        BinanceApiRestClient client = digitalGoodsTradeService.getClient();
        String symbol = goods.name() + currency.name();
        MONITOR_SCHEDULER.scheduleAtFixedRate(() -> {
            try {
                //获取当前goods的数量
                AssetBalance goodsBalance = client.getAccount().getAssetBalance(goods.name());
                //获取当前goods的价格
                TickerStatistics ticker = digitalGoodsTradeService.getTicker(symbol);
                double price = Double.parseDouble(ticker.getLastPrice());
                //计算总市值
                double total_amount = MathUtils.multiply(Double.parseDouble(goodsBalance.getFree()), price);
                //盈利卖出网格区间
                if (total_amount >= MathUtils.multiply(benchmark.doubleValue(), MathUtils.add(1.0, lattice.doubleValue()))) {
                    double subtraction = MathUtils.subtraction(total_amount, benchmark.doubleValue());
                    double profit = MathUtils.division(subtraction, price);
                    digitalGoodsTradeService.trade(symbol, OrderSide.SELL, profit);
                }
                //亏损买入网格区间
                if (total_amount <= MathUtils.multiply(benchmark.doubleValue(), MathUtils.subtraction(1, lattice.doubleValue()))) {
                    double subtraction = MathUtils.subtraction(benchmark.doubleValue(), total_amount);
                    AssetBalance currencyBalance = client.getAccount().getAssetBalance(currency.name());
                    //预计算买入价
                    double real_quality = new BigDecimal(subtraction)
                            .divide(new BigDecimal(price), 6, RoundingMode.DOWN)
                            .doubleValue();
                    if (MathUtils.multiply(real_quality, price) > Double.parseDouble(currencyBalance.getFree())) {
                        //不断优化benchmark，防止持续下跌
                        benchmark.set(price);
                    } else {
                        digitalGoodsTradeService.trade(symbol, OrderSide.BUY, real_quality);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("Strategy Execute Failed!", e);
            }

        }, 0, 5, TimeUnit.SECONDS);
    }
}