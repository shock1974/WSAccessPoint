/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.mq.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 *
 * @author han
 */
public class DelivingResultProducer {
    private String exchange;
    private String routingKey;
    private RabbitTemplate rabbitTemplate;
    

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }
    
    public void sendMessage(String msg) {
		
		//Exchange的名称为"hello.topic"，routingkey的名称为"hello.world.q123ueue"
		rabbitTemplate.convertAndSend(exchange, routingKey,
				msg);

    }
    
}
