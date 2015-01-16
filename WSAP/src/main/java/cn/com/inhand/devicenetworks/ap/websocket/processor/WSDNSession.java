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

/**
 *
 * @author han
 */
public class WSDNSession extends Engine{

    private Logger logger = Logger.getLogger(WSDNSession.class.getName()+"["+this.engineName+"]");
    private String assetid=null;
    private String rid=null;
    private String current_txid=null;

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getAssetid() {
        return assetid;
    }

    public void setAssetid(String assetid) {
        this.assetid = assetid;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getCurrent_txid() {
        return current_txid;
    }

    public void setCurrent_txid(String current_txid) {
        this.current_txid = current_txid;
    }

    public boolean isIsLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }
    
    private boolean isLogin = false;
    
    public WSDNSession(String strName) {
        super(strName);
    }
    
    public void run(){
        logger.log(Level.INFO, "is starting ...");
        while(this.isRunning()){
            //查询是否有消息下发
            //----待实现
            sleep(1000);
        }
        logger.log(Level.INFO, "is starting ...");
    }
    
    public void login(String loginStr)throws OptErrException{
        
    }
}
