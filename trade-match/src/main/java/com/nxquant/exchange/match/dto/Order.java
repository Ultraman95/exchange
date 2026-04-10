package com.nxquant.exchange.match.dto;

import java.math.BigDecimal;

/**
 * @author shilf
 * 撮合订单类
 */
public class Order implements Info {

    private String instrumentId;
    private int instrumentIndex;

    private long orderId;

    private String orderLocalId;

    private long comPrice;

    private BigDecimal price;

    private long volume;

    private OrderPriceType priceType;

    private DirectionType direction;

    private OffsetType offset;

    private TimeConditionType timeCondition;

    private OrderType orderType;

    private OrderStatus orderStatus;

    private long tradedVolume;

    private long displayVolume;

    private OrderPurposeType purposeType;

    private String clientId;

    private Long minBoundPrice;

    private Long maxBoundPrice;

    private long inputTs;

    private long createTs;

    private long updateTs;

    private long incId;

    public Order(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        return this.orderId == ((Order) o).orderId;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(orderId);
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getIncId() {
        return incId;
    }

    public void setIncId(long incId) {
        this.incId = incId;
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public int getInstrumentIndex() {
        return instrumentIndex;
    }

    public void setInstrumentIndex(int instrumentIndex) {
        this.instrumentIndex = instrumentIndex;
    }

    public long getComPrice() {
        return comPrice;
    }

    public void setComPrice(long comPrice) {
        this.comPrice = comPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getCreateTs() {
        return createTs;
    }

    public void setCreateTs(long createTs) {
        this.createTs = createTs;
    }

    public long getVolume() {
        return volume;
    }

    public void setVolume(long volume) {
        this.volume = volume;
    }

    public OrderPriceType getPriceType() {
        return priceType;
    }

    public void setPriceType(OrderPriceType priceType) {
        this.priceType = priceType;
    }

    public DirectionType getDirection() {
        return direction;
    }

    public void setDirection(DirectionType direction) {
        this.direction = direction;
    }

    public OffsetType getOffset() {
        return offset;
    }

    public void setOffset(OffsetType offset) {
        this.offset = offset;
    }

    public TimeConditionType getTimeCondition() {
        return timeCondition;
    }

    public void setTimeCondition(TimeConditionType timeCondition) {
        this.timeCondition = timeCondition;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public long getInputTs() {
        return inputTs;
    }

    public void setInputTs(long inputTs) {
        this.inputTs = inputTs;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getTradedVolume() {
        return tradedVolume;
    }

    public void setTradedVolume(long tradedVolume) {
        this.tradedVolume = tradedVolume;
    }

    public long getDisplayVolume() {
        return displayVolume;
    }

    public void setDisplayVolume(long displayVolume) {
        this.displayVolume = displayVolume;
    }

    public OrderPurposeType getPurposeType() {
        return purposeType;
    }

    public void setPurposeType(OrderPurposeType purposeType) {
        this.purposeType = purposeType;
    }

    public String getOrderLocalId() {
        return orderLocalId;
    }

    public void setOrderLocalId(String orderLocalId) {
        this.orderLocalId = orderLocalId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public long getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(long updateTs) {
        this.updateTs = updateTs;
    }

    public Long getMinBoundPrice() {
        return minBoundPrice;
    }

    public void setMinBoundPrice(Long minBoundPrice) {
        this.minBoundPrice = minBoundPrice;
    }

    public Long getMaxBoundPrice() {
        return maxBoundPrice;
    }

    public void setMaxBoundPrice(Long maxBoundPrice) {
        this.maxBoundPrice = maxBoundPrice;
    }
}
