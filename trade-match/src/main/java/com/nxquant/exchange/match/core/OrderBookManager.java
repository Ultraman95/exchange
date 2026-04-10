package com.nxquant.exchange.match.core;

import com.nxquant.exchange.match.dto.*;
import org.agrona.collections.Long2ObjectHashMap;

import java.util.*;

/**
 * @author shilf
 * 订单簿管理器
 */
public class OrderBookManager {
    private static final int DEFAULT_POOL_CAPACITY = 1 << 20; // 100万
    private static final int MAX_INSTRUMENTS = 1024;

    private final ExOrderBook[] exOrderBooks = new ExOrderBook[MAX_INSTRUMENTS];
    private final Map<String, Integer> instrumentIndexMap = new HashMap<>();
    private int instrumentCount = 0;

    private Long2ObjectHashMap<OrderNode> cacheOrderMap = new Long2ObjectHashMap<>();
    private OrderNodePool nodePool;

    public OrderBookManager() {
        this(DEFAULT_POOL_CAPACITY);
    }

    public OrderBookManager(int poolCapacity) {
        this.nodePool = new OrderNodePool(poolCapacity);
    }

    int getOrCreateInstrumentIndex(String instrumentId) {
        Integer idx = instrumentIndexMap.get(instrumentId);
        if (idx != null) return idx;
        int newIdx = instrumentCount++;
        instrumentIndexMap.put(instrumentId, newIdx);
        return newIdx;
    }

    void init(List<ExOrderBook> exOrderBookList){
        if(exOrderBookList != null) {
            for (ExOrderBook exOrderBook : exOrderBookList) {
                int idx = getOrCreateInstrumentIndex(exOrderBook.getInstrumentId());
                exOrderBook.setInstrumentIndex(idx);
                exOrderBooks[idx] = exOrderBook;
                addExOrderBookToCacheOrderMap(exOrderBook);
            }
        }
    }

    private void addExOrderBookToCacheOrderMap(ExOrderBook exOrderBook){
        if(exOrderBook.getOrderBook() != null) {
            cacheOrdersFromSide(exOrderBook.getOrderBook().getBuyOrders());
            cacheOrdersFromSide(exOrderBook.getOrderBook().getSellOrders());
        }
    }

    private void cacheOrdersFromSide(TreeMap<Long, PriceBook> side){
        for(PriceBook priceBook : side.values()){
            OrderNode node = priceBook.getOrders().getHead();
            while(node != null){
                cacheOrderMap.put(node.getOrder().getOrderId(), node);
                node = node.next;
            }
        }
    }

    void addOrderToExOrderBookMap(Order order){
        int idx = order.getInstrumentIndex();
        ExOrderBook exOrderBook = exOrderBooks[idx];
        if(exOrderBook == null){
            exOrderBook = new ExOrderBook(order.getInstrumentId(), idx);
            exOrderBooks[idx] = exOrderBook;
        }
        OrderBook orderBook = exOrderBook.getOrderBook();
        if(orderBook == null){
            orderBook = new OrderBook();
            exOrderBook.setOrderBook(orderBook);
        }
        TreeMap<Long, PriceBook> relatedOrders;
        if(order.getDirection() == DirectionType.DT_BUY){
            relatedOrders = orderBook.getBuyOrders();
        }else {
            relatedOrders = orderBook.getSellOrders();
        }
        PriceBook priceBook = relatedOrders.get(order.getComPrice());
        if(priceBook == null){
            priceBook = new PriceBook();
            priceBook.setPrice(order.getComPrice());
            relatedOrders.put(order.getComPrice(), priceBook);
        }
        OrderNode node = nodePool.acquire(order);
        priceBook.getOrders().addLast(node);
        priceBook.setReallyVolume(priceBook.getReallyVolume() + order.getVolume() - order.getTradedVolume());
        cacheOrderMap.put(order.getOrderId(), node);
    }

    boolean cacheOrderMapContainsOrder(long orderId){
        return cacheOrderMap.containsKey(orderId);
    }

    Order getOrderFromCacheOrderMap(long orderId){
        OrderNode node = cacheOrderMap.get(orderId);
        return node != null ? node.getOrder() : null;
    }

    void removeOrderFromExOrderBookMap(Order order){
        OrderNode node = cacheOrderMap.remove(order.getOrderId());
        if(node == null) return;

        int idx = order.getInstrumentIndex();
        ExOrderBook exOrderBook = exOrderBooks[idx];
        if(exOrderBook == null || exOrderBook.getOrderBook() == null) return;

        TreeMap<Long, PriceBook> relatedOrders;
        OrderBook orderBook = exOrderBook.getOrderBook();
        if(order.getDirection() == DirectionType.DT_BUY){
            relatedOrders = orderBook.getBuyOrders();
        }else {
            relatedOrders = orderBook.getSellOrders();
        }
        PriceBook priceBook = relatedOrders.get(order.getComPrice());
        if(priceBook != null) {
            priceBook.getOrders().remove(node);
            if (priceBook.getOrders().isEmpty()) {
                relatedOrders.remove(priceBook.getPrice());
            }else {
                priceBook.setReallyVolume(priceBook.getReallyVolume() - (order.getVolume() - order.getTradedVolume()));
            }
        }
        nodePool.release(node);
    }

    ExOrderBook getExOrderBook(int instrumentIndex){
        return exOrderBooks[instrumentIndex];
    }

    TreeMap<Long, PriceBook> getPartyOrders(int instrumentIndex, DirectionType direction){
        ExOrderBook exOrderBook = exOrderBooks[instrumentIndex];
        if(exOrderBook == null || exOrderBook.getOrderBook() == null){
            return null;
        }
        if(direction == DirectionType.DT_BUY){
            return exOrderBook.getOrderBook().getSellOrders();
        }else {
            return exOrderBook.getOrderBook().getBuyOrders();
        }
    }

    void updatePriceBookVolume(Order order, long volumeDelta){
        ExOrderBook exOrderBook = exOrderBooks[order.getInstrumentIndex()];
        if(exOrderBook == null || exOrderBook.getOrderBook() == null) return;
        TreeMap<Long, PriceBook> relatedOrders;
        if(order.getDirection() == DirectionType.DT_BUY){
            relatedOrders = exOrderBook.getOrderBook().getBuyOrders();
        }else {
            relatedOrders = exOrderBook.getOrderBook().getSellOrders();
        }
        PriceBook priceBook = relatedOrders.get(order.getComPrice());
        if(priceBook != null){
            priceBook.setReallyVolume(priceBook.getReallyVolume() + volumeDelta);
        }
    }

}
