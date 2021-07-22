package org.finance.quant.digital.executor;

import org.apache.log4j.Logger;
import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.strategy.FinanceTa4jSarStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FinanceStrategyExecutor extends Executor {
    private static final Logger LOGGER = Logger.getLogger(FinanceStrategyExecutor.class);
    @Autowired
    private FinanceTa4jSarStrategy financeTa4jSarStrategy;

    @Override
    public void execute() {
        financeTa4jSarStrategy.execute(AssetsEnum.USDT, AssetsEnum.BTC);
        LOGGER.info("FinanceTa4jSarStrategy Start!!!");
    }
}
