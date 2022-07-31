package com.yc.communitys.dao;

import com.yc.communitys.CommunityApplication;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer kafkaProducer;

    @Test
    public void testKafka() {
        kafkaProducer.sendMessage("test", "你好");
        kafkaProducer.sendMessage("test", "在吗");
        try {
            Thread.sleep(1000 * 10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
// 生产者和消费者封装成Bean
@Component
class KafkaProducer {

    @Autowired
    // 容器整合了, 直接获取
    private KafkaTemplate kafkaTemplate;

    /**
     * 生产者发消息
     * */
    public void sendMessage(String topic, String content) {
        kafkaTemplate.send(topic, content);
    }

}

@Component
class KafkaConsumer {
    /**
     * 消费者处理消息是被动的调用
     * */
    @KafkaListener(topics={"test"})
    public void handleMessage(ConsumerRecord record) {
        System.out.println(record.value());
    }
}