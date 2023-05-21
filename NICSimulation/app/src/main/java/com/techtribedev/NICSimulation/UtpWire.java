/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

/**
 *
 * @author asimion
 */
public class UtpWire {
   private EthPort portLeft;//maybe create a class RJ45
   private EthPort portRight;
   private boolean bothPhyConnected;
   
   public UtpWire(){
       portLeft = null;
       portRight = null;
       bothPhyConnected = false;
   }  
   
   public UtpWire(EthPort left, EthPort right){
     portLeft = left;
     portRight = right;
     if(portLeft != null && portRight != null){
         bothPhyConnected = true;
     }  
     
   }

   public void setPortLeft(EthPort port){
     portLeft = port;
   }

   public void setPortRight(EthPort port){
     portRight = port;
   }
   
   public boolean checkConnectionCable(){
       return bothPhyConnected;
   } 
     
   public EthPort getPortLeft() { return portLeft; }

   public EthPort getPortRight(){ return portRight; } 
   
   public int connect(){
    //test cablu daca este conectat in ambele capete
    boolean ret = this.checkConnectionCable();
    int errorCode =  404;//eroare
    if(ret){
        //set link UP pe fiecare interfata;
        EthPort lp = this.getPortLeft();
        EthPort rp = this.getPortRight();
        lp.setLinkUp();
        rp.setLinkUp();
        errorCode = 200;//ok
    }
    return errorCode;
  }
         
   
}
