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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author han
 */
public class DelivingNoticeConsumer implements MessageListener, ChannelAwareMessageListener {

    @Autowired
    AmqpTemplate template;
    private final static Logger logger = Logger.getLogger("TranscationConsumer");
    //
    private DNMsgProcessorInterface parser = null;
    
    private DelivingResultProducer producer = null;

    private ConnectionInfo cinfo = null;

    public DelivingNoticeConsumer(ConnectionInfo info, DNMsgProcessorInterface parser,DelivingResultProducer producer) {
        this.cinfo = info;
        this.parser = parser;
        this.producer = producer;
        System.out.println("--~~~~~~~~~Debug in TranscationConsumer.contructor()~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~--");
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
        listen(message);
    
    }



    
    public void listen(Message message) {
        String str = new String(message.getBody());
        System.out.println("===============recv:" + message);
        try {
            DNMessage msg = parser.unwrap(str.getBytes());
            if (msg != null) {
                Parameter param = msg.getParameter("asset_id");
                if (param != null) {
                    String asset_id = param.getValue();
                    WebSocketSession ws = this.cinfo.getWssn(asset_id);
                    if (ws != null) {
                        ws.sendMessage(new TextMessage(str));
                    } else {
                        //因为在负载均衡模式下可能有多个Websocket AP，不能因为本AP没有这个设备就认为设备不在线，所以以下在多AP模式下不成立
                        //设备不在线
                        List list = new ArrayList();
                        list.add(new Parameter("result", "30005"));
                        list.add(new Parameter("reason", "The asset is offline."));
                        list.add(msg.getParameter("_id"));
                        list.add(msg.getParameter("transcation_id"));
                        list.add(msg.getParameter("asset_id"));
                        DNMessage ack = new DNMessage(msg.getName(), "response", msg.getTxid(), list);

                        MessageProperties properties =message.getMessageProperties();
//                        properties.setCorrelationId(correlationId);
        
                        producer.sendMessage(new String(parser.wrap(msg)));
                        logger.info("The asset[" + asset_id + "] is not online, return a offline ack to source. msg=" + ack.toString());
                        System.out.println("The asset[" + asset_id + "] is not online, return a offline ack to source. msg=" + ack.toString());
                         //需要往rabbitmq rpc中回写
                        //--------------------------------
                        
                    }
                } else {
                    //是否返回出错信息？
                }
            }
        } catch (Exception e) {
            logger.warning(e.getLocalizedMessage());
        }
    }

}
