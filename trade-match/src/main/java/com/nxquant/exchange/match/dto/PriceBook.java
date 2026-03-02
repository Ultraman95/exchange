package com.nxquant.exchange.match.dto;

import java.util.Comparator;
import java.util.TreeSet;

/**
 * @author shilf
 * 某个价格订单簿
 */
public class PriceBook implements Info {
    private long price;
    private long reallyVolume;
    private long displayVolume;
    private TreeSet<Order> orderSet;

    public PriceBook(){
        reallyVolume = 0;
        displayVolume = 0;
        createOrderSet();
    }

    private void createOrderSet(){
        orderSet =new TreeSet<>(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                int cmp = Long.compare(o1.getCreateTs(), o2.getCreateTs());
                return cmp != 0 ? cmp : Long.compare(o1.getIncId(), o2.getIncId());
            }
        });
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

    public TreeSet<Order> getOrderSet() {
        return orderSet;
    }
}
