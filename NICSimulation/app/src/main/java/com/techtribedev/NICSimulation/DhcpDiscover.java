/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.Serializable;

/**
 *
 * @author asimion
 */
//client initiate the comm with DHCPDISCOVER message (req latest IP offered )
public class DhcpDiscover implements Serializable {
    private IPv4 tmpIP;
    private String MAC;
    //String MAC;
    
    public DhcpDiscover(String mac){
        tmpIP = null;
        MAC = mac;
    }
    
    public DhcpDiscover(IPv4 ip, String mac){
        tmpIP = ip;
        MAC = mac;
    }
    
    public IPv4 getIP(){ return tmpIP; }


    public String getClientMac() {
        return MAC;
    }
}
