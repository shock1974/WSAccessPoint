/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;

/**
 *
 * @author han
 */
public class WSAccessPoint extends WebSocketServlet {

       private static final long serialVersionUID = 4104682919545889813L;

    private Logger logger = Logger.getLogger(WSAccessPoint.class.getName());

/*
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,
            HttpServletRequest request) {
        // TODO Auto-generated method stub
        logger.info("receive ws request");
        return new DNMsgInbound();
    }
*/
    @Override
    protected StreamInbound createWebSocketInbound(String string) {
        
        // TODO Auto-generated method stub
        logger.info("receive ws request");
        return new DNMsgInbound();
    }

}
