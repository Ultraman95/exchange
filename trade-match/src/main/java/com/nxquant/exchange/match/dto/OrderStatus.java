package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * OrderStatus
 */
public enum OrderStatus implements Serializable {
    //新建订单
    OS_NEW("new", 0),
    //部分成交
    OS_PARTFILLED("partFilled", 1),
    //部分成交已撤单
    OS_PARTFILLEDCANCELED("partFilledCanceled", 2),
    //全部撤单
    OS_CANCELED("canceled", 3),
    //全部成交
    OS_FILLED("filled", 4),
    //拒绝
    OS_REJECTED("rejected", 5);

    private String key;
    private int value;


    OrderStatus(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public int getValue() {
        return value;
    }
}
