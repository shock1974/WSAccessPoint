/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.com.inhand.devicenetworks.ap.websocket.processor;

import cn.com.inhand.tools.exception.PacketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 *
 * @author han
 */
public class WSv1Processor implements DNMsgProcessorInterface {

    @Override
    public byte[] wrap(DNMessage msg) throws PacketException {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        JSONObject jsonObj = new JSONObject();
        String name = msg.getName();
        if (name == null || name.equals("")) {
            throw new PacketException("msg name cannot be null!");
        }
        jsonObj.put("name", msg.getName());
        int type = msg.getType();
        if (type != 0 && type != 1) {
            throw new PacketException("msg type must be requset or response!");
        }
        jsonObj.put("type", msg.getType() == 0 ? "request" : "response");
        String txid = msg.getTxid();
        if (txid == null || txid.equals("")) {
            throw new PacketException("msg txid cannot be null!");
        }
        jsonObj.put("txid", msg.getTxid());

        jsonObj.element("params", msg.getParams());
        return jsonObj.toString().getBytes();
    }

    @Override
    public DNMessage unwrap(byte[] packet) throws PacketException {

        try {
            String json = new String(packet);
            if (json == null) {
                throw new PacketException("null msg!");
            }
            JSONObject obj = JSONObject.fromObject(json);
            String name = obj.getString("name");
            String type = obj.getString("type");
            String txid = obj.getString("txid");
            JSONArray array = obj.getJSONArray("params");
            List<Parameter> list = new ArrayList<Parameter>();
            if (array != null) {
                for (int ii = 0; ii < array.size(); ii++) {
                    JSONObject p = array.getJSONObject(ii);
                    Parameter param = new Parameter(p.getString("name"), p.getString("value"));
                    list.add(param);
                }
            }
            DNMessage msg = new DNMessage(name, type, txid, list);
            return msg;

        } catch (Exception e) {
            throw new PacketException(e.getMessage());
        }

    }
    
    public static void main(String args[]){
        String json = "{'name':'show log','type':'response','txid':'12346','params':[{'name':'result','value':'0'},{'name':'progress','value':'100'},{'name':'reason','value':'file too large'},{'name':'name1','value':'value1'}]}";
        DNMsgProcessorInterface parser = new WSv1Processor();
        try {
            DNMessage msg = parser.unwrap(json.getBytes());
            System.out.println("DNMessage:\n"+msg.toString());
            String str=new String(parser.wrap(msg));
            
            System.out.println("json:\n"+str);
        } catch (PacketException ex) {
            Logger.getLogger(WSv1Processor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

}
