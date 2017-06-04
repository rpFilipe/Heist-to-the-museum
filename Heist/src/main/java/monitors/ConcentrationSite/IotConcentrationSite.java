/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ConcentrationSite;

import java.rmi.RemoteException;
import structures.Pair;
import structures.VectorClock;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface IotConcentrationSite {

    /**
     * Method to check if the Ordinary Thief id needed.
     * @param id of the Ordinary Thief
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */ 
    public Pair<VectorClock, Boolean> amINeeded(int id, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Get the id of the Assault Party associated with a Ordinary Thief.
     * @param thiefId Id of the Thief.
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Integer> getPartyId(int thiefId, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to signal thar the Ordinary Thief has joined the Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock prepareExcursion(int id, VectorClock vc) throws RemoteException,InterruptedException;
}