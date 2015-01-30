/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.inhand.devicenetworks.ap.websocket;

import cn.com.inhand.devicenetworks.ap.mq.rabbitmq.DelivingResultProducer;
import cn.com.inhand.devicenetworks.ap.websocket.processor.DNMessage;
import cn.com.inhand.devicenetworks.ap.websocket.processor.DNMsgProcessorInterface;
import cn.com.inhand.devicenetworks.ap.websocket.processor.Parameter;
import cn.com.inhand.devicenetworks.ap.websocket.processor.WSDNSession;
import cn.com.inhand.devicenetworks.ap.websocket.processor.WSv1Processor;
import cn.com.inhand.tools.exception.PacketException;
import cn.com.inhand.common.dto.Error;
import cn.com.inhand.devicenetworks.ap.websocket.packet.LoginResultPacket;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

/**
 *
 * @author han
 */
public class WSDNAccessPoint extends TextWebSocketHandler {

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper mapper;
    //private WebSocketSession session = null;
    private DNMsgProcessorInterface parser = null;
    private ConnectionInfo cinfo = null;
    private DelivingResultProducer producer = null;
    private String server_addr = "mall.inhand.com.cn"

    /**
     * 初始化
     */
    public WSDNAccessPoint(ConnectionInfo info, DNMsgProcessorInterface parser, DelivingResultProducer producer,String host) {
        super();
        this.cinfo = info;
        this.parser = parser;
        this.producer = producer;
        this.server_addr = host;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Debug in WSDNAccessPoint.java [Ln:50] : WebSocketSession session=" + session.toString() + " is colsed. stauts=" + status);

        //----此处应该补充call 在线状态的api上报 该设备的websocket断开
        WSDNSession wsdnsn = this.cinfo.getWsdnsn(session.toString());
        if (wsdnsn != null) {
            wsdnsn.setSession(session);
            wsdnsn.setLast_msg(System.currentTimeMillis());
            this.cinfo.getWssn_map().remove(wsdnsn.getId());
            wsdnsn.setIsLogin(false);
            wsdnsn.setSession(null);
            /**
             * status.getCode() 1000客戶端異常斷掉 1001服務端主動斷掉 ？ 超時
             *
             */
            int status_code = status.getCode();
            if (status_code == 1000) {
                if (wsdnsn.getAction() >= 3) {
                    this.updateStatus(wsdnsn.getAction(), wsdnsn);
                } else {
                    this.updateStatus(202, wsdnsn);
                }
            } else {
                if (wsdnsn.getAction() >= 3) {
                    this.updateStatus(wsdnsn.getAction(), wsdnsn);
                } else {
                    this.updateStatus(202, wsdnsn);
                }
            }
        }
        //从map中去掉该session
        this.cinfo.getWsdnsn_map().remove(session.toString());

        super.afterConnectionClosed(session, status);
    }

    /**
     * 关闭该websocket连接
     *
     * @param session
     */
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

        System.out.println("Debug in WSDNAccessPoint.java [Ln:81] : WebSocketSession session=" + session.toString());

        super.handleTextMessage(session, message);
        TextMessage returnMessage = new TextMessage(message.getPayload() + " received at server");
        //session.sendMessage(returnMessage);
        System.out.println("Debug in WSDNAccessPoint.java [Ln:57] : WebSocketSession recv:" + message.getPayload());

        try {
            String string = message.getPayload();
            if (string == null || string.trim().equals("") || string.equals("undefined")) {
                System.out.println("Debug in WSDNAccessPoint.java [Ln:64] : WebSocketSession recv:null msg");

                return;
            }
            DNMessage msg = parser.unwrap(message.getPayload().getBytes());

            WSDNSession wsdnsn = this.cinfo.getWsdnsn(session.toString());

            if (wsdnsn == null) {
                if (msg.getName().equals("login")) {
                    try {
                        this.onLogin(msg, session, wsdnsn);
                    } catch (PacketException pe) {
                        //登陆失败
                        Logger.getLogger(WSDNAccessPoint.class.getName()).warning("Failed to Login from "
                                + session.getRemoteAddress() + ":"
                                + pe.toString());
                        this.close(session);
                    }
                } else {
                    //第一个包不是login报文
                    Logger.getLogger(WSDNAccessPoint.class.getName()).warning("Illegal connection from + " + session.getRemoteAddress() + ", the msg:"
                            + string);
                    this.close(session);
                }
            } else if (!wsdnsn.isIsLogin()) {
                wsdnsn.setSession(session);
                try {

                    this.onLogin(msg, session, wsdnsn);
                } catch (PacketException pe) {
                    //登陆失败
                    Logger.getLogger(WSDNAccessPoint.class.getName()).warning("Failed to Login from "
                            + session.getRemoteAddress() + ":"
                            + pe.toString());
                    this.close(session);
                }
            } else {
                //已经登录

                if (msg.getName().equalsIgnoreCase("heartbeat") && msg.getType() == 0) {
                    this.onHeartbeat(msg, session, wsdnsn);

                    this.updateStatus(2, wsdnsn);
                } else if (msg.getName().equalsIgnoreCase("logout")) {//&& msg.getType() == 0) {
                    this.onLogout(msg, session, wsdnsn);
                    session.close();
                } else if (msg.getType() == 1) {
                    this.onAck(msg, session, wsdnsn);
                } else {
                    this.onUnkownMsg(msg, session, wsdnsn);
                    Logger.getLogger(WSDNAccessPoint.class.getName()).warning("Unsupported msg from "
                            + session.getRemoteAddress() + ":"
                            + msg.toString());
                    //this.close(session);
                }

            }

        } catch (PacketException ex) {
            Logger.getLogger(WSv1Processor.class.getName()).log(Level.SEVERE, "while handling a text message", ex);
        }

    }

    /**
     *
     * @param type 1:login,2:heatbeat,3:logout,others:undefined
     * @param wsdnsn ,Websocket DN会话
     * @return 执行结果
     */
    private int updateStatus(int action, WSDNSession wsdnsn) {
        Map map = new HashMap();
        String id = wsdnsn.getId();
        String key = wsdnsn.getKey();
        String token = wsdnsn.getToken();
        if (id == null || key == null) {
            return 23007;
        }
        map.put("key", key);
        map.put("action", 1);

        String result = restTemplate.postForObject("http://"+server_addr+"/api/asset_status/" + id + "?access_token=" + token, null, String.class, map);

        System.out.println("----Debug in WSDNAccessPoint.auth()[ln:209]:result:" + result);
        map.clear();
        if (!result.contains("error_code")) {
            //认证成功
            //取_id,asset_id
            return 0;
        } else {
            try {
                Error error = mapper.readValue(result, Error.class);
                System.out.println("----Debug in WSDNAccessPoint.auth()[ln:218]:Error=" + error.toString());

                return error.getErrorCode();
            } catch (IOException ex) {
                Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
    }

    /**
     * 调用API，认证本连接是否合法
     *
     * @param params 登陆认证所需的参数
     * @return
     */
    private int auth(DNMessage login) {
        Map map = new HashMap();
        String id = login.getParameter("id").getValue();
        String key = login.getParameter("key").getValue();
        String token = login.getParameter("tocken").getValue();
        if (id == null || key == null) {
            return 23007;
        }
        map.put("key", key);
        map.put("action", 1);

        String result = restTemplate.postForObject("http://"+server_addr+"/api/asset_status/" + id + "?access_token=" + token, null, String.class, map);

        System.out.println("----Debug in WSDNAccessPoint.auth()[ln:246]:result:" + result);
        map.clear();
        if (!result.contains("error_code")) {
            try {
                //认证成功
                //取_id,asset_id
                LoginResultPacket packet=mapper.readValue(result,LoginResultPacket.class);
                login.getParams().put("id",packet.getId());
                login.getParams().put("asset_id", packet.getAssetId());
                login.getParams().put("sn", packet.getSn());
                System.out.println("----Debug in WSDNAccessPoint.auth()[ln:251]:id=" +packet );
                return 0;
            } catch (IOException ex) {
                Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        } else {
            try {
                Error error = mapper.readValue(result, Error.class);
                System.out.println("----Debug in WSDNAccessPoint.auth()[ln:256]:Error=" + error.toString());

                return error.getErrorCode();
            } catch (IOException ex) {
                Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.SEVERE, null, ex);
                return -1;
            }
        }
    }

    /**
     * 处理Inbox的登陆请求
     *
     * @param login
     */
    private void onLogin(DNMessage login, WebSocketSession session, WSDNSession wsdnsn) throws PacketException, IOException {
        if (login.getName().equals("login") && login.getType() == 0) {
            int result = auth(login);
            //调用登录API验证合法性
            if (result != 0) {
                List list = new ArrayList();
                list.add(new Parameter("result", "" + result));
                list.add(new Parameter("reason", ""));
                DNMessage ack = new DNMessage("login", "response", login.getTxid(), list);
                session.sendMessage(new TextMessage(new String(parser.wrap(ack))));
                list.clear();

                throw new PacketException("Failed to Login!");
            } else {
                //for debug
                if (login.getParameter("id").getValue().equals("1111")) {

                    List list = new ArrayList();
                    list.add(new Parameter("result", "21336"));
                    list.add(new Parameter("reason", ""));
                    DNMessage ack = new DNMessage("login", "response", login.getTxid(), list);
                    session.sendMessage(new TextMessage(new String(parser.wrap(ack))));
                    list.clear();

                    throw new PacketException("The token is invalid!");
                } else {
                    List list = new ArrayList();
                    list.add(new Parameter("result", "0"));
                    list.add(new Parameter("reason", ""));
                    DNMessage ack = new DNMessage("login", "response", login.getTxid(), list);
                    session.sendMessage(new TextMessage(new String(parser.wrap(ack))));
                    list.clear();
                    wsdnsn = new WSDNSession(login, session);
                    wsdnsn.setAction(1);
                    wsdnsn.setId(login.getParameter("id").getValue());
                    wsdnsn.setIsLogin(true);
                    wsdnsn.setAssetid(login.getParameter("asset_id").getValue());
                    wsdnsn.setSn(login.getParameter("sn").getValue());
                    wsdnsn.setToken(login.getParameter("access_token").getValue());
                    
                    wsdnsn.setKey(login.getParameter("key").getValue());
                    wsdnsn.setId(login.getParameter("id").getValue());
                    wsdnsn.setConnection_time(System.currentTimeMillis());
                    wsdnsn.setLast_msg(wsdnsn.getConnection_time());
                    
                   
                    //放入map中
                    this.cinfo.putWsdnsn(session.toString(), wsdnsn);
                    try {
                        WebSocketSession oldSession = this.cinfo.getWssn(wsdnsn.getId());
                        if (oldSession != null && oldSession.isOpen()) {
                            List list1 = new ArrayList();
                            list1.add(new Parameter("result", "23010"));
                            list1.add(new Parameter("reason", "A new session is established"));
                            DNMessage logout = new DNMessage("logout", "request", "MSG_FROM_SMARTVMS-1", list1);
                            oldSession.sendMessage(new TextMessage(new String(parser.wrap(logout))));
                            oldSession.close();
                        }
                    } catch (Exception e) {

                    }
                    this.cinfo.putWssn(wsdnsn.getId(), session);
                    //this.isLogin = true;
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
    private void onHeartbeat(DNMessage heartbeat, WebSocketSession session, WSDNSession wsdnsn) throws IOException, PacketException {

        List list = new ArrayList();
        list.add(new Parameter("result", "0"));
        list.add(new Parameter("reason", "" + wsdnsn.getAssetid() + "@" + wsdnsn.getLast_msg()));
        DNMessage ack = new DNMessage("heartbeat", "response", heartbeat.getTxid(), list);
        session.sendMessage(new TextMessage(new String(parser.wrap(ack))));
        list.clear();
        wsdnsn.setSession(session);
        wsdnsn.setLast_msg(System.currentTimeMillis());
        wsdnsn.setAction(2);
        this.updateStatus(2, wsdnsn);
    }

    /**
     * 处理Inbox返回的ACK
     *
     * @param ack
     */
    private void onAck(DNMessage ack, WebSocketSession session, WSDNSession wsdnsn) {

        if (ack.getName().equalsIgnoreCase("deliver goods")) {
            try {
                this.producer.sendMessage(new String(parser.wrap(ack)));
            } catch (PacketException ex) {
                Logger.getLogger(WSDNAccessPoint.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Debug in WSDNAccessPoint.java [Ln:165] : ack=" + ack.toString());
        wsdnsn.setSession(session);
        wsdnsn.setLast_msg(System.currentTimeMillis());
        wsdnsn.setAction(2);
        this.updateStatus(2, wsdnsn);
    }

    /**
     * 处理Inbox的logout请求
     *
     * @param logout
     */
    private void onLogout(DNMessage logout, WebSocketSession session, WSDNSession wsdnsn) throws IOException, PacketException {

        if (logout.getType() != 0) {
            //logout回应
            wsdnsn.setAction(3);
        } else {
            //logout 请求
            List list = new ArrayList();
            int result = 100;
            try {
                result = Integer.parseInt(logout.getParameter("action").getValue());
            } catch (Exception e) {

            }
            wsdnsn.setAction(result);

            list.add(new Parameter("result", "0"));
            list.add(new Parameter("reason", "" + wsdnsn.getAssetid() + "@" + wsdnsn.getLast_msg()));
            DNMessage ack = new DNMessage("logout", "response", logout.getTxid(), list);

            session.sendMessage(new TextMessage(new String(parser.wrap(ack))));
            list.clear();
        }
//        wsdnsn.setSession(session);
//        wsdnsn.setLast_msg(System.currentTimeMillis());
//        //从map中去掉该session
//        this.cinfo.getWsdnsn_map().remove(session.toString());
//        this.cinfo.getWssn_map().remove(wsdnsn.getId());
//        wsdnsn.setIsLogin(false);
//        wsdnsn.setSession(null);
    }

    /**
     * 处理Inbox的logout请求
     *
     * @param logout
     */
    private void onUnkownMsg(DNMessage msg, WebSocketSession session, WSDNSession wsdnsn) throws IOException, PacketException {
        List list = new ArrayList();
        list.add(new Parameter("result", "23009"));
        list.add(new Parameter("reason", "" + wsdnsn.getAssetid() + "@" + wsdnsn.getLast_msg()));
        DNMessage ack = new DNMessage(msg.getName(), "response", msg.getTxid(), list);
        session.sendMessage(new TextMessage(new String(parser.wrap(ack))));
        list.clear();
        wsdnsn.setSession(session);
        wsdnsn.setLast_msg(System.currentTimeMillis());
    }

}
