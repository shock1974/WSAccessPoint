/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket.processor;

import cn.com.inhand.tools.exception.PacketException;

/**
 *
 * @author han
 */
public interface DNMsgProcessorInterface {
    /**
     * 
     * @param msg   DNMessage
     * @return  packet 
     */
    public byte[] wrap(DNMessage msg)throws PacketException;
    
    /**
     * 
     * @param packet
     * @return 
     */
    public DNMessage unwrap(byte[] packet)throws PacketException;
    
}
