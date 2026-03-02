package com.nxquant.exchange.match.core;

import com.nxquant.exchange.match.configure.WorkContext;
import com.nxquant.exchange.match.dto.Info;
import com.nxquant.exchange.match.utils.ToolUtils;
import com.nxquant.exchange.match.dto.ExOrderBook;
import com.nxquant.exchange.match.dto.RedoOffset;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * @author shilf
 * Match主线程
 */
@Component
public class MainWorker implements ConsumerRebalanceListener{
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private boolean isStop = false;

    private ConcurrentMap<String, PartitionWorker> kpWorkMap = new ConcurrentHashMap<>();
    private KafkaConsumer<String, Info> inputConsumer;
    private KafkaConsumer<String, Info> snapConsumer;
    private KafkaConsumer<String, Info> incConsumer;


    private Map<TopicPartition, RedoOffset> redoOffsetMap = new HashMap<>();
    private Map<TopicPartition, List<ExOrderBook>> baseExOrderBookMap = new HashMap<>();

    public WorkContext getWorkContext() {
        return workContext;
    }

    @Autowired
    WorkContext workContext;

    private boolean isSupportKafka = false;

    public void start(){
        if (isSupportKafka) {
            if (initRedoOffset()) {
                //验证反演区间
                for (Map.Entry<TopicPartition, RedoOffset> entry : redoOffsetMap.entrySet()) {
                    RedoOffset redoOffset = entry.getValue();
                    if (redoOffset.getMaxOffset() == redoOffset.getMinOffset()) {
                        logger.info("Match_INFO: no need redo !" + ToolUtils.getGson().toJson(redoOffset));
                    } else if (redoOffset.getMaxOffset() < redoOffset.getMinOffset()) {
                        logger.error("Match_ERROR: redoOffsetMap failure !" + ToolUtils.getGson().toJson(redoOffsetMap));
                        System.exit(-1);
                    }
                }
            } else {
                logger.error("Match_ERROR: initRedoOffset Failure !");
                System.exit(-1);
            }
            consumeInput();
        }
    }


    private void consumeInput() {
        inputConsumer = new KafkaConsumer<>(workContext.getInputConsumerProp());
        inputConsumer.subscribe(Collections.singleton(workContext.getInputTopic()), this);
        while(!isStop){
            try {
                ConsumerRecords<String, Info> inputDataList = inputConsumer.poll(workContext.getPollTimeout());
                if(!inputDataList.isEmpty()){
                    for (ConsumerRecord<String, Info> inputData : inputDataList) {
                        String workerKey = inputData.topic() + "_" + inputData.partition();
                        PartitionWorker kpWorker = kpWorkMap.get(workerKey);
                        if (kpWorker == null) {
                            logger.error("Match_ERROR: consumeInput , kpWorker is null !");
                            System.exit(-1);
                        }
                        kpWorker.publishDisruptor(inputData);
                    }
                }
            }catch (Exception exp){
                logger.error("Match_ERROR: MainWork consumeInput failure !", exp);
                inputConsumer.close();
                System.exit(-1);
            }
        }
    }


    /**
     * 初始化反演信息
     * @return
     */
    private boolean initRedoOffset(){
        try {
            //获取反演全量
            snapConsumer = new KafkaConsumer<>(workContext.getSnapConsumerProp());
            List<PartitionInfo> snapPtInfoList = snapConsumer.partitionsFor(workContext.getSnapTopic());
            if (!snapPtInfoList.isEmpty()) {
                List<TopicPartition> snapTpList = new ArrayList<>();
                for (PartitionInfo pInfo : snapPtInfoList) {
                    TopicPartition snapTp = new TopicPartition(pInfo.topic(), pInfo.partition());
                    snapTpList.add(snapTp);
                }
                Map<TopicPartition, Long> snapEndOffsetMap = snapConsumer.endOffsets(snapTpList);
                for (Map.Entry<TopicPartition, Long> entry : snapEndOffsetMap.entrySet()) {
                    TopicPartition inputTp = new TopicPartition(workContext.getInputTopic(), entry.getKey().partition());
                    long endOffset = entry.getValue();
                    long minInputOffset = 0;
                    if (endOffset != 0) {
                        long seekOffset = 0;
                        if (endOffset >= workContext.getTransActionOffset()) {
                            seekOffset = endOffset - workContext.getTransActionOffset();
                        }
                        snapConsumer.assign(Collections.singleton(entry.getKey()));
                        snapConsumer.seek(entry.getKey(), seekOffset);

                        boolean isSnapReceive = false;
                        int tryCount = 0;
                        while (!isSnapReceive){
                            tryCount++;
                            if(tryCount > workContext.getTryTimes()){
                                logger.info("Match_INFO: snapConsumer Try Time Out, but It's ok !");
                                break;
                            }
                            ConsumerRecords<String, Info> snapDataList = snapConsumer.poll(workContext.getPollTimeout());
                            if(!snapDataList.isEmpty()){
                                isSnapReceive = true;
                                //ToDo--shilf
                                //初始化订单簿--baseExOrderBookMap

                                minInputOffset = 0;
                            }
                        }
                    }
                    RedoOffset redoOffset = new RedoOffset(inputTp, minInputOffset, 0);
                    redoOffsetMap.put(inputTp, redoOffset);
                }
            } else {
                logger.error("Match_INFO: snapConsumer partitions is empty !");
                return false;
            }

            //获取反演增量
            incConsumer = new KafkaConsumer<>(workContext.getIncConsumerProp());
            List<PartitionInfo> incPtInfoList = incConsumer.partitionsFor(workContext.getIncTopic());
            if (!incPtInfoList.isEmpty()) {
                List<TopicPartition> incTpList = new ArrayList<>();
                for (PartitionInfo pInfo : incPtInfoList) {
                    TopicPartition incTp = new TopicPartition(pInfo.topic(), pInfo.partition());
                    incTpList.add(incTp);
                }
                Map<TopicPartition, Long> incEndOffsetMap = incConsumer.endOffsets(incTpList);
                for (Map.Entry<TopicPartition, Long> entry : incEndOffsetMap.entrySet()) {
                    TopicPartition inputTp = new TopicPartition(workContext.getInputTopic(), entry.getKey().partition());
                    long endOffset = entry.getValue();
                    long inputConsumedOffset = 0;
                    if (endOffset != 0) {
                        long seekOffset = 0;
                        if (endOffset >= workContext.getTransActionOffset()) {
                            seekOffset = endOffset - workContext.getTransActionOffset();
                        }
                        incConsumer.assign(Collections.singleton(entry.getKey()));
                        incConsumer.seek(entry.getKey(), seekOffset);

                        boolean isIncReceive = false;
                        int tryCount = 0;
                        while (!isIncReceive) {
                            tryCount++;
                            if (tryCount > workContext.getTryTimes()) {
                                logger.info("Match_INFO: snapConsumer Try Time Out, but It's ok !");
                                break;
                            }
                            ConsumerRecords<String, Info> incDataList = incConsumer.poll(workContext.getPollTimeout());
                            if(!incDataList.isEmpty()){
                                isIncReceive = true;
                                //ToDo--shilf

                                inputConsumedOffset = 0;
                            }
                        }
                    }

                    if (redoOffsetMap.containsKey(inputTp)) {
                        RedoOffset redoOffset = redoOffsetMap.get(inputTp);
                        redoOffset.setMaxOffset(inputConsumedOffset);
                    } else {
                        logger.info("Match_INFO: initRedoOffset--redoOffsetMap noContains , " + ToolUtils.getGson().toJson(inputTp));
                    }
                }
            } else {
                logger.error("Match_INFO: incConsumer partitions is empty !");
                return false;
            }
            return true;
        }catch (Exception exp){
            logger.error("Match_ERROR: initRedoOffset--", exp);
            return false;
        }finally {
            if(snapConsumer != null){
                snapConsumer.close();
            }
            if(incConsumer != null){
                incConsumer.close();
            }
        }
    }


    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        //ToDo--kafka动态切换处理
    }

    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        if(!partitions.isEmpty()){
            for(TopicPartition inputTp : partitions){
                if(redoOffsetMap.containsKey(inputTp)){
                    addKafkaPartitionWorK(inputTp);
                }else{
                    logger.info("Match_INFO: onPartitionsAssigned--redoOffsetMap noContains , " + ToolUtils.getGson().toJson(inputTp));
                }
            }
        }
    }

    private void addKafkaPartitionWorK(TopicPartition inputTp){
        PartitionWorker kpWorker;
        String workerKey = inputTp.topic() + "_" + inputTp.partition();
        if (!kpWorkMap.containsKey(workerKey)) {
            RedoOffset redoOffset = redoOffsetMap.get(inputTp);
            List<ExOrderBook> exOrderBookList = baseExOrderBookMap.getOrDefault(inputTp, Collections.emptyList());
            kpWorker = new PartitionWorker(redoOffset, exOrderBookList, workContext);
            kpWorkMap.put(workerKey, kpWorker);
            kpWorker.start();
            inputConsumer.seek(inputTp, redoOffset.getMinOffset());
        }
    }

    public void setMainWorkerStop(boolean stop) {
        isStop = stop;
        if (stop) {
            shutdown();
        }
    }

    private void shutdown() {
        // 关闭所有 PartitionWorker 的 Disruptor
        for (Map.Entry<String, PartitionWorker> entry : kpWorkMap.entrySet()) {
            try {
                entry.getValue().stop();
            } catch (Exception e) {
                logger.error("Match_ERROR: shutdown PartitionWorker failure, key={}", entry.getKey(), e);
            }
        }
        kpWorkMap.clear();
        // 关闭 inputConsumer
        if (inputConsumer != null) {
            try {
                inputConsumer.close();
            } catch (Exception e) {
                logger.error("Match_ERROR: close inputConsumer failure!", e);
            }
        }
    }
}
