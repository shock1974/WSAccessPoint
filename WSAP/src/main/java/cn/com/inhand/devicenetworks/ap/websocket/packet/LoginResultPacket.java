/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package cn.com.inhand.devicenetworks.ap.websocket.packet;

/**
 *
 * @author han
 */
public class LoginResultPacket {
    private String id;
    private String assetId;

    public String getId() {
        return id;
    }

    public void setId(String _id) {
        this.id = _id;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
    private String sn;
    
    @Override
    public String toString(){
        return "id="+id+"\nasset_id="+assetId+"\nsn="+sn;
    }
    
}
