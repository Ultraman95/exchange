package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * 报单类型
 */
public enum OrderType implements Serializable {
    //报单
    OT_INSERT("insert", 0),
    //撤单
    OT_CANCEL("cancel", 1),
    //改单
    OT_UPDATE("update", 2);


    private String key;
    private int value;


    OrderType(String key, int value) {
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
