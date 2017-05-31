/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import java.rmi.RemoteException;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 */
public interface IotAssaultParty {

    /**
     * Method to Thieves crawl from the Concentration Site to the Museum interior.
     * @param id of the Ordinary Thief that invoked the method.
     * @param vc
     * @return 
     */
    public VectorClock crawlIn(int id, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to Thieves crawl from the Museum to the Concentration Site.
     * @param id of the Ordinary Thief that invoked the method.
     * @param vc
     * @return 
     */
    public VectorClock crawlOut(int id, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to add a Ordinary Thief joins this Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     * @param speed maximum crawling distance per step.
     * @param vc
     * @return 
     */
    public VectorClock joinParty(int id, int speed, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to change the direction in crawling.
     * @param vc
     * @return 
     */
    public VectorClock reverseDirection(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Get the current target room assigned to this Assault Party.
     * @param vc
     * @return Room id
     */
    public Pair< VectorClock, Integer> getTargetRoom(VectorClock vc) throws RemoteException,InterruptedException;
}
