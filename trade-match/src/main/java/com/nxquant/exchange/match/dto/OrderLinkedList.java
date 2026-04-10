package com.nxquant.exchange.match.dto;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author shilf
 * 订单双向链表，尾部追加O(1)，按节点删除O(1)
 */
public class OrderLinkedList implements Iterable<Order> {
    private OrderNode head;
    private OrderNode tail;
    private int size;

    public void addLast(OrderNode node) {
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    public void remove(OrderNode node) {
        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }
        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
        node.prev = null;
        node.next = null;
        size--;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public OrderNode getHead() {
        return head;
    }

    @Override
    public Iterator<Order> iterator() {
        return new Iterator<>() {
            private OrderNode current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Order next() {
                if (current == null) throw new NoSuchElementException();
                Order order = current.order;
                current = current.next;
                return order;
            }
        };
    }
}
