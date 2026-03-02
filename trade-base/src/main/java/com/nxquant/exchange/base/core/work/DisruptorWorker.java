package com.nxquant.exchange.base.core.work;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.dsl.Disruptor;
import com.nxquant.exchange.base.core.kafka.ExKafkaEvent;
import com.nxquant.exchange.base.core.kafka.KafkaEvent;
import com.nxquant.exchange.base.core.work.exception.DisruptorExceptionHandler;
import com.nxquant.exchange.base.utils.NamedThreadFactory;

public class DisruptorWorker extends AbstractWorker {

    private final Disruptor<ExKafkaEvent> disruptor;
    private final DisruptorWorker.Translator TRANSLATOR = new DisruptorWorker.Translator();

    public DisruptorWorker(){
        //工人的任务列队长度（用2的指数幂表示）必须在 [0, 16]范围内
        byte queueSizeBits = 12;
        String workerName = "";
        this.disruptor = new Disruptor<>(ExKafkaEvent::new, 1 << queueSizeBits, new NamedThreadFactory(workerName));
        this.disruptor.setDefaultExceptionHandler(new DisruptorExceptionHandler());
        this.disruptor.handleEventsWith(this::handle);
    }

    private void handle(ExKafkaEvent exKafkaEvent, long sequence, boolean endOfBatch) {

    }


    @Override
    public void start(){
        disruptor.start();
    }

    /**
     * 此方法被外部调用，向Lmax写入消息
     * @param event
     */
    public void publish(KafkaEvent event) {
        //这是一种写法
        disruptor.publishEvent(TRANSLATOR, event);
    }


    private static class Translator implements EventTranslatorOneArg<ExKafkaEvent, KafkaEvent> {
        @Override
        public void translateTo(ExKafkaEvent exKafkaEvent, long sequence, KafkaEvent event) {
            exKafkaEvent.setEvent(event);
        }
    }

}
