/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

import java.rmi.RemoteException;
import structures.Pair;
import structures.VectorClock;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface ImtControlAndCollectionSite {
    /**
     * Method to get the Assault Party to be deployed.. 
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Integer> getTargetRoom(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to get the Assault Party to be deployed.. 
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Integer> getPartyToDeploy(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to send the Master Thief to a idle state waiting for a Ordinary
     * Thief to return from the Museum.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock takeARest(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to get the canvas from the Ordinary Thief.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock collectCanvas(VectorClock vc) throws RemoteException,InterruptedException;
    
    /**
     * Check if all the Rooms have been cleared.
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Boolean> isHeistCompleted(VectorClock vc) throws RemoteException,InterruptedException;
    
    /**
     * Check if all the MasterThief can rest for a bit.
     * @param vc VectorClock.
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Boolean> waitingNedded(VectorClock vc) throws RemoteException,InterruptedException;   
}