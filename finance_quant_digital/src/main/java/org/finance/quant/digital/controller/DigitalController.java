/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2021 All Rights Reserved.
 */
package org.finance.quant.digital.controller;

import org.finance.quant.digital.enums.AssetsEnum;
import org.finance.quant.digital.strategy.FinanceMultipleStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hanqing.zf
 * @version : DigitalController.java, v 0.1 2021年06月14日 1:26 下午 hanqing.zf Exp $
 */
@RestController
// 表示该类返回json格式
@EnableAutoConfiguration
// 该类装配springboot内置tomcat中
public class DigitalController {
    @Autowired
    private FinanceMultipleStrategy multipleStrategy;

    @RequestMapping("/index")
    public String index() {
        return "Hello,quant digital!";
    }

    @RequestMapping("/start")
    public Boolean startStrategy() {
        multipleStrategy.execute(AssetsEnum.USDT, AssetsEnum.BTC);
        return Boolean.TRUE;
    }
}