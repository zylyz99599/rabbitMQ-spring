package com.berry.rabbitmqspring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


@Configuration
@Slf4j
public class RabbitConifg implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        // 消息发送到交换机成功或者失败时调用这个方法 arg1: 代表ACK arg2 代表错误信息
        log.info("confirm()回调函数打印 CorrelationData:" + correlationData);
        log.info("confirm()回调函数打印 ACK:" + b);
        log.info("confirm()回调函数打印 cause 失败原因:" + s);

    }

    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        // 消息从交换机投送到队列失败时才会回调，上者是无论是白都会返回
        log.info("消息主体：" + new String(returnedMessage.getMessage().getBody()));
        log.info("应答码：" + returnedMessage.getReplyCode());
        log.info("描述：" + returnedMessage.getReplyText());
        log.info("消息使用的交换机exchange："+returnedMessage.getExchange());
        log.info("消息使用的路由键routing："+returnedMessage.getRoutingKey());
    }
}
