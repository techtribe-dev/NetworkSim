/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.IOException;

/**
 *
 * @author asimion
 */
public class Computer extends NIC {
    
    private final String mHostDomain;
    
    public Computer(boolean discDhcp, String host) throws IOException {
        super(discDhcp);
        mHostDomain = host;
    }
    
    public void displayInfo() {
        System.out.println("Computer Hostname:" + mHostDomain);
        System.out.println("LAN:");
        if(this.getEthPort().getIPv4() != null){
            String info = this.getEthPort().getName() + ": link is "    + this.getEthPort().getLinkStatus() + 
                                                        " ether: "      + this.getEthPort().getMAC() + 
                                                        " inet: "       + this.getEthPort().getIPv4().toStringIP() + 
                                                        " netmask: "    + this.getEthPort().getIPv4().toStringMask();
            System.out.println(info);
        } else{
            String info = this.getEthPort().getName() + ": link is " + this.getEthPort().getLinkStatus() + 
                                                          " ether: " + this.getEthPort().getMAC();
            System.out.println(info);
        }
        System.out.println();
    } 
}
