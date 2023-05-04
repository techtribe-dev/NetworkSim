/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asimion
 */
public class Router {
    private EthPort mInPortWAN;//(defapt prin InputOutput din/catre WAN)
    //private EthPort mOutPortWAN;
    private ArrayList<EthPort> mPortsLAN;
    private String mNameVendor;
    private Integer mID;
    //server DHPC
    private Semaphore serverSemaphore;
    private DhcpServer dhcpServer;
    private boolean enableDhcpServer;
    //TODO: GW (+ MASK ) care zice pentru mInPortWAN;
    private IPv4 gwLan;//(defapt InputOutput din/Catre LAN)
    
    public Router() throws IOException {
        //mInPortWAN = new EthPort("eno0", "HALF_DUPLEX", "GIGABIT", "WAN");
        //mOutPortWAN = new EthPort("eno1", "HALF_DUPLEX", "GIGABIT", "WAN");
        enableDhcpServer = false;
        mPortsLAN = new ArrayList();
        for(Integer i = 2; i < 5; i++){
            EthPort p = new EthPort("eno"+i.toString(), "HALF_DUPLEX", "GIGABIT", "LAN");
            mPortsLAN.add(p);
        }
        mID = (new Random()).nextInt(65523) * 123;
    }

    public Router(String vendor, int noLanPorts, boolean runDhcpServer) throws IOException {
        mNameVendor = vendor;
        //mInPortWAN = new EthPort("eno0", "HALF_DUPLEX", "GIGABIT", "WAN");
        //mOutPortWAN = new EthPort("eno1", "HALF_DUPLEX", "GIGABIT", "WAN");
        mPortsLAN = new ArrayList();
        for(Integer i = 2; i < noLanPorts; i++){;
            EthPort p = new EthPort("eno"+i.toString(), "HALF_DUPLEX", "GIGABIT", "LAN");
            mPortsLAN.add(p);
        }
        gwLan = new IPv4(new short[]{10,0,0,1}, new short[]{255,255,0,0});//hardcodat adresa IP de gw
        mID = (new Random()).nextInt(65523) * 123;
        enableDhcpServer = runDhcpServer;
        if(enableDhcpServer){
            serverSemaphore = new Semaphore(0);//semafor pentru singcronizare
            dhcpServer = new DhcpServer(gwLan, new short[]{255,255,0,0}, new short[]{10,0,128,15}, new short[]{10,0,128,30}, serverSemaphore);//hardcodat pool-ul de adrese din Lan
            dhcpServer.start();
        }
    }

    public void displayRoutersInfo() {
        System.out.println("Vendor:" + mNameVendor);
        System.out.println("ID:" + mID);
        //String iipw = (mInPortWAN.getIPv4() != null)? mInPortWAN.getIPv4().toStringIP() : "";
        //String oipw = (mOutPortWAN.getIPv4() != null)? mOutPortWAN.getIPv4().toStringIP() : "";
        //System.out.println(mInPortWAN.getName() +"(eth wan IN): " + "link is " + mInPortWAN.getLinkStatus() + " IP:" + iipw);
        //System.out.println(mOutPortWAN.getName() +"(eth wan OUT): " + "link is " + mOutPortWAN.getLinkStatus() + " IP:" + oipw);
        System.out.println("LAN:");
        for(EthPort ep : mPortsLAN){
            if(ep.getIPv4() != null){
                String info = ep.getName() + ": link is "    + ep.getLinkStatus() + 
                                                " ether: "   + ep.getMAC() + 
                                                " inet: "    + ep.getIPv4().toStringIP() + 
                                                " netmask: " + ep.getIPv4().toStringMask();
                System.out.println(info);
            }
            if(ep.getIPv4() == null) System.out.println(ep.getName() + ": link is " + ep.getLinkStatus() + " ether: " + ep.getMAC());
        }
    }

    public EthPort getInWan() {
        return mInPortWAN;
    }
    
    //pt Test
    public EthPort getLan1(){
        return mPortsLAN.get(0);//primul port LAN
    }
    
    public EthPort getLan2(){
        return mPortsLAN.get(1);//al doilea port LAN
    }
    //-----------
/*
    public EthPort getOutWan(){
        return mOutPortWAN;
    }
*/
    public int connectPorts(UtpWire wire) throws IOException{
        int r = wire.connect();
        if(r != 404){
            //TODO: negociere viteza de transmisie intre cele doua placi -- pe viitor
            boolean ret = (wire.getPortRight().getIPv4() == null && wire.getPortLeft().getIPv4() == null);
            if(ret){
                EthPort lp = wire.getPortLeft();
                EthPort rp = wire.getPortRight();
                if(lp.getNetAsString().equals("WAN") && rp.getNetAsString().equals("WAN")){
                    /*
                      IPv4 ip = new IPv4((new short[]{192,168,1,1}), (new short[]{255,255,255,0}));
                      wire.getPortLeft().setIPv4(ip);
                      wire.getPortRight().setIPv4(ip);
                    */
                    r = 200;//OK
                } else if (lp.getNetAsString().equals("LAN") && rp.getNetAsString().equals("LAN")){
                    if(enableDhcpServer == true){
                        //while(dhcpServer.getState() == Thread.State.TERMINATED){}
                        try {
                            //dhcpServer.start();
                            Thread.sleep(1000);
                            lp.getDhcpClient().setSemaphore(serverSemaphore);
                            lp.getDhcpClient().start();
                            
                            //dhcpServer.join();
                            lp.getDhcpClient().join();
                            
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Router.class.getName()).log(Level.SEVERE, null, ex);
                        } 
                    }
                    
                    if(lp.getDhcpClient().getIsRunning() == false){
                        lp.setIPv4(lp.getDhcpClient().getIp());
                        lp.setGwIp(gwLan);
                        rp.setIPv4(lp.getDhcpClient().getIp());
                        rp.setGwIp(gwLan);
                    }
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
