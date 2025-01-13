package org.example.deadletter;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class DeadLetterProducer {

    public static ConnectionFactory getConnectionFactory() {
        // 创建连接工程，下面给出的是默认的case
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setPort(5673);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        return factory;
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory connectionFactory = getConnectionFactory();
        Connection newConnection = null;
        Channel createChannel = null;
        try {
            newConnection = connectionFactory.newConnection();
            createChannel = newConnection.createChannel();

            // 声明一个正常的direct类型的交换机
            String exchangeName = "order.exchange";
            createChannel.exchangeDeclare(exchangeName, BuiltinExchangeType.DIRECT);
            // 声明死信交换机为===order.dead.exchange
            String dlxName = "order.dead.exchange";
            createChannel.exchangeDeclare(dlxName, BuiltinExchangeType.DIRECT);
            // 声明队列并指定死信交换机为上面死信交换机
            Map<String, Object> arg = new HashMap<String, Object>();
            arg.put("x-dead-letter-exchange", dlxName);
            createChannel.queueDeclare("myQueue", true, false, false, arg);

            String message = "测试消息test";
            createChannel.basicPublish("order.exchange", "routing_key_myQueue", null, message.getBytes());
            System.out.println("消息发送成功......");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (createChannel != null) {
                createChannel.close();
            }
            if (newConnection != null) {
                newConnection.close();
            }
        }

    }

}
