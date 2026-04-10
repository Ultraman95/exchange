package com.nxquant.exchange.match.dto;

public class AmendOrder implements Info {
    private long orderId;
    private Long newComPrice;
    private Long newVolume;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Long getNewComPrice() {
        return newComPrice;
    }

    public void setNewComPrice(Long newComPrice) {
        this.newComPrice = newComPrice;
    }

    public Long getNewVolume() {
        return newVolume;
    }

    public void setNewVolume(Long newVolume) {
        this.newVolume = newVolume;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AmendOrder)) return false;
        return this.orderId == ((AmendOrder) o).orderId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(orderId);
    }
}
