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
public interface ImtConcentrationSite {

    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock startOperations(VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock prepareAssaultParty(int partyId, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Notify that the assault has ended.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock sumUpResults(VectorClock vc) throws RemoteException,InterruptedException;
    
    /**
     * Evaluates the situation and blocks if needed.
     * @param isHeistCompleted has the heist been successful.
     * @param isWaitingNedded does the MasterThief have to wait for a group arrival.
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public Pair<VectorClock, Integer> appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded, VectorClock vc) throws RemoteException,InterruptedException;
}