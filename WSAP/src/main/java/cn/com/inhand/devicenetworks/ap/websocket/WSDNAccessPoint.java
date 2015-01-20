/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.inhand.devicenetworks.ap.websocket;

import cn.com.inhand.devicenetworks.ap.websocket.processor.DNMessage;
import cn.com.inhand.devicenetworks.ap.websocket.processor.DNMsgProcessorInterface;
import cn.com.inhand.devicenetworks.ap.websocket.processor.Parameter;
import cn.com.inhand.devicenetworks.ap.websocket.processor.WSv1Processor;
import cn.com.inhand.tools.exception.PacketException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author han
 */
public class WSDNAccessPoint extends TextWebSocketHandler {

    private boolean isLogin = false;
    private long connection_time = System.currentTimeMillis();
    private long last_msg = 0l;
    private String token = null;
    private String vmid = null;
    private String deviceid = null;

    private WebSocketSession session = null;

    protected void close(WebSocketSession session) {
        try {
            session.close();
           
        } catch (IOException ex) {
            Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    protected void handleTextMessage(WebSocketSession session,
            TextMessage message) throws Exception {
        this.session = session;
        System.out.println("Debug in WSDNAccessPoint.java [Ln:48] : WebSocketSession session=" + session.toString());
        this.last_msg = System.currentTimeMillis();
        super.handleTextMessage(session, message);
        TextMessage returnMessage = new TextMessage(message.getPayload() + " received at server");
        session.sendMessage(returnMessage);

        DNMsgProcessorInterface parser = new WSv1Processor();
        try {
            DNMessage msg = parser.unwrap(message.getPayload().getBytes());
            Logger.getLogger(WSv1Processor.class.getName()).log(Level.INFO, null, msg);
            if (!this.isLogin) {
                try {
                    this.onLgin(msg, session);
                } catch (PacketException pe) {
                    Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.WARNING, null, "Failed to Login from "
                            + session.getRemoteAddress() + ":"
                            + pe.toString());
                    this.close(session);
                }
            } else {
                if (msg.getName().equalsIgnoreCase("heatbeat") && msg.getType() == 0) {
                    this.onHeatbeat(msg, session);
                } else if (msg.getName().equalsIgnoreCase("logout") && msg.getType() == 0) {
                    this.onLogout(msg, session);
                    this.close(session);
                } else if (msg.getType() == 1) {
                    this.onAck(msg, session);
                } else {
                    Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.WARNING, null, "Unsupported msg from "
                            + session.getRemoteAddress() + ":"
                            + msg.toString());
                    this.close(session);
                }

            }

        } catch (PacketException ex) {
            Logger.getLogger(WSv1Processor.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * 调用API，认证本连接是否合法
     *
     * @param params 登陆认证所需的参数
     * @return
     */
    private boolean auth(Map params) {
        if (false) {
            //如果认证失败，则返回false
            return false;
        }
        //认证成功
        return true;
    }

    /**
     * 处理Inbox的登陆请求
     *
     * @param login
     */
    private void onLgin(DNMessage login, WebSocketSession session) throws PacketException, IOException {
        if (login.getName().equals("login") && login.getType() != 0) {
            //调用登录API验证合法性
            if (!auth(login.getParams())) {
                List list = new ArrayList();
                list.add(new Parameter("result", "0"));
                list.add(new Parameter("reason", ""));
                DNMessage ack = new DNMessage("login", "response", login.getTxid(), list);
                session.sendMessage(new TextMessage(ack.toString()));
                list.clear();

                throw new PacketException("Failed to Login!");
            } else {
                //for debug
                if (login.getParameter("vmid").equals("1111")) {

                    List list = new ArrayList();
                    list.add(new Parameter("result", "0"));
                    list.add(new Parameter("reason", ""));
                    DNMessage ack = new DNMessage("login", "response", login.getTxid(), list);
                    session.sendMessage(new TextMessage(ack.toString()));
                    list.clear();

                    throw new PacketException("The Packet is not a login packet!");
                }
            }

        } else {
            throw new PacketException("The Packet is not a login packet!");
        }
    }

    /**
     * 处理Inbox的心跳请求
     *
     * @param heartBeat
     */
    private void onHeatbeat(DNMessage heartbeat, WebSocketSession session) throws IOException {

        List list = new ArrayList();
        list.add(new Parameter("result", "0"));
        list.add(new Parameter("reason", ""));
        DNMessage ack = new DNMessage("heatbeat", "response", heartbeat.getTxid(), list);
        session.sendMessage(new TextMessage(ack.toString()));
        list.clear();
    }

    /**
     * 处理Inbox返回的ACK
     *
     * @param ack
     */
    private void onAck(DNMessage ack, WebSocketSession session) {
        System.out.println("Debug in WSDNAccessPoint.java [Ln:165] : ack=" + ack.toString());
    }

    /**
     * 处理Inbox的logout请求
     *
     * @param logout
     */
    private void onLogout(DNMessage logout, WebSocketSession session) throws IOException {
       List list = new ArrayList();
                list.add(new Parameter("result", "0"));
                list.add(new Parameter("reason", ""));
                DNMessage ack = new DNMessage("login", "response", logout.getTxid(), list);
                session.sendMessage(new TextMessage(ack.toString()));
                list.clear();
    }

}
