/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.Museum;

import java.rmi.RemoteException;
import structures.VectorClock;
import structures.Pair;

/**
 *
 * @author Ricardo Filipe
 */
public interface ImtMuseum {

    /**
     * This method returns the distance of a room to the outside.
     * @param targetRoom of the room
     * @return
     */
    public Pair<VectorClock, Integer> getRoomDistance(int targetRoom, VectorClock vc)throws RemoteException,InterruptedException;
    
}
