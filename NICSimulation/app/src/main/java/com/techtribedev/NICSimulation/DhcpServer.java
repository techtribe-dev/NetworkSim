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
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asimion
 */
public class DhcpServer extends Thread {
    private  short[] mSubnetmask;
    private IPv4  mLanIp;
    private final Integer _rxPort = 49168;
    private final Integer _txPort = 49167;
    private final DatagramSocket _rxSock;
    private final DatagramSocket _txSock;
    private ByteArrayInputStream bis;
    private ObjectInputStream ois;
    private ByteArrayOutputStream bos ;
    private ObjectOutputStream oos;
    private boolean running ;
    private  HashMap<String,IPv4> leasesTable;
    private  Map<String, Boolean> ipPool;
    private byte[] buff = new byte[1024];
    
    public DhcpServer(IPv4 lanIp, short[] subnetmask, short[] ipstart, short[] ipstop) throws SocketException, IOException{
        mLanIp = lanIp;
        _rxSock = new DatagramSocket(_rxPort);
        _txSock = new DatagramSocket(_txPort);
        leasesTable = new HashMap();
        //verifca daca ipstart si ipstop sunt din acelsi subnet
        ipPool = new TreeMap();
        for(short s = ipstart[3]; s <= ipstop[3]; s++){
            short[] newIp = new short[]{ipstart[0], ipstart[1], ipstart[2], s};
            ipPool.put(toString(newIp), true);//true inseamna liber de utilizat
        }
        mSubnetmask = subnetmask;
        
        //pentru serializare si deserializare
        bis = new ByteArrayInputStream(buff);
        ois = new ObjectInputStream(bis);
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
    }
    
    private String toString(short[] X){
        String _mys = "";
        for(short x : X){
            _mys += String.valueOf(x) + ".";
        }
        return _mys.substring(0, _mys.length()-1);
    }
    
    private short[] toShortArr(String X){
       short[] x = new short[4];
       String[] arr = X.split(".");
       x[0] = Short.parseShort(arr[0]);
       x[1] = Short.parseShort(arr[1]);
       x[2] = Short.parseShort(arr[2]);
       x[3] = Short.parseShort(arr[3]);
       return x;
    }
    
    public String getTheMAC(EthPort eth){
        return eth.getMAC();
    }
      
    boolean checkLeasesTable(IPv4 dIP){
        boolean check = false;
        Set set = leasesTable.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()){
            HashMap.Entry lT = (HashMap.Entry)it.next();
            
            IPv4 lTIP = (IPv4)lT.getValue();
            if(lTIP.toStringIP().equals(dIP.toStringIP())){
                //am gasit deja IP-ul cerut
                check = true; 
                break;
            }
        }        
        return check;
    }
    
    IPv4 findFirstIPValid(){
        IPv4 valid = null;
        Set setIP = ipPool.entrySet();
        Iterator it = setIP.iterator();
        while(it.hasNext()){
            Map.Entry ip = (Map.Entry)it.next();
            if(ip.getValue().equals(true)){
                valid = new IPv4(toShortArr((String) ip.getKey()), mSubnetmask);
            }
        }
        return valid;
    } 
    
    
    public void run(){
        running = true;
        while(running){
            DatagramPacket packet = new DatagramPacket(buff, buff.length);
            try {
                //Citeste de pe PORTUL 68 de la client (DHCPDISCOVER, DHCPREQUEST)
                _rxSock.receive(packet);
                //TODO: memoreaza intr-o strucutra Porturile Clientilor
                Object deserializedObject = ois.readObject(); 
                //IPv4 allocatedIP  = null;
                IPv4 validIP = null;
                if (deserializedObject instanceof DhcpDiscover ) {
                    DhcpDiscover discovered = (DhcpDiscover) deserializedObject;
                    // genereaza DHPCPOFFER
                    DhcpOffer dhcpmessage = null;
                    if(discovered.getIP() != null){
                        IPv4 dip = discovered.getIP();
                        boolean isInLeases = checkLeasesTable(dip);
                        if(isInLeases){
                            //DHCPOFFER CU IP valid;
                            validIP = findFirstIPValid();
                            
                            dhcpmessage = new DhcpOffer(discovered.getClientMac(), validIP, mLanIp);
                           
                        }else{
                            validIP = dip;
                            //DHCPOFFER cu IP-ul cerut de clientS
                            dhcpmessage = new DhcpOffer(discovered.getClientMac(), dip, mLanIp);
                        }
                    }
                      // Serializarea obiectului în fluxul de bytes
                      oos.writeObject(dhcpmessage);
                      oos.flush();
                      buff = bos.toByteArray();

                     // send DHCPOFFER pe port 67 
                     DatagramPacket sendPacketOffer = new DatagramPacket(buff, buff.length, packet.getAddress(), packet.getPort());
                     _txSock.send(sendPacketOffer);
                }else if(deserializedObject instanceof DhcpRequest){
                    //inainte e ACK OK -> pune in leasesTable ce s-a oferit;marcheaza in ipPool ca acel ip e folosit
                    if(validIP != null){
                        leasesTable.put(validIP.toStringIP(), validIP);
                        ipPool.put(validIP.toStringIP() , false);
                    }else{
                        System.err.println("validIP=NULL, DEBUG!");
                    }
                    // genereaza DHCPACK
                    DhcpAck dhcpmessage = new DhcpAck();
                    //Serializarea obiectului în fluxul de bytes
                    oos.writeObject(dhcpmessage);
                    oos.flush();
                    buff = bos.toByteArray();
                    // send pe portul 67
                    DatagramPacket sendPacketAck = new DatagramPacket(buff, buff.length, packet.getAddress(), packet.getPort());
                    _txSock.send(sendPacketAck);
                }
                //TODO: handle dhcp decline (mai tarziu) (trebuie implementare ARP
            } catch (IOException ex) {
                Logger.getLogger(DhcpServer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DhcpServer.class.getName()).log(Level.SEVERE, null, ex);
            }
              
        }
        
    }
    
    //TODO: "destructor" unde inchizi ti bis, ois, bos, oos 
}
