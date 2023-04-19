/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.Serializable;

/**
 *
 * @author asimion
 */
public class DhcpAck implements Serializable{
    private final String mACK = "OKACK";//un mesaj de final
   
    public DhcpAck(){
    
    }
    
    public String getResponse(){
        return mACK;
    }
}
