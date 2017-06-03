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
public class VectorClock implements Cloneable, Serializable, Comparable {

    private int[] clocks;
    private int clockPosition;
    private String text;

    public VectorClock(int clkSize, int clkPosition) {
        clocks = new int[clkSize];
        clockPosition = clkPosition;
    }
    
    public VectorClock(VectorClock toCopy){
        this.clocks = new int[toCopy.clocks.length];
        System.arraycopy(toCopy.clocks, 0, this.clocks, 0, this.clocks.length);
        this.clockPosition = toCopy.clockPosition;
    }
    
    /*
    For order
    */
    public VectorClock(String text, int[] clocks){
        this.text = text;
        this.clocks = clocks;
    }
        
    public synchronized VectorClock incrementClock(){
        clocks[clockPosition]++;
        return new VectorClock(this);
    }

    public synchronized void update(VectorClock vc) {
        for (int i = 0; i < clocks.length; i++) {
            clocks[i] = Math.max(vc.clocks[i], clocks[i]);
        }
    }

    public synchronized int[] getClocks() {
        return this.clocks;
    }
    
    public String getText(){
        return text;
    }

    public void setClocks(int[] clocks) {
        this.clocks = clocks;
    }
        
    @Override
    public int compareTo(Object other) {
        int[] otherClocks = ((VectorClock) other).clocks;

        int lessOrEqualCount = 0;
        boolean hasLessThan = false;
        boolean hasGreaterThan = false;

        for (int i = 0; i < clocks.length; i++) {
            if (clocks[i] <= otherClocks[i]) {
                lessOrEqualCount++;
                if (clocks[i] < otherClocks[i]) hasLessThan = true;
            } else {
                hasGreaterThan = true;
            }
        }

        if (lessOrEqualCount == clocks.length && hasLessThan) {
            return -1;
        } else if (hasLessThan && hasGreaterThan) {
            return 0;
        } else {
            return 1;
        }
    }
    
    /*
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
    */
}
