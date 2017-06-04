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
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface IotMuseum {

    /**
     * This method is used to get a canvas from a room.
     * @param roomId room Identifier
     * @param vc VectorClock.
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Boolean> rollACanvas(int roomId, VectorClock vc)throws RemoteException,InterruptedException;
}