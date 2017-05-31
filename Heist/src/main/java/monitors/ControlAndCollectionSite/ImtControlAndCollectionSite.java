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
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface ImtControlAndCollectionSite {
    /**
     * Method to get the Assault Party to be deployed.. 
     * @return Id of the Assault Party to be prepared.
     */
    public Pair<VectorClock, Integer> getTargetRoom(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to get the Assault Party to be deployed.. 
     * @return Id of the Assault Party to be prepared.
     */
    public Pair<VectorClock, Integer> getPartyToDeploy(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to send the Master Thief to a idle state waiting for a Ordinary
     * Thief to return from the Museum.
     */
    public VectorClock takeARest(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to get the canvas from the Ordinary Thief.
     */
    public VectorClock collectCanvas(VectorClock vc) throws RemoteException,InterruptedException;
    
    /**
     * Check if all the Rooms have been cleared.
     * @return heist completed
     */
    public Pair<VectorClock, Boolean> isHeistCompleted(VectorClock vc) throws RemoteException,InterruptedException;
    
    /**
     * Check if all the MasterThief can rest for a bit.
     * @return MasterThief has to wait
     */
    public Pair<VectorClock, Boolean> waitingNedded(VectorClock vc) throws RemoteException,InterruptedException;
    
}
