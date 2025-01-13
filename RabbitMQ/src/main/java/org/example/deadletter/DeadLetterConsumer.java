package org.example.deadletter;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.AMQP.BasicProperties;

public class DeadLetterConsumer {

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

            // 队列绑定交换机-channel.queueBind(队列名, 交换机名, 路由key[广播消息设置为空串])
            createChannel.queueBind("myQueue", "order.dead.exchange", "routing_key_myQueue");

            createChannel.basicConsume("myQueue", false, "", new DefaultConsumer(createChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
                                           byte[] body) throws IOException {

                    System.out.println("时间： " + new Date());

                    System.out.println("consumerTag: " + consumerTag);
                    System.out.println("envelope: " + envelope);
                    System.out.println("properties: " + properties);
                    String string = new String(body, "UTF-8");
                    System.out.println("接收到消息： -》 " + string);

                    long deliveryTag = envelope.getDeliveryTag();
                    Channel channel = this.getChannel();
                    channel.basicAck(deliveryTag, true);
                    System.out.println("死信队列中处理完消息");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }

}
