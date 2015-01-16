/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket.processor;

/**
 *
 * @author han
 */
public class job {
    private String name = null;
    private String txid = null;
    /**
     * -1: waiting to excute
     * 0: waiting for response
     * 200: ok
     * other: error
     */
    private int status = -1;
    /**
     * result code from remote device in ersponse
     */
    //private int result = 0;
    
    //private String reason = null;
    
    private long start_ts = 0l;
    
    private long last_ts = 0l;
    
}
