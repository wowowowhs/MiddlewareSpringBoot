package org.example.fanout;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class FanoutConsumer {

    public static void main(String[] args) throws IOException, TimeoutException {

        String changeName = "xc_exchange_name";

        String queueName1 = "xc_queue_fanout_name1";
        String queueName2 = "xc_queue_fanout_name2";
        String queueName3 = "xc_queue_fanout_name3";
        String queueName4 = "xc_queue_fanout_name4";

        //创建一个连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //服务地址
        factory.setHost("127.0.0.1");
        //账号
        factory.setUsername("guest");
        //密码
        factory.setPassword("guest");
        //端口号
        factory.setPort(5673);

        //创建连接
        Connection connection = factory.newConnection();
        //创建信道
        Channel channel = connection.createChannel();

        DeliverCallback deliverCallback = (consumerTage, message) -> {
            System.out.println("接受到消息：" + new String(message.getBody()));
        };

        CancelCallback cancelCallback = consumerTage -> {
            System.out.println("消费消息被中断......");
        };

        /**
         * 消费消息
         * 1、消费哪个队列
         * 2、消费成功之后是否需要自动应答，true：自动应答
         * 3、接受消息的回调函数
         * 4、取消消息的回调函数
         */
        channel.basicConsume(queueName1, true, deliverCallback, cancelCallback);
        channel.basicConsume(queueName2, true, deliverCallback, cancelCallback);
        channel.basicConsume(queueName3, true, deliverCallback, cancelCallback);
        channel.basicConsume(queueName4, true, deliverCallback, cancelCallback);

    }

}
