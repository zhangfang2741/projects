/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2019 All Rights Reserved.
 */
package org.finance.quant.digital.utils;

import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.github.rholder.retry.WaitStrategies;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author hanqing.zf
 * @version : RetryUtils.java, v 0.1 2019年11月15日 17:52 tang Exp $
 */
public class RetryUtils {

    /**
     * sleep time
     */
    private static final int             SLEEP_TIME     = 10;
    /**
     * seconds
     */
    private static final int             MAX_CALL_TIME  = 1;
    /**
     * retry times
     */
    private static final int             ATTEMPT_NUMBER = 3;
    /**
     * retry caller
     */
    private static final Retryer<Object> RETRYER        = RetryerBuilder.newBuilder()
            .retryIfException()
            .withWaitStrategy(WaitStrategies.fixedWait(SLEEP_TIME, TimeUnit.MILLISECONDS))
            .withStopStrategy(StopStrategies.stopAfterAttempt(ATTEMPT_NUMBER))
            .build();

    /**
     * Retry.
     *
     * @param callable the callable
     * @throws Exception the exception
     */
    @SuppressWarnings("unchecked")
    public static <T> T retry(Callable<T> callable) throws Exception {
        return (T) RETRYER.call((Callable<Object>) callable);
    }

    /**
     * Retry.
     *
     * @param callable the callable
     * @param waitTime the wait time
     * @throws Exception the exception
     */
    public static <T> T retry(Callable<T> callable, long waitTime) throws Exception {
        return RetryerBuilder.<T>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(waitTime, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(ATTEMPT_NUMBER))
                .build().call(callable);
    }

    /**
     * Retry.
     *
     * @param callable   the callable
     * @param retryTimes the retry times
     * @throws Exception the exception
     */
    public static <T> T retry(Callable<T> callable, int retryTimes) throws Exception {
        return RetryerBuilder.<T>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(SLEEP_TIME, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes))
                .build().call(callable);
    }

    /**
     * Retry.
     *
     * @param callable   the callable
     * @param waitTime   the wait time
     * @param retryTimes the retry times
     * @throws Exception the exception
     */
    public static <T> T retry(Callable<T> callable, long waitTime, int retryTimes) throws Exception {
        return RetryerBuilder.<T>newBuilder()
                .retryIfException()
                .withWaitStrategy(WaitStrategies.fixedWait(waitTime, TimeUnit.MILLISECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(retryTimes))
                .build().call(callable);
    }
}