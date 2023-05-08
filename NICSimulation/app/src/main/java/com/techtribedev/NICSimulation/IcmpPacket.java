/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

import java.io.Serializable;
import java.util.Random;

/**
 *
 * @author asimion
 */
public class IcmpPacket implements Serializable{
    private static final Byte ECHO_REQUEST            = (byte) 8;
    private static final Byte ECHO_REPLY              = (byte) 0;
    private static final Byte DESTINATION_UNREACHABLE = (byte) 3;
    
    private final Byte  mType;
    private Byte  mCode;
    //TODO: mechanism checksum
    //private Short mChecksum;
    private final Short mId;
    private Short mSeq;
    private final Byte  mPayload[];
    
    public IcmpPacket(Short identif, Byte type){
        mId = identif;
        mType = type;
        mPayload = generatePayload();
        if (mType.equals(ECHO_REQUEST)){
            mCode = (byte) 0;
        } else if (mType.equals(ECHO_REPLY)){
            mCode = (byte) 0;
        } else if(mType.equals(DESTINATION_UNREACHABLE)) {
            //TODO: DESTINATION_UNREACHABLE
            DebugMode.log("ICPM Packet Dest Unreachable");
        }
    }
    
    private Byte[] generatePayload(){
        Random rand = new Random();
        Integer lenPayload = rand.nextInt(1024 - 32 + 1) + 32;
        Byte[] payload = new Byte[lenPayload];
        for(int i = 0; i < lenPayload; i++){
            payload[i] = (byte) 0;
        }
        return payload;
    }
}
