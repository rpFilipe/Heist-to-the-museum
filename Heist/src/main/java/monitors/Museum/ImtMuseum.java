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
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface ImtMuseum {

    /**
     * This method returns the distance of a room to the outside.
     * @param targetRoom of the room
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Integer> getRoomDistance(int targetRoom, VectorClock vc)throws RemoteException,InterruptedException;
}