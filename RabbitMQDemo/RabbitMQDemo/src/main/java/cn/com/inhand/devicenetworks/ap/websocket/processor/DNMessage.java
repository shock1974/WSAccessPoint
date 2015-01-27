/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.inhand.devicenetworks.ap.websocket.processor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author han
 *
 * {
 * "name":"show log", "type":"request", "txid":"12346", "params":[{
 * "name":"param1", "value":"value1" }] }
 */
public class DNMessage {

    //message name
    private String name = null;
    //0:request;1:response;other:undefined
    private int type = 0;
    //transcation id
    private String txid = null;
    //parameters
    private Map<String, Parameter> params;

    /**
     *
     * @param name
     * @param type
     */
    public DNMessage(String name, String stype, String txid, List<Parameter> parameters) {

        this.name = name;
        if (stype != null && stype.equalsIgnoreCase("request")) {
            type = 0;
        } else {
            type = 1;
        }

        this.txid = txid;
        params = new HashMap<String, Parameter>();
        if (parameters != null) {
            for (Parameter param : parameters) {
                if (param.getName() != null) {
                    this.params.put(param.getName(), param);
                }
            }
        }
    }

    @Override
    public void finalize() throws Throwable {
        this.params.clear();
        super.finalize();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public Map getParams() {
        return params;
    }

    public void setParams(Map<String, Parameter> params) {
        this.params = params;
    }

    public Parameter getParameter(String key) {
        return (Parameter) this.params.get(key);
    }
    
    @Override
    public String toString(){
        StringBuffer sb = new StringBuffer();
        sb.append("name="+this.name+"\n");
        sb.append("type="+this.type+"\n");
        sb.append("txid="+this.txid+"\n");
        
        Iterator it = this.params.values().iterator();
        while (it.hasNext()){
            Parameter p = (Parameter)it.next();
            sb.append("params."+p.getName()+"="+p.getValue()+"\n");
        }
        
        return sb.toString();
    }

}
