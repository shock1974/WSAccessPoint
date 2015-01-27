/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket;

import cn.com.inhand.devicenetworks.ap.websocket.processor.WSDNSession;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.socket.WebSocketSession;

/**
 *
 * @author han
 */
public class ConnectionInfo {
    private Map<String, WSDNSession> wsdnsn_map = null;
    private Map<String, WebSocketSession> wssn_map = null;
    
    /**
     * Constructor
    */
    public ConnectionInfo(){
        this.wsdnsn_map=new HashMap();
        this.wssn_map = new HashMap();
    }
    
    @Override
    public void finalize() throws Throwable{
        if (this.wsdnsn_map != null){
            this.wsdnsn_map.clear();
        }
        if (this.wssn_map != null){
            this.wssn_map.clear();
        }
        super.finalize();
    }
    

    public Map<String, WSDNSession> getWsdnsn_map() {
        return wsdnsn_map;
    }

    public void setWsdnsn_map(Map<String, WSDNSession> wsdnsn_map) {
        this.wsdnsn_map = wsdnsn_map;
    }

    public Map<String, WebSocketSession> getWssn_map() {
        return wssn_map;
    }

    public void setWssn_map(Map<String, WebSocketSession> wssn_map) {
        this.wssn_map = wssn_map;
    }
    
    public void putWsdnsn(String key,WSDNSession session){
        this.wsdnsn_map.put(key, session);
    }
    
    public void putWssn(String key,WebSocketSession ws){
        this.wssn_map.put(key,ws);
    }
    
    public WSDNSession getWsdnsn(String key){
        return (WSDNSession)wsdnsn_map.get(key);
    }
    
    public WebSocketSession getWssn(String key){
        return (WebSocketSession)wssn_map.get(key);
    }
    
}
