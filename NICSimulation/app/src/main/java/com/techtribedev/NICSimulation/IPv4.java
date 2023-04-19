/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

/**
 *
 * @author asimion
 */
public class IPv4 {
    private short[] mIp;
    private short[] mMask;
    
    public IPv4(short[] ip, short[] mask){
       mIp = ip;
       mMask = mask;
    }
    
    public void setIp(short[] ip){
        mIp = ip;
    }
    
    public void setMask(short[] mask){
        mMask = mask;
    }
    
    public short[] getIp(){
        return mIp;
    }
   
    public short[] getMask(){
        return mMask;
    }
    
    public String toStringIP(){
        String _mys = "";
        for(short sip : mIp){
            _mys += String.valueOf(sip) + ".";
        }
        return _mys.substring(0, _mys.length()-1);
    }

    public String toStringMask(){
        String _mym = "";
        for(short sm : mMask){
            _mym += String.valueOf(sm) + ".";
        }
        return _mym.substring(0, _mym.length()-1);
    }
}
