package com.nxquant.exchange.match.dto;

/**
 * @author shilf
 * 订单双向链表节点，支持O(1)摘除
 */
public class OrderNode {
    public Order order;
    public OrderNode prev;
    public OrderNode next;

    public OrderNode(Order order) {
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
