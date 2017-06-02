/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structures;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Filipe
 */
public class VectorClock implements Cloneable, Serializable {

    private int[] clocks;
    private int clockPosition;
    private int returnIntValue;
    private boolean returnBoolValue;

    public VectorClock(int clkSize, int clkPosition) {
        clocks = new int[clkSize];
        clockPosition = clkPosition;
    }
    
    public VectorClock(VectorClock toCopy){
        this.clocks = new int[toCopy.clocks.length];
        System.arraycopy(toCopy.clocks, 0, this.clocks, 0, this.clocks.length);
        this.clockPosition = toCopy.clockPosition;
    }
        
    public VectorClock incrementClock(){
        clocks[clockPosition]++;
        return new VectorClock(this);
    }

    public void update(VectorClock vc) {
        for (int i = 0; i < clocks.length; i++) {
            clocks[i] = Math.max(vc.clocks[i], clocks[i]);
        }
    }

    public void setClocks(int[] clocks) {
        this.clocks = clocks;
    }

    public int getReturnIntValue() {
        return returnIntValue;
    }

    public void setReturnIntValue(int returnValue) {
        this.returnIntValue = returnValue;
    }

    public boolean getreturnBoolValue() {
        return returnBoolValue;
    }

    public synchronized void setReturnBoolValue(boolean returnBoolValue) {
        this.returnBoolValue = returnBoolValue;
    }
    
    @Override
    public VectorClock clone() {
        try {
            return (VectorClock) super.clone();
        } catch (CloneNotSupportedException ex) {
            Logger.getLogger(VectorClock.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    return null;
    }
}
