/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.techtribedev.NICSimulation;

/**
 *
 * @author asimion
 */
public class DebugMode {
    public static boolean isDebugModeEnabled ;
    
    public static void log(String msg){
        if(isDebugModeEnabled) System.out.println("[DEBUG] : " + msg);
    }
}
