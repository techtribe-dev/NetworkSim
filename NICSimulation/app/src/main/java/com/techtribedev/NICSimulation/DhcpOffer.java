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
//the offer from dhcp server for client
public class DhcpOffer implements Serializable {
    private String mClientMAC;
    private IPv4 mClientIP;
    private IPv4 mServerIP;
    
    public DhcpOffer(String cMac, IPv4 cIp, IPv4 sIp){
        mClientMAC = cMac;
        mClientIP = cIp;
        mServerIP = sIp;
    }
    
    public String  getCMAC(){ return mClientMAC; }
    
    public IPv4    getCIP() { return mClientIP; }
    
    public IPv4    getSIP() { return mServerIP; }
}
