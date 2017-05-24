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
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface ImtConcentrationSite {

    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     */
    public VectorClock startOperations(VectorClock vc) throws RemoteException;

    /**
     * Get the number of Ordinary Thieves that are idling.
     * @return number of Ordinary Thieves waiting.
     */
    //public int getNumberThivesWaiting();

    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     */
    public VectorClock prepareAssaultParty(int partyId, VectorClock vc) throws RemoteException;

    /**
     * Notify that the assault has ended.
     */
    public VectorClock sumUpResults(VectorClock vc) throws RemoteException;
    
    /**
     * Evaluates the situation and blocks if needed.
     * @param isHeistCompleted has the heist been successful.
     * @param isWaitingNedded does the MasterThief have to wait for a group arrival.
     * @return Master Thief next State
     */
    public Pair<VectorClock, Integer> appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded, VectorClock vc) throws RemoteException;
}
