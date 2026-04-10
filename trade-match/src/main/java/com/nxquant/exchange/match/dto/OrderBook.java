package com.nxquant.exchange.match.dto;

import java.util.Comparator;
import java.util.TreeMap;

/**
 * @author shilf
 * 订单簿
 */
public class OrderBook implements Info {
    private TreeMap<Long, PriceBook> buyOrders;
    private TreeMap<Long, PriceBook> sellOrders;

    public OrderBook(){
        buyOrders = new TreeMap<>(Comparator.reverseOrder());
        sellOrders = new TreeMap<>();
    }

    public TreeMap<Long, PriceBook> getBuyOrders() {
        return buyOrders;
    }

    public TreeMap<Long, PriceBook> getSellOrders() {
        return sellOrders;
    }
}
