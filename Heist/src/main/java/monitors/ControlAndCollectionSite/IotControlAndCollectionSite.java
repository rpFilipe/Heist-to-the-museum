/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

import java.rmi.RemoteException;
import structures.VectorClock;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface IotControlAndCollectionSite {

    /**
     * Method used to give the content of the canvas cylinder to the Master Thief.
     * @param id identifier
     * @param canvas canvas
     * @param roomId Id of the Room that was being target.
     * @param partyId Id of the Assault Party that Ordinary Thief belonged to.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock handACanvas(int id, boolean canvas, int roomId, int partyId, VectorClock vc) throws RemoteException,InterruptedException;
    
}
