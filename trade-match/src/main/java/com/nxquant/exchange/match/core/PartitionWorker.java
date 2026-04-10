package com.nxquant.exchange.match.core;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.nxquant.exchange.match.configure.WorkContext;
import com.nxquant.exchange.match.dto.*;
import com.nxquant.exchange.match.dto.Order;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;
import java.util.concurrent.ThreadFactory;

/**
 * @author shilf
 * kafka-partition消费线程
 */
public class PartitionWorker implements EventHandler<InputEventData> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RedoOffset redoOffset;
    private Disruptor<InputEventData> disruptor;
    private boolean disruptorStartStatus = false;

    private WorkContext workContext;
    private int receiveSize = 0;
    private KafkaProducer sendProducer;

    PartitionWorker(RedoOffset redoOffset, List<ExOrderBook> exOrderBookList, WorkContext workContext){
        this.redoOffset = redoOffset;
        this.workContext = workContext;
        if (!exOrderBookList.isEmpty()) {
            this.workContext.getMatchService().initOrderBookManager(exOrderBookList);
        }
        initDisruptor();
    }

    private void initDisruptor(){
        int ringBufferSize = 1 << 15;
        TopicPartition tp = this.redoOffset.getTopicPartition();
        String namePrefix = tp.topic() + "_" + tp.partition();
        ThreadFactory threadFactory = r -> {
            Thread t = new Thread(r, namePrefix);
            t.setDaemon(true);
            return t;
        };
        this.disruptor = new Disruptor<>(InputEventData::new, ringBufferSize, threadFactory);
        this.disruptor.handleEventsWith(this);
    }

    void start(){
        this.disruptor.start();
    }

    void stop(){
        if (disruptorStartStatus) {
            this.disruptor.shutdown();
        }
    }


    @Override
    public void onEvent(InputEventData eData, long sequence, boolean endOfBatch) {
        Info content = eData.getContent();
        boolean redo  = isReDo(eData.getOffset());
        if(content instanceof Order){
            Order order = (Order) content;
            this.workContext.getMatchService().insertOrder(order, redo);
        }else if(content instanceof CancelOrder){
            CancelOrder cancelOrder = (CancelOrder) content;
            this.workContext.getMatchService().cancelOrder(cancelOrder, redo);
        }else if(content instanceof AmendOrder){
            AmendOrder amendOrder = (AmendOrder) content;
            this.workContext.getMatchService().amendOrder(amendOrder, redo);
        }
        sendMsg(endOfBatch);
    }

    void publishDisruptor(ConsumerRecord<String, Info> inputData) {
        long sequence = 0;
        try {
            sequence = this.disruptor.getRingBuffer().next();
            InputEventData eData = this.disruptor.getRingBuffer().get(sequence);
            eData.setData(inputData);
        } catch (Exception exp) {
            logger.error("Match_ERROR: publishDisruptor failure !", exp);
        } finally {
            this.disruptor.getRingBuffer().publish(sequence);
        }
    }

    private void sendMsg(boolean endOfBatch){
        try {
            receiveSize++;
            boolean isFitReceivedSize = receiveSize > this.workContext.getCommitReceiveSize();
            boolean isFitRtnSize = this.workContext.getMatchService().getRtnInfoListSize() > this.workContext.getCommitRtnSize();
            //endOfBatch--后面没有订单了，直接写回报
            //isFitReceivedSize--累计收到报单次数
            //isFitRtnSize--累计成交回报次数
            if (isFitReceivedSize || isFitRtnSize || endOfBatch) {
                //向kafka写回报
                //this.sendProducer.beginTransaction();
                //ToDo--send Msg
                //this.sendProducer.commitTransaction();
                clearScene();
            }
        }catch (Exception exp){
            logger.error("Match_ERROR: sendMsg Failure !", exp);
        }
    }

    private void clearScene(){
        this.workContext.getMatchService().clearRtnInfoList();
        receiveSize = 0;
        //此时除了订单簿是新的状态，其余应该是空状态，等待输入
        logger.info("Match_INFO: clearScene !");
    }

    private boolean isReDo(long offset){
        if(offset < redoOffset.getMaxOffset()){
            return true;
        }
        return false;
    }

    @Override
    public void onStart() {
        disruptorStartStatus = true;
    }

    @Override
    public void onShutdown() {
        disruptorStartStatus = false;
    }
}
