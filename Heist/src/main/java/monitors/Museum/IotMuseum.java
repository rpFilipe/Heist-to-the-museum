/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.Museum;

import java.rmi.RemoteException;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface IotMuseum {

    /**
     * This method is used to get a canvas from a room.
     * @param roomId
     * @return true if the room still has canvas to be stolen.
     */
    public Pair<VectorClock, Boolean> rollACanvas(int roomId, VectorClock vc)throws RemoteException,InterruptedException;
    
}
