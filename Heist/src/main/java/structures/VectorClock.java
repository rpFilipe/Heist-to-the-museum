/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package structures;

import java.io.Serializable;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public class VectorClock implements Cloneable, Serializable, Comparable {

    private int[] clocks;
    private int clockPosition;
    private String text;

    /**
     * Constructor1.
     * @param clkSize clock size
     * @param clkPosition clock position
     */
    public VectorClock(int clkSize, int clkPosition) {
        clocks = new int[clkSize];
        clockPosition = clkPosition;
    }
    
    /**
     * Constructor2.
     * @param toCopy vectorclock to copy
     */
    public VectorClock(VectorClock toCopy){
        this.clocks = new int[toCopy.clocks.length];
        System.arraycopy(toCopy.clocks, 0, this.clocks, 0, this.clocks.length);
        this.clockPosition = toCopy.clockPosition;
    }
    
    /**
    Constructor used for order.
     * @param text clocks on string
     * @param clocks clocks
    */
    public VectorClock(String text, int[] clocks){
        this.text = text;
        this.clocks = clocks;
    }
        
    /**
     * Method that increments the clock and returns it.
     * @return VectorClock.
     */
    public synchronized VectorClock incrementClock(){
        clocks[clockPosition]++;
        return new VectorClock(this);
    }

    /**
     * Method that updates the clock.
     * @param vc VectorClock.
     */
    public synchronized void update(VectorClock vc) {
        for (int i = 0; i < clocks.length; i++) {
            clocks[i] = Math.max(vc.clocks[i], clocks[i]);
        }
    }

    /**
     * Method that returns the clock.
     * @return clocks
     */
    public synchronized int[] getClocks() {
        return this.clocks;
    }
    
    /**
     * Method that returns a string representing the clocks.
     * @return text
     */
    public String getText(){
        return text;
    }

    /**
     * Method that sets the clock.
     * @param clocks clocks
     */
    public void setClocks(int[] clocks) {
        this.clocks = clocks;
    }
        
    /**
     * Method that compares clocks.
     * @param other other clock
     * @return integer 
     */
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
}