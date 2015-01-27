/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket;
import java.util.Map;
 
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;
 
public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor{
 
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {
        System.out.println("Before Handshake");
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }
 
    @Override
    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception ex) {
        System.out.println("After Handshake");
        super.afterHandshake(request, response, wsHandler, ex);
        
    }
 
}