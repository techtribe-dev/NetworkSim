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
import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author asimion
 */
public class NIC {
    final static String SERVER_ADDRESS = "localhost";
    final static Integer SERVER_RX_PORT = 49168;
    final static Integer SERVER_TX_PORT = 49167;
    
    private EthPort ethPort;
    private final ArrayList<byte[]> transmitBuffer; 
    private final ArrayList<byte[]> receiveBuffer;
    private boolean discoverableDhcp;
    
    public NIC(boolean discDhcp) throws IOException{
        if(discDhcp){
            ethPort = new EthPort("enp1s0", "HALF_DUPLEX", "GIGABIT", "LAN");//typeN BOTH
        }else{
            ethPort = new EthPort();
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
    
    public void sendPacket(Object packet) throws IOException{
        if(packet instanceof IcmpPacket){
            IcmpPacket icmpPack = (IcmpPacket) packet;
            DebugMode.log(icmpPack.getHeader());
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(packet);
        oos.flush();        
        byte[] packetByte = bos.toByteArray();
        transmitBuffer.add(packetByte);
        DatagramPacket sendPacket = new DatagramPacket(packetByte, packetByte.length,  InetAddress.getByName(SERVER_ADDRESS), SERVER_RX_PORT);
        //Send the Datagram
        ethPort.getTxSock().send(sendPacket);
    }
    
    public void recvPacket(byte[] buff) throws IOException, ClassNotFoundException{
            DatagramPacket recvPacket = new DatagramPacket(buff, buff.length, InetAddress.getByName(SERVER_ADDRESS), SERVER_TX_PORT);
            Object objRecv = null;
            ByteArrayInputStream bis = null;
            ObjectInputStream ois = null;
            while(objRecv == null){
                ethPort.getRxSock().receive(recvPacket);
                bis = new ByteArrayInputStream(recvPacket.getData());
                ois = new ObjectInputStream(bis);
                objRecv = ois.readObject();
            }
            //bis.close();
            //ois.close();
            DebugMode.log("Received " + objRecv.toString());
            receiveBuffer.add(recvPacket.getData());
    }
}
