package org.example.deadletter;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CommonConsumer {
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
            createChannel.queueBind("myQueue", "order.exchange", "routing_key_myQueue");

            Consumer consumer = new DefaultConsumer(createChannel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    System.out.println("consumerTag: " + consumerTag);
                    System.out.println("envelope: " + envelope);
                    System.out.println("properties: " + properties);
                    String string = new String(body, "UTF-8");
                    System.out.println("接收到消息： -》 " + string);

                    long deliveryTag = envelope.getDeliveryTag();
                    Channel channel = this.getChannel();
                    System.out.println("拒绝消息， 使之进入死信队列");
                    System.out.println("时间： " + new Date());
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                    }

                    // basicReject第二个参数为false进入死信队列或丢弃
                    channel.basicReject(deliveryTag, false);
                }
            };


            createChannel.basicConsume("myQueue", false, "", consumer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }

    }

}
