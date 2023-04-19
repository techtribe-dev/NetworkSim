/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asimion
 */
public class DhcpServer extends Thread {
    private final Integer srcPORT = 68;
    private final Integer destPORT = 67;
    private final DatagramSocket srcSock;
    private final DatagramSocket destSock;
    private ByteArrayInputStream bis;
    private ObjectInputStream ois;
    private boolean running ;
    private  HashMap<String,IPv4> leasesTable;
    private byte[] buff = new byte[1024];
    
    public DhcpServer() throws SocketException{
        srcSock = new DatagramSocket(srcPORT);
        destSock = new DatagramSocket(destPORT);
        leasesTable = new HashMap();
    }
    
    public String getTheMAC(EthPort eth){
        return eth.getMAC();
    }
    
    public void run(){
        running = true;
        
        while(running){
            
            
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            try {
                //Citeste de pe PORTUL 68 de la client (DHCPDISCOVER, DHCPREQUEST)
                srcSock.receive(packet);
                bis = new ByteArrayInputStream(buff);
                ois = new ObjectInputStream(bis);
                Object deserializedObject = ois.readObject();
                if (deserializedObject instanceof DhcpDiscover ) {
                    //ExpectedType expectedObject = (ExpectedType) deserializedObject;
                    // Use expectedObject...
                }else if(deserializedObject instanceof DhcpRequest){
                    //do something
                }
                
                
                //genereaza  DHCP packet de raspuns (DHCPOFFER, DHPCACK)
                //SEND raspuns pe portul 67
                //inainte e ACK OK -> pune in leasesTable ce s-a oferit;
            } catch (IOException ex) {
                Logger.getLogger(DhcpServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DhcpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
              
        }
        
    }
}
