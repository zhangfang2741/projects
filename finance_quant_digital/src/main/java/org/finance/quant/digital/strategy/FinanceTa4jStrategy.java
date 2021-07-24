package org.finance.quant.digital.strategy;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.service.DigitalGoodsTradeService;
import org.finance.quant.digital.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.ta4j.core.*;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DoubleNum;
import org.ta4j.core.num.Num;
import org.ta4j.core.trading.rules.CrossedDownIndicatorRule;
import org.ta4j.core.trading.rules.CrossedUpIndicatorRule;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class FinanceTa4jStrategy extends FinanceBaseStrategy {
    @Autowired
    private DigitalGoodsTradeService digitalGoodsTradeService;

    private Strategy buildStrategy(BarSeries series) {
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        SMAIndicator sma5 = new SMAIndicator(closePrice, 5);
        SMAIndicator sma30 = new SMAIndicator(closePrice, 30);
        Rule buyingRule = new CrossedUpIndicatorRule(sma5, sma30);
        Rule sellingRule = new CrossedDownIndicatorRule(sma5, sma30);
        BaseStrategy strategy = new BaseStrategy(buyingRule, sellingRule);
        return strategy;
    }

    @Override
    public void execute(AssetsEnum currency, AssetsEnum goods) {
        BinanceApiRestClient client = digitalGoodsTradeService.getClient();
        String symbol = goods.name() + currency.name();
        List<Candlestick> candlesticks = client.getCandlestickBars(symbol, CandlestickInterval.DAILY, Integer.MAX_VALUE,
                DateUtils.getDay(-366), DateUtils.getDay(-1));
        //candlesticks.sort(Comparator.comparing(Candlestick::getOpenTime).reversed());

        // Initializing the trading history
        TradingRecord tradingRecord = new BaseTradingRecord();
        System.out.println("************************************************************");

        BarSeries series = new BaseBarSeriesBuilder().withName("QUANT_DIGITAL").build();
        candlesticks.stream().forEach(candlestick -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date(candlestick.getOpenTime()));
            ZonedDateTime zonedDateTime = ZonedDateTime.of(
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND), 0,
                    ZoneId.of("UTC+08:00"));
            series.addBar(zonedDateTime, candlestick.getOpen(), candlestick.getHigh(), candlestick.getLow(), candlestick.getClose(), candlestick.getVolume());
            //TradingRecord tradingRecord = new BarSeriesManager(series).run(buildStrategy(series));
            Strategy strategy = buildStrategy(series);
            int endIndex = series.getEndIndex();
            if (strategy.shouldEnter(endIndex)) {
                // Our strategy should enter
                System.out.println("Strategy should ENTER on " + endIndex);
                boolean entered = tradingRecord.enter(endIndex, series.getLastBar().getClosePrice(), DoubleNum.valueOf(10));
                if (entered) {
                    Order entry = tradingRecord.getLastEntry();
                    System.out.println("Entered on " + entry.getIndex() + " (price=" + entry.getNetPrice().doubleValue()
                            + ", amount=" + entry.getAmount().doubleValue() + ")");
                }
            } else if (strategy.shouldExit(endIndex)) {
                // Our strategy should exit
                System.out.println("Strategy should EXIT on " + endIndex);
                boolean exited = tradingRecord.exit(endIndex, series.getLastBar().getClosePrice(), DoubleNum.valueOf(10));
                if (exited) {
                    Order exit = tradingRecord.getLastExit();
                    System.out.println("Exited on " + exit.getIndex() + " (price=" + exit.getNetPrice().doubleValue()
                            + ", amount=" + exit.getAmount().doubleValue() + ")");
                }
            }
        });
    }
}
