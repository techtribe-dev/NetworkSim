/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asimion
 */
public class DhcpServer extends Thread {
    private short[] mSubnetmask;
    private IPv4    mLanIp;//folosit pt a fi trimis ca ruta default in DhcpOffer
    private final Integer _rxPort = 49168;
    private final Integer _txPort = 49167;
    private final DatagramSocket  _rxSock;
    private final DatagramSocket  _txSock;
    private InputStream  bis;
    private ObjectInputStream     ois;
    private ByteArrayOutputStream bos;
    private ObjectOutputStream    oos;
    private boolean running ;
    private HashMap<String,IPv4>      leasesTable;
    private Map<String, Boolean>      ipPool;
    private HashMap<String, Integer> _rxClientMapPort; //<MAC, PORT>
    private HashMap<String, Integer> _txClientMapPort; //<MAC, PORT>
    private Semaphore serverSemaphore;
    private byte[] buff;
    
    public DhcpServer(IPv4 lanIp, short[] subnetmask, short[] ipstart, short[] ipstop, Semaphore sem ) throws SocketException, IOException{
        mLanIp = lanIp;
        _rxSock = new DatagramSocket(_rxPort);
        _txSock = new DatagramSocket(_txPort);
        leasesTable = new HashMap();
        //TODO: verifca daca ipstart si ipstop sunt din acelsi subnet
        ipPool = new TreeMap();
        for(short s = ipstart[3]; s <= ipstop[3]; s++){
            short[] newIp = new short[]{ipstart[0], ipstart[1], ipstart[2], s};
            ipPool.put(toString(newIp), true);//true inseamna liber de utilizat
        }
        mSubnetmask = subnetmask;
        
        //Monitorizare Porturi Clienti
        _rxClientMapPort = new HashMap();
        _txClientMapPort = new HashMap();
        
        //pentru serializare si deserializare
        buff = new byte[1024];
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        serverSemaphore = sem;
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
       String[] arr = X.split("\\.");
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
    
    
    public synchronized void run(){
        System.out.println("->Start Server!");
        running = true;
      
        while(running){
            System.out.println("#Server:....");
            try {
                byte[] buffdisc = new byte[1024];
                
                //Ascult DHCPDISCOVER
                serverSemaphore.release();
                DatagramPacket recvDiscover = new DatagramPacket(buffdisc, buffdisc.length);
                Object objRecv = null;
                while(objRecv == null){
                  _rxSock.receive(recvDiscover);//ascult                 
                  bis = new ByteArrayInputStream(recvDiscover.getData());
                  ois = new ObjectInputStream(bis);
                  objRecv = ois.readObject();
                }
                System.out.println("*Server: primit DHCPDISCOVER " + objRecv.toString());
                Integer __txClientPort = recvDiscover.getPort();
                
                //Generez DHCPOFFER si TRIMIT
                IPv4 validIP = null;
                if (objRecv instanceof DhcpDiscover ) {
                    System.out.println("*Server:Create dhcpOffer ");
                    DhcpDiscover dhcpDiscover = (DhcpDiscover) objRecv;
                    // genereaza DHPCPOFFER
                    DhcpOffer dhcpmessage = null;
                    if(dhcpDiscover.getIP() != null){
                        System.out.println("*Server: DhcpDiscovered IP NOT NULL");
                        IPv4 dip = dhcpDiscover.getIP();
                        boolean isInLeases = checkLeasesTable(dip);
                        if(isInLeases){
                            //DHCPOFFER CU IP valid;
                            validIP = findFirstIPValid();
                            dhcpmessage = new DhcpOffer(dhcpDiscover.getClientMac(), validIP, mLanIp);
                            System.out.println("*    Server: am gasit un Ip Valid pt a crea dhcp offer");
                        } else {
                            validIP = dip;
                            //DHCPOFFER cu IP-ul cerut de client
                            dhcpmessage = new DhcpOffer(dhcpDiscover.getClientMac(), dip, mLanIp);
                            System.out.println("174*    Server: ip-ul cerut in discover este bun! Il trimit in dhcp offer!");
                        }
                    } else {
                        System.out.println("*Server: DhcpDiscovered IP NULL");
                        //DHCPOFFER CU IP valid;
                        validIP = findFirstIPValid();
                        dhcpmessage = new DhcpOffer(dhcpDiscover.getClientMac(), validIP, mLanIp);
                        System.out.println("*    Server: am gasit un Ip Valid pt a crea dhcp offer");                        
                    }
                      // Serializarea obiectului în fluxul de bytes
                      oos.writeObject(dhcpmessage);
                      oos.flush();
                      byte[] buffoffer = bos.toByteArray();
                     // send DHCPOFFER
                     DatagramPacket sendPacketOffer = new DatagramPacket(buffoffer, buffoffer.length, recvDiscover.getAddress(), __txClientPort-1);
                     _txSock.send(sendPacketOffer);
                     System.out.println("190*Server: Trimis DHCPOFFER pe " + (__txClientPort-1));

                     // mem rxClientPort
                     _rxClientMapPort.put(dhcpDiscover.getClientMac(), __txClientPort-1);
                     //deserializedObject = null;
                }
                
                
                //ASCULT DHCPREQUES
                serverSemaphore.release();
                byte[] buffreq = new byte[1024];
                DatagramPacket recvRequest = new DatagramPacket(buffreq, buffreq.length);
                objRecv = null;
                
                while(objRecv == null){
                  _rxSock.receive(recvRequest);//ascult
                  bis = new ByteArrayInputStream(recvRequest.getData());
                  ois = new ObjectInputStream(bis);
                  objRecv = ois.readObject();
                }
                System.out.println("208*Server: primit DHCPREQUEST " + objRecv.toString());
                __txClientPort = recvRequest.getPort();

                validIP = null;
                if(objRecv instanceof DhcpRequest){
                    //inainte e ACK OK -> pune in leasesTable ce s-a oferit;marcheaza in ipPool ca acel ip e folosit
                    DhcpRequest dhcpRequest = (DhcpRequest) objRecv;
                    validIP = dhcpRequest.getCIP();
                    if(validIP != null){
                        leasesTable.put(validIP.toStringIP(), validIP);
                        ipPool.put(validIP.toStringIP() , false);
                    }else{
                        System.err.println("$$ validIP=NULL, DEBUG!");
                    }
                    // genereaza DHCPACK
                    DhcpAck dhcpAck = new DhcpAck();
                    
                    //inchid bos si oos vechi
                    bos.close();
                    oos.close();
                    //Pt serializarea obiectului în fluxul de bytes dechid noi bos si oos
                    bos = new ByteArrayOutputStream();
                    oos = new ObjectOutputStream(bos);
                    oos.writeObject(dhcpAck);
                    oos.flush();
                    byte[] buffack = bos.toByteArray();
                    // send pe portul txServer catre portul rxClient
                    DatagramPacket sendAck = new DatagramPacket(buffack, buffack.length, recvRequest.getAddress(), __txClientPort-1);
                    _txSock.send(sendAck);
                    //deserializedObject = null;
                    running = false;
                } 
                
                //TODO: handle dhcp decline (mai tarziu) (trebuie implementare ARP
            } catch (IOException | ClassNotFoundException ex) {
                Logger.getLogger(DhcpServer.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        _rxSock.close();
        _txSock.close();
    }
    
    //TODO: "destructor" unde inchizi ti bis, ois, bos, oos 
}
