/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

import java.rmi.RemoteException;
import structures.VectorClock;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface ImtGeneralRepository {

    /**
     * Method to update state the Master Thief in the General Repository.
     * @param state Current state the Master Thief.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock updateMThiefState(int state, VectorClock vc) throws RemoteException,InterruptedException;

    /**
     * Method to finalize the log.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    public VectorClock FinalizeLog(VectorClock vc) throws RemoteException,InterruptedException;
    
}
