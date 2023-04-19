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
    
    public DhcpDiscover(){
        tmpIP = null;
    }
    
    public DhcpDiscover(IPv4 ip){
        tmpIP = ip;
    }
    
    public IPv4 getIP(){ return tmpIP; }
}
