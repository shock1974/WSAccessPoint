/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.WsOutbound;

/**
 *
 * @author han
 */
class DNMsgInbound extends MessageInbound {
    //comment by hancj
    private int userIdName = 0;
    private Logger logger = Logger.getLogger(DNMsgInbound.class.getName());

    public int getUserIdName() {
        return userIdName;
    }
    
    protected void onOpen(WsOutbound outbound) {
        super.onOpen(outbound);
        logger.info("Server onOpen");
    }

    protected void onClose(int status) {
        logger.info("Server onClose");
    }

    // 有二进制消息数据到达，比如音频等文件传输
    @Override
    protected void onBinaryMessage(ByteBuffer buffer) throws IOException {
        logger.info("Binary Message Receive: " + buffer.remaining());
    }

    @Override
    protected void onTextMessage(CharBuffer buffer) throws IOException {
        String msgOriginal = buffer.toString();
        
        java.util.Date now = new java.util.Date(System.currentTimeMillis());
        String rspMsg = "" + now.toString() + ">" + msgOriginal;
        logger.log(Level.INFO, "recieved:{0}", msgOriginal);
        System.out.println("recieved:" + msgOriginal);
        
        String[] msg=msgOriginal.split("\n");
        for (int ii=0;ii<msg.length;ii++){
            System.out.println("Line["+ii+"]:("+msg[ii]+")");
        }
        
        CharBuffer msgBuffer = CharBuffer.wrap(rspMsg);

        WsOutbound outbound = this.getWsOutbound();
        outbound.writeTextMessage(msgBuffer);

    }
}
