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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
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
    //49169 și 65535 --valori porturi pentru clienti
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
    //private byte[] buff = new byte[1024]; 
    boolean running_dhclient;
    private CyclicBarrier barrier ;//= new CyclicBarrier(1);
    private Semaphore serverSemaphore;
    
    public DhcpClient(String mac) throws IOException {
        //set ports 
        _rxPort = cntRxPort;
        _txPort = cntTxPort;
        //System.out.println(_rxPort + " " + _txPort);
        //pentru a Trimite Pachete Datagram
        _rxSock = new DatagramSocket(_rxPort);
        _txSock = new DatagramSocket(_txPort);
        _serverAddr = InetAddress.getByName(SERVER_ADDRESS);
        
        //client IPv4       
        mIp = null;
        mMac = mac;
        
        //pentru serializare si deserializare
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        
        //cnt ports  pentru noul client dhcp
        cntRxPort = cntRxPort + 2;
        cntTxPort = cntTxPort + 2;
        
        running_dhclient = true;
    }
    
    public void getRxTxPort(){
        System.out.println(_rxPort.toString() + "  "  + _txPort.toString());
    }
    
    public void setBarrier(CyclicBarrier cb){
        barrier = cb;
    }
    public void setSemaphore(Semaphore semaphore){
     serverSemaphore = semaphore;
    }
    public synchronized void run(){
         System.out.println("->Client Start!");
         IPv4 tmpIpOffered = null;
         IPv4 tmpGwOffered = null;
         
        try {
            DhcpDiscover dhcpmessage = new DhcpDiscover(mIp, mMac);
            oos.writeObject(dhcpmessage);
            oos.flush();
            byte[] buffdisc = bos.toByteArray();
            DatagramPacket sendDiscover = new DatagramPacket(buffdisc, buffdisc.length, _serverAddr, SERVER_RX_PORT);
            
            _txSock.send(sendDiscover);
            System.out.println("@Client: DHCPDISCOVER sended");
            
            //Astept DHCPOFFER
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
            System.out.println("@Client: DHCPOFFER received " + objRecv.toString());
            
            //Citesc din Offer
            DhcpOffer dhcpoffer = (DhcpOffer)objRecv;
            tmpIpOffered = dhcpoffer.getCIP();
            tmpGwOffered = dhcpoffer.getSIP();
            
            //Creez request cu ip-urile din offer + trimit catre server
            DhcpRequest dhcpreq = new DhcpRequest(tmpIpOffered, tmpGwOffered);
            
            //System.out.println(dhcpreq.toString());
            bos.close();
            oos.close();
            bos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(bos);
            oos.writeObject(dhcpreq);
            oos.flush();
            byte[] buffreq = bos.toByteArray();

            //System.out.println(buffreq.toString());
            DatagramPacket sendRequest = new DatagramPacket(buffreq, buffreq.length, _serverAddr, SERVER_RX_PORT);
            _txSock.send(sendRequest);
            System.out.println("@Client: DHCPREQUEST sended");
            
             //Astept DHCPACK
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
            System.out.println("@Client: DHCPACK received " + objRecv.toString()); 
            mIp = tmpIpOffered;
            mGwIp = tmpGwOffered;
            
            System.out.println("@Client: Bye Bye!");
            //Testat cu arp si apoi dhcpdecline daca nu unic ip-ul 
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
