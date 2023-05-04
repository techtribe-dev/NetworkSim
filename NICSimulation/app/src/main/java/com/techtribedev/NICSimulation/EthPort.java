/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author asimion
 */
public class EthPort implements PhyEth {
    private enum TypeNet{WAN, LAN} TypeNet mNet;
    private enum LinkState{UP, DOWN} LinkState mLink;
    private enum TypeComm{HALF_DUPLEX, FULL_DUPLEX} TypeComm mComm;
    private enum TypeBandwidth{GIGABIT, FAST_ETHERNET} TypeBandwidth mBand;
    private final String mEthName;
    private String mMAC;
    private IPv4 mIP;
    private IPv4 mGw;
    private DhcpClient dhclient;
    private final static String PREFIX_MAC = "00:16:3e0";
    private static final List<String> generatedAddresses = new ArrayList<>();
    //pentru test 
    private static Integer lastByteMac = 10;
    
    public EthPort(){
        mEthName = "eth0";
        mMAC = generateMac();
        mComm = TypeComm.HALF_DUPLEX;
        mBand = TypeBandwidth.FAST_ETHERNET;
        mLink = LinkState.DOWN;
        mNet  = TypeNet.LAN;
    }

    public EthPort(String name, String typeC, String typeB, String typeN) throws IOException{
        mEthName = name;
        mMAC = generateMac();
        mComm = (typeC.equals("HALF_DUPLEX"))? TypeComm.HALF_DUPLEX : TypeComm.FULL_DUPLEX;
        mBand = (typeC.equals("GIGABIT"))? TypeBandwidth.GIGABIT : TypeBandwidth.FAST_ETHERNET;
        mNet = (typeN.equals("WAN"))? TypeNet.WAN : TypeNet.LAN;
        mLink = LinkState.DOWN;
        dhclient = new DhcpClient(mMAC);
    }
    

    public EthPort(String name){
        mEthName = name;
    }

    @Override
    public void setLinkUp(){
        mLink = LinkState.UP;
        System.out.println("Link is UP!");
    }

    @Override
    public void setLinkDown(){
        mLink = LinkState.DOWN;
        System.out.println("Link is Down");
    }

    public void setIPv4(IPv4 _ip){
        mIP = _ip;
    }
    public void setGwIp(IPv4 _gw){
        mGw = _gw;
    }
    @Override
    public String getLinkStatus(){
        //System.out.println("Link is " + mLink.toString());
        return mLink.toString();
    }
    
    private static String generateRandomHexByte() {
        Random random = new Random();
        int randomByte = random.nextInt(256);
        return String.format("%02x", randomByte);
    }
    
    private static String generateMac(){
        String address;
        do {
            address = PREFIX_MAC + ":" + generateRandomHexByte() + ":" + generateRandomHexByte() + ":" + generateRandomHexByte();
        } while (generatedAddresses.contains(address));
        generatedAddresses.add(address);
        return address;       
    }
    
    public void setMac(){
        mMAC = generateMac();
    }
    
    public TypeComm getTC(){
        return mComm;
    }
    
    public TypeBandwidth getTB(){
        return mBand;
    }

    public String getName(){
        return mEthName;
    }

    public String getNetAsString(){
        return mNet.name();
    }
    
    public TypeNet getNet(){
        return mNet;
    }
    
    public String getMAC(){
        return mMAC;
    }
    
    public IPv4 getIPv4(){
        return mIP;
    }
    
    public DhcpClient getDhcpClient(){
        return dhclient;
    }
}
