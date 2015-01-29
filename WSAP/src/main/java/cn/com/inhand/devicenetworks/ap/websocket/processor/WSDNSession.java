/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket.processor;

import cn.com.inhand.tools.exception.OptErrException;
import cn.com.inhand.tools.utilities.Engine;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author han
 */
public class WSDNSession {

    private Logger logger = Logger.getLogger(WSDNSession.class.getName()+"["+this.assetid+"]");
    private String key=null;
    private String _id=null;
    private String assetid=null;
    private String sn=null;
    //private String current_txid=null;
    private boolean isLogin = false;
    private long connection_time = 0l;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
    private long last_msg = 0l;
    private String token = null;
    private WebSocketSession session = null;
    //状态类型。0:未登录;1：登录;2：维持心跳;101：升级固件（正常）退出;102：应用新配置（正常）退出; 103:维护（正常退出）; 201：超时断开; 202:设备端异常断开;其它：待定义
    private int action = 0;

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public boolean isIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public long getConnection_time() {
        return connection_time;
    }

    public void setConnection_time(long connection_time) {
        this.connection_time = connection_time;
    }

    public long getLast_msg() {
        return last_msg;
    }

    public void setLast_msg(long last_msg) {
        this.last_msg = last_msg;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }
    
    public WSDNSession(DNMessage login,WebSocketSession session) {
        this.key = login.getParameter("key").getValue();
        this._id=login.getParameter("_id").getValue();
        this.assetid = login.getParameter("asset_id").getValue();
        this.sn = login.getParameter("sn").getValue();
        this.token = login.getParameter("access_token").getValue();
        this.last_msg = System.currentTimeMillis();
        this.connection_time = this.last_msg;
        this.isLogin = true;
        this.session = session;
    }
    
//    public void run(){
//        logger.log(Level.INFO, "is starting ...");
//        while(this.isRunning()){
//            //查询是否有消息下发
//            //----待实现
//            sleep(1000);
//        }
//        logger.log(Level.INFO, "is starting ...");
//    }
//    

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

}
