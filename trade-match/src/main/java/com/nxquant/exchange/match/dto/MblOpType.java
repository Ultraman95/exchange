package com.nxquant.exchange.match.dto;

import java.io.Serializable;

/**
 * @author shilf
 * Mbl行情操作符
 */
public enum MblOpType implements Serializable {
    //增加
    MOT_ADD("add", 0),
    //删除
    MOT_REMOVE("remove", 1),
    //更新
    MOT_UPDATE("update", 2);

    private String key;
    private int value;


    MblOpType(String key, int value) {
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
