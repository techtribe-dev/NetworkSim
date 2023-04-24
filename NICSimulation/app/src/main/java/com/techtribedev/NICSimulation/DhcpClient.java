/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asimion
 */
public class DhcpClient extends Thread{
    private final static String SERVER_ADDRESS = "localhost";
    private final static Integer SERVER_RX_PORT = 49168;
    private final static Integer SERVER_TX_PORT = 49167;
    //49169 și 65535 --valori porturi pentru clienti
    private static Integer cntRxPort = 49169;
    private static Integer cntTxPort = 49170;
    private Integer _rxPort;
    private Integer _txPort;
    private  DatagramSocket _rxSock;
    private  DatagramSocket _txSock; 
    private InetAddress _serverAddr;
    private IPv4 mIp;
    private String mMac;
    private ByteArrayInputStream bis;
    private ObjectInputStream ois;
    private ByteArrayOutputStream bos ;
    private ObjectOutputStream oos;
    private byte[] buff = new byte[1024]; 
    
    public DhcpClient(IPv4 ip, String mac) throws IOException {
        //set ports 
        _rxPort = cntRxPort;
        _txPort = cntTxPort;
        
        //pentru a Trimite Pachete Datagram
        _rxSock = new DatagramSocket(_rxPort);
        _txSock = new DatagramSocket(_txPort);
        _serverAddr = InetAddress.getByName(SERVER_ADDRESS);
        
        //client IPv4       
        mIp = ip;
        mMac = mac;
        
        //pentru serializare si deserializare
        bis = new ByteArrayInputStream(buff);
        ois = new ObjectInputStream(bis);
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        
        //cnt ports  pentru noul client dhcp
        cntRxPort++;
        cntTxPort++;
    }
    
    public void getRxTxPort(){
        System.out.println(_rxPort.toString() + "  "  + _txPort.toString());
    }
    
    
    public void run(){
        boolean running_dhclient = true;
        try {
            //generate DISCOVER 
            DhcpDiscover dhcpmessage = new DhcpDiscover(mIp, mMac);
            
            oos.writeObject(dhcpmessage);
            oos.flush();
            buff = bos.toByteArray(); 
           
            DatagramPacket sendPacketDiscover = new DatagramPacket(buff, buff.length, _serverAddr, SERVER_RX_PORT);//rxServer
             _txSock.send(sendPacketDiscover);
             
             //while(running_dhclient){
             //     ascult pachete datagram pe SERVER_TX_PORT, deserializez, vad ce tip de mesaj este si raspund mai departe
             //     cand am primit DHCPACK atunci running_dhclinet = false; 
             //    
             //}
        } catch (IOException ex) {
            Logger.getLogger(DhcpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
