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
import java.util.concurrent.Semaphore;
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
    //range [49169 and 65535] - ports value for each client
    private static Integer cntRxPort = 49268;
    private static Integer cntTxPort = 49269;
    private Integer _rxPort;
    private Integer _txPort;
    private DatagramSocket  _rxSock;
    private DatagramSocket  _txSock; 
    private InetAddress     _serverAddr;
    private IPv4   mIp;
    private IPv4   mGwIp;
    private String mMac;
    private ByteArrayInputStream bis;
    private ObjectInputStream ois;
    private ByteArrayOutputStream bos ;
    private ObjectOutputStream oos;
    boolean running_dhclient;
    private Semaphore serverSemaphore;
    
    public DhcpClient(String mac) throws IOException {
        //set ports 
        _rxPort = cntRxPort;
        _txPort = cntTxPort;
        //create UDP Sockets 
        _rxSock = new DatagramSocket(_rxPort);
        _txSock = new DatagramSocket(_txPort);
        _serverAddr = InetAddress.getByName(SERVER_ADDRESS);
        
        //client IPv4       
        mIp = null;
        mMac = mac;
        
        //for serialization 
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        
        //cnt ports for new dhcp client
        cntRxPort = cntRxPort + 2;
        cntTxPort = cntTxPort + 2;
        
        //start dhclient
        running_dhclient = true;
    }
    
    public void getRxTxPort(){
        DebugMode.log(_rxPort.toString() + "  "  + _txPort.toString());
    }
    
    public void setSemaphore(Semaphore semaphore){
     serverSemaphore = semaphore;
    }
    
    @Override
    public synchronized void run(){
         DebugMode.log("->Client Start!");
         IPv4 tmpIpOffered = null;
         IPv4 tmpGwOffered = null;
         
        try {
            //Create DHCPDISCOVER 
            DhcpDiscover dhcpmessage = new DhcpDiscover(mIp, mMac);
            //Serialize DhcpDiscover
            oos.writeObject(dhcpmessage);
            oos.flush();
            byte[] buffdisc = bos.toByteArray();
            //Create Datagram with bytes of DhcpDiscover
            DatagramPacket sendDiscover = new DatagramPacket(buffdisc, buffdisc.length, _serverAddr, SERVER_RX_PORT);
            //Send the Datagram
            _txSock.send(sendDiscover);
            DebugMode.log("@Client: DHCPDISCOVER sent");
            
            //wait DHCPOFFER
            byte[] buffoffer = new byte[1024];
            DatagramPacket recvOffer = new DatagramPacket(buffoffer, buffoffer.length, _serverAddr, SERVER_TX_PORT);
            Object objRecv = null;
            serverSemaphore.acquire();
            
            while(objRecv == null){
                _rxSock.receive(recvOffer);
                bis = new ByteArrayInputStream(recvOffer.getData());
                ois = new ObjectInputStream(bis);
                objRecv = ois.readObject();
            }
            DebugMode.log("@Client: DHCPOFFER received " + objRecv.toString());
            
            //Read from DHCPOFFER
            DhcpOffer dhcpoffer = (DhcpOffer)objRecv;
            tmpIpOffered = dhcpoffer.getCIP();
            tmpGwOffered = dhcpoffer.getSIP();
            
            //Create DhcpRequest with info from DhcpOffer
            DhcpRequest dhcpreq = new DhcpRequest(tmpIpOffered, tmpGwOffered);
            
            //close bos and oos to remove tmp bytes from pipe
            bos.close();
            oos.close();
            //open new bos and oos 
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(dhcpreq);
            oos.flush();
            byte[] buffreq = bos.toByteArray();
            //Create Datagram with bytes of DhcpRequest
            DatagramPacket sendRequest = new DatagramPacket(buffreq, buffreq.length, _serverAddr, SERVER_RX_PORT);
            //Send the Datagram
            _txSock.send(sendRequest);
            DebugMode.log("@Client: DHCPREQUEST sent");
            
            //wait DHCPACK
            byte[] buffack = new byte[1024];
            DatagramPacket recvAck = new DatagramPacket(buffack, buffack.length, _serverAddr, SERVER_TX_PORT);
            objRecv = null;
            serverSemaphore.acquire();
            while(objRecv == null){
                _rxSock.receive(recvAck);
                bis = new ByteArrayInputStream(recvAck.getData());
                ois = new ObjectInputStream(bis);
                objRecv = ois.readObject();
            }
            DebugMode.log("@Client: DHCPACK received " + objRecv.toString()); 
            mIp = tmpIpOffered;
            mGwIp = tmpGwOffered;
            
            DebugMode.log("@Client: Bye Bye!");
            //TODO: Implement ARP and then: DHCPDECLINE if not we have an uniq IPv4 in our network 
        } catch (IOException | ClassNotFoundException | InterruptedException ex) {
            Logger.getLogger(DhcpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        running_dhclient = false;        
    }
    
    public boolean getIsRunning(){
        return running_dhclient;
    }
    
    public IPv4 getIp(){
        return mIp;
    }
    
    public IPv4 getGwIp(){
        return mGwIp;
    }
}
