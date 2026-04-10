package com.nxquant.exchange.match.dto;

/**
 * @author shilf
 * OrderNode对象池，预分配避免GC
 * 基于数组+空闲栈，acquire O(1)，release O(1)
 */
public class OrderNodePool {
    private final OrderNode[] pool;
    private int top;

    public OrderNodePool(int capacity) {
        pool = new OrderNode[capacity];
        for (int i = 0; i < capacity; i++) {
            pool[i] = new OrderNode(null);
        }
        top = capacity;
    }

    public OrderNode acquire(Order order) {
        if (top > 0) {
            OrderNode node = pool[--top];
            node.order = order;
            return node;
        }
        // 池耗尽，降级为new（极端情况）
        return new OrderNode(order);
    }

    public void release(OrderNode node) {
        node.order = null;
        node.prev = null;
        node.next = null;
        if (top < pool.length) {
            pool[top++] = node;
        }
        // 超出池容量的节点丢弃，由GC回收
    }

    public int available() {
        return top;
    }

    public int capacity() {
        return pool.length;
    }
}
