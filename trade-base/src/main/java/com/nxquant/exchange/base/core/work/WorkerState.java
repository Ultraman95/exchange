package com.nxquant.exchange.base.core.work;

import org.apache.kafka.common.TopicPartition;

/**
 * 反演信息---同时也是初始化必要信息
 */
public class WorkerState {
    TopicPartition inputTp;

    SnapState snapState;

    IncState incState;
}
