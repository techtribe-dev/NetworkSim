/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author asimion
 */
public class Router {
    private EthPort mInPortWAN;//(defapt e InputOutput pot trece pachete prin din LAN catre exterior)
    private EthPort mOutPortWAN;
    private ArrayList<EthPort> mPortsLAN;
    private String mNameVendor;
    private Integer mID;
    //TODO: generator de MAC pt fiecare port
    //TODO: server DHPC
    //TODO: GW (+ MASK ) care zice pentru mInPortWAN;
    //TODO: GW pt LAN;
    private IPv4 gwLan;
    
    public Router(){
        mInPortWAN = new EthPort("eno0", "HALF_DUPLEX", "GIGABIT", "WAN");
        mOutPortWAN = new EthPort("eno1", "HALF_DUPLEX", "GIGABIT", "WAN");
        
        mPortsLAN = new ArrayList();
        for(Integer i = 2; i < 5; i++){
            EthPort p = new EthPort("eno"+i.toString(), "HALF_DUPLEX", "GIGABIT", "LAN");
            mPortsLAN.add(p);
        }
        mID = (new Random()).nextInt(65523) * 123;
    }

    public Router(String vendor, int noLanPorts){
        mNameVendor = vendor;
        mInPortWAN = new EthPort("eno0", "HALF_DUPLEX", "GIGABIT", "WAN");
        mOutPortWAN = new EthPort("eno1", "HALF_DUPLEX", "GIGABIT", "WAN");
        mPortsLAN = new ArrayList();
        for(Integer i = 2; i < noLanPorts; i++){
            EthPort p = new EthPort("eno"+i.toString(), "HALF_DUPLEX", "GIGABIT", "LAN");
            mPortsLAN.add(p);
        }
        mID = (new Random()).nextInt(65523) * 123;
    }

    public void displayRoutersInfo(){
        System.out.println("Vendor:" + mNameVendor);
        System.out.println("ID:" + mID);
        String iipw = (mInPortWAN.getIPv4() != null)? mInPortWAN.getIPv4().toStringIP() : "";
        String oipw = (mOutPortWAN.getIPv4() != null)? mOutPortWAN.getIPv4().toStringIP() : "";
        System.out.println(mInPortWAN.getName() +"(eth wan IN): " + "link is " + mInPortWAN.getLinkStatus() + " IP:" + iipw);
        System.out.println(mOutPortWAN.getName() +"(eth wan OUT): " + "link is " + mOutPortWAN.getLinkStatus() + " IP:" + oipw);
        System.out.println("LAN:");
        for(EthPort ep : mPortsLAN){
            if(ep.getIPv4() != null) System.out.println(ep.getName() + ": link is " + ep.getLinkStatus() + " IP: " + ep.getIPv4());
            if(ep.getIPv4() == null) System.out.println(ep.getName() + ": link is " + ep.getLinkStatus() + " IP: ");
        }
    }

    public EthPort getInWan(){
        return mInPortWAN;
    }

    public EthPort getOutWan(){
        return mOutPortWAN;
    }
    
    public int connectPorts(UtpWire wire){
        int r = wire.connect();
        if(r != 404){
            //negociere viteza de transmisie intre cele doua placi -- pe viitor
            boolean ret = (wire.getPortRight().getIPv4() == null && wire.getPortLeft().getIPv4() == null);
            if(ret){
                EthPort lp = wire.getPortLeft();
                EthPort rp = wire.getPortRight();
                if(lp.getNetAsString().equals("WAN") && rp.getNetAsString().equals("WAN")){
                    IPv4 ip = new IPv4((new short[]{192,168,1,1}), (new short[]{255,255,255,0}));
                    wire.getPortLeft().setIPv4(ip);
                    wire.getPortRight().setIPv4(ip);
                    r = 200;//OK
                } else if (lp.getNetAsString().equals("LAN") && rp.getNetAsString().equals("LAN")){
                    
                    r = 200;//OK
                } else{
                    System.err.println("TYPE NET PORT NOT MATCHING");
                    r = 404;//ERROR
                }
            }else {
                System.err.println("ETH PORTS : Aleardy Have IP");
                r = 404;//error
            }
        }
        
        return r;
    }
}
