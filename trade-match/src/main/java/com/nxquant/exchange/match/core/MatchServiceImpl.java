package com.nxquant.exchange.match.core;

import com.nxquant.exchange.match.dto.*;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author shilf
 * 撮合服务接口实现
 */
@Service
public class MatchServiceImpl implements MatchService {
    private static final int MAX_MATCH_DEPTH = 1024;

    private OrderBookManager orderBookManager = new OrderBookManager();
    private List<IRtnInfo> rtnInfoList = new ArrayList<>();

    // 预分配MatchInfo数组，避免每次撮合new
    private final MatchInfo[] matchInfoPool = new MatchInfo[MAX_MATCH_DEPTH];
    private int matchCount;

    {
        for (int i = 0; i < MAX_MATCH_DEPTH; i++) {
            matchInfoPool[i] = new MatchInfo();
        }
    }

    @Override
    public void initOrderBookManager(List<ExOrderBook> exOrderBookList){
        orderBookManager.init(exOrderBookList);
    }

    @Override
    public int getRtnInfoListSize(){
        return  rtnInfoList.size();
    }

    @Override
    public void clearRtnInfoList(){
        rtnInfoList.clear();
    }

    @Override
    public void insertOrder(Order order, boolean isRedo){
        TreeMap<Long, PriceBook> partyOrders = orderBookManager.getPartyOrders(order.getInstrumentIndex(), order.getDirection());
        if(partyOrders == null || partyOrders.isEmpty()){
            IRtnInfo rtnInfo = null;
            if(order.getPriceType() == OrderPriceType.OPT_LIMIT){
                if(order.getTimeCondition() == TimeConditionType.TCT_GTC){
                    orderBookManager.addOrderToExOrderBookMap(order);

                    rtnInfo = new RtnOrder();
                }else if(order.getTimeCondition() == TimeConditionType.TCT_IOC){
                    //打回
                }else if(order.getTimeCondition() == TimeConditionType.TCT_FOK){
                    //打回
                }
            }else if(order.getPriceType() == OrderPriceType.OPT_MARKET){
                //打回
            }
            if(rtnInfo != null && !isRedo){
                addRtnInfo(rtnInfo);
            }
        }else {
            long orderRemainVolume = order.getVolume();
            matchCount = 0;
            boolean matchDone = false;
            Map.Entry<Long, PriceBook> entry = partyOrders.firstEntry();
            while (entry != null && !matchDone) {
                OrderNode node = entry.getValue().getOrders().getHead();
                while (node != null) {
                    Order partyOrder = node.order;
                    if (!isMatch(order, partyOrder)) {
                        matchDone = true;
                        break;
                    }
                    orderRemainVolume -= (partyOrder.getVolume() - partyOrder.getTradedVolume());
                    MatchInfo matchInfo = matchInfoPool[matchCount++];
                    matchInfo.setMatchOrder(partyOrder);
                    if (orderRemainVolume <= 0) {
                        matchInfo.setRemainVolume(-orderRemainVolume);
                        orderRemainVolume = 0;
                        matchDone = true;
                        break;
                    }
                    matchInfo.setRemainVolume(0);
                    node = node.next;
                }
                entry = partyOrders.higherEntry(entry.getKey());
            }

            if (order.getPriceType() == OrderPriceType.OPT_LIMIT && order.getTimeCondition() == TimeConditionType.TCT_FOK && orderRemainVolume > 0 ) {
                //FOK没有全部成交，撤单
                if(!isRedo){

                }
                return;
            }

            if(order.getPurposeType() == OrderPurposeType.OPT_POSTONLY && order.getVolume() != orderRemainVolume ){
                //被动委托，撤单
                if(!isRedo){

                }
                return;
            }


            if (orderRemainVolume == order.getVolume()) {
                IRtnInfo rtnInfo = null;
                if (order.getPriceType() == OrderPriceType.OPT_LIMIT) {
                    //没有匹配，插入订单簿
                    orderBookManager.addOrderToExOrderBookMap(order);

                    rtnInfo = new RtnOrder();
                } else if (order.getPriceType() == OrderPriceType.OPT_MARKET) {
                    //没有匹配
                    rtnInfo = new RtnOrder();
                }
                if(rtnInfo != null && !isRedo){
                    addRtnInfo(rtnInfo);
                }
            }

            for (int i = 0; i < matchCount; i++) {
                MatchInfo matchInfo = matchInfoPool[i];
                Order matchOrder = matchInfo.getMatchOrder();
                if (matchInfo.getRemainVolume() > 0) {
                    long filledVolume = matchOrder.getVolume() - matchOrder.getTradedVolume() - matchInfo.getRemainVolume();
                    matchOrder.setTradedVolume(matchOrder.getVolume() - matchInfo.getRemainVolume());
                    matchOrder.setOrderStatus(OrderStatus.OS_PARTFILLED);
                    orderBookManager.updatePriceBookVolume(matchOrder, -filledVolume);
                } else {
                    orderBookManager.removeOrderFromExOrderBookMap(matchOrder);
        
                    matchOrder.setOrderStatus(OrderStatus.OS_FILLED);
                }
                if(!isRedo) {
                    RtnTrade partyRtnTrade = new RtnTrade();
                    addRtnInfo(partyRtnTrade);
                }
            }

            order.setTradedVolume(order.getVolume() - orderRemainVolume);
            if (orderRemainVolume == 0) {
                //全部成交
                order.setOrderStatus(OrderStatus.OS_FILLED);
            } else {
                if (order.getTimeCondition() == TimeConditionType.TCT_GTC) {
                    if (order.getPriceType() == OrderPriceType.OPT_LIMIT) {
                        //部分成交，其余插入订单簿
                        order.setOrderStatus(OrderStatus.OS_PARTFILLED);
                        orderBookManager.addOrderToExOrderBookMap(order);
    
                    }
                } else if (order.getTimeCondition() == TimeConditionType.TCT_IOC) {
                    //部分成交，其余撤单
                    order.setOrderStatus(OrderStatus.OS_PARTFILLEDCANCELED);
                }
            }
            if(!isRedo) {
                RtnTrade rtnTrade = new RtnTrade();
                addRtnInfo(rtnTrade);
            }
        }
    }


    @Override
    public void cancelOrder(CancelOrder cancelOrder, boolean isRedo){
        long orderId = cancelOrder.getOrderId();
        if(orderBookManager.cacheOrderMapContainsOrder(orderId)){
            Order order = orderBookManager.getOrderFromCacheOrderMap(orderId);
            orderBookManager.removeOrderFromExOrderBookMap(order);

        }else{
            //订单不存在
        }
    }


    @Override
    public void amendOrder(AmendOrder updateOrder, boolean isRedo){
        long orderId = updateOrder.getOrderId();
        if(orderBookManager.cacheOrderMapContainsOrder(orderId)){
            Order order = orderBookManager.getOrderFromCacheOrderMap(orderId);
            boolean priceChanged = updateOrder.getNewComPrice() != null
                    && updateOrder.getNewComPrice() != order.getComPrice();
            if (priceChanged) {
                orderBookManager.removeOrderFromExOrderBookMap(order);
                order.setComPrice(updateOrder.getNewComPrice());
                if (updateOrder.getNewVolume() != null) {
                    order.setVolume(updateOrder.getNewVolume());
                }
                insertOrder(order, isRedo);
            } else if (updateOrder.getNewVolume() != null) {
                long oldRemain = order.getVolume() - order.getTradedVolume();
                order.setVolume(updateOrder.getNewVolume());
                long newRemain = order.getVolume() - order.getTradedVolume();
                orderBookManager.updatePriceBookVolume(order, newRemain - oldRemain);
            }
        }else{
            //订单不存在
        }
    }


    private void addRtnMblData(Order order, OrderBookManager orderBookManager){
        long mblVolume = Math.min(order.getVolume() - order.getTradedVolume() , order.getDisplayVolume());
        RtnMblData rtnMblData = new RtnMblData();
        rtnMblData.setVolume(mblVolume);
        addRtnInfo(rtnMblData);
    }

    //获取成交价格
    private BigDecimal getTradePrice(TradePriceType tradePriceType){
        if(tradePriceType == null){
            tradePriceType = TradePriceType.TCT_OPPONENT;
        }
        return null;
    }

    //是否能够匹配
    private boolean isMatch(Order order, Order partyOrder){
        if(order.getPriceType() == OrderPriceType.OPT_LIMIT) {
            if (order.getDirection() == DirectionType.DT_BUY) {
                if (order.getComPrice() < partyOrder.getComPrice()) {
                    return false;
                }
                return true;
            } else {
                if (order.getComPrice() > partyOrder.getComPrice()) {
                    return false;
                }
                return true;
            }
        }else{
            if(order.getMinBoundPrice() != null){
                if(partyOrder.getComPrice() < order.getMinBoundPrice()){
                    return false;
                }
            }
            if(order.getMaxBoundPrice() != null){
                if(partyOrder.getComPrice() > order.getMaxBoundPrice()){
                    return false;
                }
            }
            return true;
        }
    }

    void addRtnInfo(IRtnInfo rtnInfo){
        rtnInfoList.add(rtnInfo);
    }
}
