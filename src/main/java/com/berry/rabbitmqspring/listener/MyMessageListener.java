package com.berry.rabbitmqspring.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class MyMessageListener {

    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME = "queue.order";

//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = QUEUE_NAME, durable = "true"),
//            exchange = @Exchange(value = EXCHANGE_DIRECT),
//            key = {ROUTING_KEY}
//    )
//    )
    public void processMessage(String dataString, Message message, Channel channel) throws IOException {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            // 获取当前消息的deliveryTag
            log.info("消费端接收到了消息：dataString =" + dataString);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {

            // 获取当前消息是否是重复投递的
            Boolean redelivered = message.getMessageProperties().getRedelivered();

            // 第三个参数取值为true ：重新放回队列，broker会重新投递这个消息
            // 如果为false ：不重新放回会丢弃这个消息
            if (redelivered) channel.basicNack(deliveryTag, false, false);
            else channel.basicNack(deliveryTag, false, true);


            /**
             * multiple，它用于指示是否应该一次性拒绝多个消息。
             * multiple = true：拒绝所有小于或等于提供的 deliveryTag 的消息。此时，所有这些消息都会被批量处理。
             * multiple = false：仅拒绝提供的 deliveryTag 对应的单条消息。
             * reject 表示拒绝
             * channel.basciReject(deliveryTag,True); 与nack的区别就在于是否有批量操作，nack能够批量操作在deliveryTag之前的
             */
        }

        /**  设置尝试次数
         long deliveryTag = message.getMessageProperties().getDeliveryTag();
         try {
         // 获取当前消息的deliveryTag
         log.info("消费端接收到了消息：dataString =" + dataString);
         // 确认消息已被成功消费
         channel.basicAck(deliveryTag, false);
         } catch (Exception e) {
         // 获取当前消息的重试次数
         Integer retryCount = (Integer) message.getMessageProperties().getHeaders().get(RETRY_COUNT_HEADER);
         if (retryCount == null) {
         retryCount = 0;
         }

         if (retryCount >= MAX_RETRY_COUNT) {
         log.error("处理消息时发生异常，重试次数超过限制，删除消息。", e);
         channel.basicNack(deliveryTag, false, false); // 丢弃消息
         } else {
         log.warn("处理消息时发生异常，将消息重新放回队列。重试次数：" + (retryCount + 1), e);
         // 增加重试次数
         message.getMessageProperties().getHeaders().put(RETRY_COUNT_HEADER, retryCount + 1);
         channel.basicNack(deliveryTag, false, true); // 重新放回队列
         }
         }
         */

    }

    @RabbitListener(queues = {QUEUE_NAME})
    public void processMessagePrefetch(String dataString, Message message, Channel channel) throws InterruptedException, IOException {
        log.info("消费端 消息内容： " + dataString);
        TimeUnit.SECONDS.sleep(1);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
    }

}

