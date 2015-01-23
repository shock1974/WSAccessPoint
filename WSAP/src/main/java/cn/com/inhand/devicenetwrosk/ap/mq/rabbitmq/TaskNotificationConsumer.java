/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetwrosk.ap.mq.rabbitmq;

import com.rabbitmq.client.Channel;
import java.util.logging.Logger;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 *
 * @author han
 */
@Component
public class TaskNotificationConsumer  implements MessageListener,ChannelAwareMessageListener{
private final static Logger logger =  Logger.getLogger("TaskNotificationConsumer");
@Override
public void onMessage(Message message, Channel channel) throws Exception {
    logger.info("recv a message:"+(new String(message.getBody())));
    System.out.println("recv a message:"+(new String(message.getBody())));
onMessage(message);	
}

@Override
public void onMessage(Message message) {
// TODO Auto-generated method stub
//在此接收消息
    logger.info("recv a message:"+(new String(message.getBody())));
    System.out.println("recv a message:"+(new String(message.getBody())));
}


}
    

