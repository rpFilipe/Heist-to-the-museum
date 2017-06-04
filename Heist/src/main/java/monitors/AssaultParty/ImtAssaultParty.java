/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import java.rmi.RemoteException;
import structures.VectorClock;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface ImtAssaultParty {

    /**
     * Method to set the target room parameters to this Assault Party.
     * @param id id of the Room.
     * @param distance from the Concentration Site to the Room.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock setRoom(int id, int distance, VectorClock vc) throws RemoteException,InterruptedException; 

    /**
     * Method to signal the first Ordinary Thief that joined the party party to start crawling inwards.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock sendAssaultParty(VectorClock vc) throws RemoteException,InterruptedException;
}
