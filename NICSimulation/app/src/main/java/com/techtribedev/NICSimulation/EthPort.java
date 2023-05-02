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
    //pentru test 
    private static Integer lastByteMac = 10;
    
    public EthPort(){
        mEthName = "eth0";
        mMAC = "aa:bb:cc:dd:ee:00:66";
        mComm = TypeComm.HALF_DUPLEX;
        mBand = TypeBandwidth.FAST_ETHERNET;
        mLink = LinkState.DOWN;
        mNet  = TypeNet.LAN;
    }

    public EthPort(String name, String typeC, String typeB, String typeN) throws IOException{
        mEthName = name;
        mMAC = "aa:bb:cc:dd:ee:00:"+ lastByteMac.toString();
        mComm = (typeC.equals("HALF_DUPLEX"))? TypeComm.HALF_DUPLEX : TypeComm.FULL_DUPLEX;
        mBand = (typeC.equals("GIGABIT"))? TypeBandwidth.GIGABIT : TypeBandwidth.FAST_ETHERNET;
        mNet = (typeN.equals("WAN"))? TypeNet.WAN : TypeNet.LAN;
        mLink = LinkState.DOWN;
        dhclient = new DhcpClient(mMAC);
        lastByteMac = lastByteMac > 99 ? 10 : lastByteMac++;
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
