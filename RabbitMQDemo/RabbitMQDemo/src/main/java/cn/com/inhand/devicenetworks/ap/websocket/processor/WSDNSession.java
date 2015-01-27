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
    private String assetid=null;
    private String sn=null;
    //private String current_txid=null;
    private boolean isLogin = false;
    private long connection_time = 0l;
    private long last_msg = 0l;
    private String token = null;
    private WebSocketSession session = null;

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

}
