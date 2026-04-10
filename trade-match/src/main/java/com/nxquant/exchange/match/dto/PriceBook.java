package com.nxquant.exchange.match.dto;

/**
 * @author shilf
 * 某个价格订单簿
 */
public class PriceBook implements Info {
    private long price;
    private long reallyVolume;
    private long displayVolume;
    private OrderLinkedList orders;

    public PriceBook(){
        reallyVolume = 0;
        displayVolume = 0;
        orders = new OrderLinkedList();
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getReallyVolume() {
        return reallyVolume;
    }

    public void setReallyVolume(long reallyVolume) {
        this.reallyVolume = reallyVolume;
    }

    public long getDisplayVolume() {
        return displayVolume;
    }

    public void setDisplayVolume(long displayVolume) {
        this.displayVolume = displayVolume;
    }

    public OrderLinkedList getOrders() {
        return orders;
    }
}
