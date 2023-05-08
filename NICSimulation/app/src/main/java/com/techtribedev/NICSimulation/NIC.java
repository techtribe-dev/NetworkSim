/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author asimion
 */
public class NIC {
    private EthPort ethPort;
    private ArrayList<Byte[]> transmitBuffer; 
    private ArrayList<Byte[]> receiveBuffer;
    private boolean discoverableDhcp;
    
    public NIC(boolean discDhcp) throws IOException{
        ethPort = new EthPort();
        if(discDhcp){
            ethPort = new EthPort("enp1s0", "HALF_DUPLEX", "GIGABIT", "LAN");//typeN BOTH
        }
        transmitBuffer = new ArrayList();
        receiveBuffer = new ArrayList();
    }
    
    public void setStaticEth(IPv4 _ip, IPv4 _gw){
        ethPort.setIPv4(_ip);
        ethPort.setGwIp(_gw);
    }
    
    public EthPort getEthPort(){
        return ethPort;
    }
    
}
