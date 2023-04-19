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
public class DhcpRequest implements Serializable{
    private IPv4 mClientIP;
    private IPv4 mServerIP;
    
    public DhcpRequest(IPv4 cIp, IPv4 sIp){
        mClientIP = cIp;
        mServerIP = sIp;    
    }
    
    public IPv4    getCIP() { return mClientIP; }
    
    public IPv4    getSIP() { return mServerIP; }
}
