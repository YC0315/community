package com.yc.communitys.event;

import com.alibaba.fastjson.JSONObject;
import com.yc.communitys.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @description: 事件生产者
 * @author: yangchao
 * @date: 2022/7/31 15:22
 **/
@Component
public class EventProducer {

    // spring整合kafka
    @Autowired
    private KafkaTemplate kafkaTemplate;

    // 处理事件(本质就是发布消息)
    public void fireEvent(Event event) {
        // 将事件发布到指定的主题，字符串包含事件对象的所有数据，先转换成字符串，消费者再转换回来
        kafkaTemplate.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
