package com.berry.rabbitmqspring;


import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class RabbitMQTest {

    public static final String EXCHANGE_DIRECT = "exchange.direct.order";
    public static final String ROUTING_KEY = "order";
    public static final String QUEUE_NAME = "queue.order";

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Test
    public void test01() {
        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "Hello RabbitMQ!Im SpringBoot!");
    }

    @Test
    public void test02() {
        for (int i = 0; i < 100; i++) {
            rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "Test Prefetch" + i);
        }
    }

    @Test
    public void test03() { // ���ó�ʱʱ��

        // ������Ϣ���ô�����,MessagePostProcessor�Ǹ��ӿڣ������Ǹ�����ʽ�ӿ�
        MessagePostProcessor messagePostProcessor = message -> {
            // ���ù���ʱ��
            message.getMessageProperties().setExpiration("7000"); // ��λ�Ǻ���
            return message;
        };

        rabbitTemplate.convertAndSend(EXCHANGE_DIRECT, ROUTING_KEY, "TIMEOUT~", messagePostProcessor);
    }

}
