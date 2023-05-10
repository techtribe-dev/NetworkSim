/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asimion
 */
public class Computer extends NIC implements Runnable {
    
    private final String mHostDomain;
    boolean runningNIC;
    
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

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        runningNIC = true;
        while(runningNIC){
            try {
                //TODO: asculta sa raspunda la ping adica sa faca echo
                byte[] buff = new byte[1024];
                super.recvPacket(buff);
                DebugMode.log(Arrays.toString(super.getLatestPacket()));
                
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(Computer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
