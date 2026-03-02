package com.nxquant.exchange.base.entity;

import java.util.Comparator;

public class OrderBook {
    private String instrumentId;

    private UpdateAbleTreeSet<Order> buyOrders;
    private UpdateAbleTreeSet<Order> sellOrders;

    OrderBook(String instrumentId){
        this.instrumentId = instrumentId;
        createOrderBook();
    }

    private void createOrderBook(){
        buyOrders =new UpdateAbleTreeSet<>(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getComPrice() < o2.getComPrice()) {
                    return 1;
                } else if (o1.getComPrice() > o2.getComPrice()) {
                    return -1;
                } else {
                    int cmp = Long.compare(o1.getCreateTs(), o2.getCreateTs());
                    return cmp != 0 ? cmp : Long.compare(o1.getIncId(), o2.getIncId());
                }
            }
        });
        sellOrders =new UpdateAbleTreeSet<>(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                if (o1.getComPrice() < o2.getComPrice()) {
                    return -1;
                } else if (o1.getComPrice() > o2.getComPrice()) {
                    return 1;
                } else {
                    int cmp = Long.compare(o1.getCreateTs(), o2.getCreateTs());
                    return cmp != 0 ? cmp : Long.compare(o1.getIncId(), o2.getIncId());
                }
            }
        });
    }

    public UpdateAbleTreeSet<Order> getBuyOrders() {
        return buyOrders;
    }

    public UpdateAbleTreeSet<Order> getSellOrders() {
        return sellOrders;
    }
}
