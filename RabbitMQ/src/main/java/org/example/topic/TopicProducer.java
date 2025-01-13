package org.example.topic;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * direct类型
 *
 */
public class TopicProducer {

    public static void main(String[] args) throws IOException, TimeoutException {

        String changeName = "xc_exchange_topic_name";

        String queueName1 = "xc_queue_topic_name1";
        String queueName2 = "xc_queue_topic_name2";
        String queueName3 = "xc_queue_topic_name3";
        String queueName4 = "xc_queue_topic_name4";

        String key1 = "key1.key2.key3.*";
        String key2 = "key1.#";
        String key3 = "*.key2.*.key4";
        String key4 = "#.key3.key4";

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

        /**
         * 创建交换机
         * 1、交换机名称
         * 2、交换机类型:direct,topic,fanout,headers
         * 3、指定交换机是否需要持久化，如果设置为true，那么交换机的元数据要持久化
         * 4、指定交换机在没有队列绑定时，是否需要删除，设置false表示不删除
         * 5、Map<String,Object>类型，用来指定我们交换机类型其他的一些机构化参数，我们这里直接设置为null
         */
        channel.exchangeDeclare(changeName, BuiltinExchangeType.TOPIC, true, false, null);

        /**
         * 生成一个队列
         * 1、队列名称
         * 2、队列是否需要持久化，但是要注意，这里的持久化只是队列名称等这些元数据的持久化，不是队列中消息的持久化
         * 3、表示队列是不是私有的，如果是私有的，只有创建它的应用程序才能消费消息
         * 4、队列在没有消费者订阅的情况下，是否自动删除
         * 5、队列的一些结构化信息，比如声明死信队列，磁盘队列会用到
         */
        channel.queueDeclare(queueName1, true, false, false, null);
        channel.queueDeclare(queueName2, true, false, false, null);
        channel.queueDeclare(queueName3, true, false, false, null);
        channel.queueDeclare(queueName4, true, false, false, null);

        /**
         * 将交换机和队列绑定
         * 1、队列名称
         * 2、交换机名称
         * 3、路由键，在我们直连模式下，可以是我们的队列名称
         */
        channel.queueBind(queueName1, changeName, key1);
        channel.queueBind(queueName2, changeName, key2);
        channel.queueBind(queueName3, changeName, key3);
        channel.queueBind(queueName4, changeName, key4);

        // 发送消息
        String message = "hello my rabbit mq";

        /**
         * 发送消息
         * 1、发送到哪个交换机
         * 2、队列名称
         * 3、其他参数连接
         * 4、发送消息的消息体
         */
        channel.basicPublish(changeName, key1, null, "key1 fanout message".getBytes());

        channel.close();
        connection.close();
    }
}
