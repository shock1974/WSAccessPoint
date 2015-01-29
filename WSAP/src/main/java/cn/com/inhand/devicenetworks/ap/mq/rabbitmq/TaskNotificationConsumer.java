/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.inhand.devicenetworks.ap.mq.rabbitmq;

import cn.com.inhand.devicenetworks.ap.websocket.ConnectionInfo;
import cn.com.inhand.devicenetworks.ap.websocket.processor.DNMessage;
import cn.com.inhand.devicenetworks.ap.websocket.processor.DNMsgProcessorInterface;
import cn.com.inhand.devicenetworks.ap.websocket.processor.Parameter;
import com.rabbitmq.client.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author han
 */
//@Component
public class TaskNotificationConsumer implements MessageListener, ChannelAwareMessageListener {

    private final static Logger logger = Logger.getLogger("TaskNotificationConsumer");
    //
    private DNMsgProcessorInterface parser=null;
    
    private ConnectionInfo cinfo = null;
    public TaskNotificationConsumer(ConnectionInfo info,DNMsgProcessorInterface parser){
        this.cinfo = info;
        this.parser = parser;
        System.out.println("--~~~~~~~~~Debug in TaskNotificationConsumer.contructor()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--");
    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        logger.info("recv a message@onMessage(x,y):" + (new String(message.getBody())));
        System.out.println("recv a message@onMessage(x,y):" + (new String(message.getBody())));
        
        onMessage(message);
    }

    @Override
    public void onMessage(Message message) {
// TODO Auto-generated method stub
//在此接收消息
        logger.info("recv a message@onMessage(x):" + (new String(message.getBody())));
        System.out.println("recv a message@onMessage(x):" + (new String(message.getBody())));
       listen((new String(message.getBody())));
    }


    
    public void listen(String message){

         try{
             DNMessage msg = parser.unwrap(message.getBytes());
             if (msg != null){
                 Parameter param = msg.getParameter("_id");
                 if (param != null){
                     String _id=param.getValue();
                     WebSocketSession ws = this.cinfo.getWssn(_id);
                     if (ws != null){
                         ws.sendMessage(new TextMessage(message));
                     }else{
                         //因为在负载均衡模式下可能有多个Websocket AP，不能因为本AP没有这个设备就认为设备不在线，所以以下在多AP模式下不成立
                        //设备不在线
//                         List list = new ArrayList();
//                         list.add(new Parameter("result","30005"));
//                         list.add(new Parameter("reason","The asset is offline."));
//                         DNMessage ack = new DNMessage(msg.getName(),"response",msg.getTxid(),list);
//                         
//                         logger.info("The asset["+asset_id+"] is not online, return a offline ack to source. msg="+ack.toString());
//                          System.out.println("The asset["+asset_id+"] is not online, return a offline ack to source. msg="+ack.toString());
//                         //需要往rabbitmq rpc中回写
//                         //--------------------------------
                     }
                 }else{
                     //是否返回出错信息？
                 }
             }
         }catch(Exception e){
             logger.warning(e.getLocalizedMessage());
         }
    }
    
    public static void main(String[] agrs) throws Exception {  
        String path = "file:/home/han/myworks/workroom/NetBeansProjects/WSAP/RabbitMQDemo/RabbitMQDemo/src/main/webapp/WEB-INF/MQXMLApplicationContext.xml";  
        AbstractApplicationContext ctx = new FileSystemXmlApplicationContext(path);  
        RabbitTemplate template = ctx.getBean(RabbitTemplate.class);  
        template.convertAndSend("Hello, world!");  
        Thread.sleep(1000);  
        ctx.destroy();  
    } 

}
