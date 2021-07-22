package org.finance.quant.digital.strategy;

import com.alibaba.fastjson.JSON;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.OrderSide;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.apache.log4j.Logger;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.ParabolicSarIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.rules.AndRule;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class FinanceTa4jSarStrategy extends FinanceBaseStrategy {
    private static final Logger LOGGER = Logger.getLogger(FinanceTa4jSarStrategy.class);

    @Autowired
    private DigitalGoodsTradeService digitalGoodsTradeService;


    @Override
    public void execute(AssetsEnum currency, AssetsEnum goods) {
        BinanceApiRestClient client = digitalGoodsTradeService.getClient();
        String symbol = goods.name() + currency.name();
        while (true) {
            try {
                Long currentTime = new Date().getTime();
                //2天前：-2天*24小时*60分钟*60秒*1000毫秒
                Long startTime = currentTime - 2 * 24 * 60 * 60 * 1000L;
                List<Candlestick> candlesticks = client.getCandlestickBars(symbol, CandlestickInterval.HOURLY, Integer.MAX_VALUE, startTime, currentTime);
                //构建BarSeries
                BarSeries series = buildBarSeries(candlesticks);
                //构建Strategy
                Strategy strategy = buildStrategy(series);
                Candlestick candlestick = candlesticks.get(series.getEndIndex());
                LOGGER.info("[" + DateUtils.format(new Date(candlestick.getOpenTime())) + "]开始处理candlestick:" + JSON.toJSONString(candlestick));
                if (strategy.shouldEnter(series.getEndIndex())) {
                    //满足买入条件
                    buy(candlestick, currency, goods);
                } else if (strategy.shouldExit(series.getEndIndex())) {
                    //满足卖出条件
                    sell(candlestick, currency, goods);
                }
                Thread.sleep(10 * 1000l);
            } catch (Exception e) {
                LOGGER.error("FinanceTa4jSarStrategy execute exception!", e);
            }
        }
    }

    /**
     * 创建策略
     *
     * @param series
     * @return
     */
    private Strategy buildStrategy(BarSeries series) {
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        //买入策略
        SMAIndicator sma5 = new SMAIndicator(closePriceIndicator, 5);
        SMAIndicator sma60 = new SMAIndicator(closePriceIndicator, 60);
        ParabolicSarIndicator parabolicSarIndicator = new ParabolicSarIndicator(series);
        CrossedUpIndicatorRule sarCrossClosePrice = new CrossedUpIndicatorRule(parabolicSarIndicator, closePriceIndicator);
        CrossedUpIndicatorRule sma5CrossSma60 = new CrossedUpIndicatorRule(sma5, sma60);
        Rule buyingRule = new AndRule(sarCrossClosePrice, sma5CrossSma60);
        //卖出策略
        CrossedDownIndicatorRule sarDownClosePrice = new CrossedDownIndicatorRule(parabolicSarIndicator, closePriceIndicator);
        CrossedDownIndicatorRule sma5DownSma60 = new CrossedDownIndicatorRule(sma5, sma60);
        Rule sellingRule = new AndRule(sarDownClosePrice, sma5DownSma60);
        //构建策略
        BaseStrategy strategy = new BaseStrategy(buyingRule, sellingRule);
        return strategy;
    }

    /**
     * 构建Series
     *
     * @param candlesticks
     * @return
     */
    private BarSeries buildBarSeries(List<Candlestick> candlesticks) {
        BarSeries series = new BaseBarSeriesBuilder().withName("QUANT_DIGITAL").build();
        candlesticks.stream().forEach(candlestick -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(candlestick.getOpenTime()));
            ZonedDateTime zonedDateTime = ZonedDateTime.of(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 0,
                    ZoneId.of(DateUtils.TIME_ZONE));
            series.addBar(zonedDateTime, candlestick.getOpen(), candlestick.getHigh(), candlestick.getLow(), candlestick.getClose(), candlestick.getVolume());
        });
        return series;
    }

    /**
     * buy
     *
     * @param candlestick
     * @param currency
     * @param goods
     */
    public void buy(Candlestick candlestick, AssetsEnum currency, AssetsEnum goods) {
        double free = digitalGoodsTradeService.assetBanlance(currency.name(), 6);
        double price = new BigDecimal(candlestick.getClose()).setScale(6, RoundingMode.UP).doubleValue();
        //计算最大买入量
        double quantity = new BigDecimal(free / price).setScale(6, RoundingMode.DOWN)
                .doubleValue();
        String symbol = goods.name() + currency.name();
        digitalGoodsTradeService.trade(symbol, OrderSide.BUY, quantity);
    }

    /**
     * sell
     *
     * @param candlestick
     * @param currency
     * @param goods
     */
    public void sell(Candlestick candlestick, AssetsEnum currency, AssetsEnum goods) {
        double free = digitalGoodsTradeService.assetBanlance(goods.name(), 6);
        String symbol = goods.name() + currency.name();
        digitalGoodsTradeService.trade(symbol, OrderSide.SELL, free);
    }

}
