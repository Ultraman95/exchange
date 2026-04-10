package com.nxquant.exchange.match.dto;

public class CancelOrder implements Info {
    private long orderId;

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CancelOrder)) return false;
        return this.orderId == ((CancelOrder) o).orderId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(orderId);
    }
}
