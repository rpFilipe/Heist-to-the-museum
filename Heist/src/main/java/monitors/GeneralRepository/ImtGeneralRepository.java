/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface ImtGeneralRepository {

    /**
     * Method to update state the Master Thief in the General Repository.
     * @param state Current state the Master Thief.
     */
    public VectorClock updateMThiefState(int state, VectorClock vc);

    /**
     * Method to finalize the log.
     */
    public VectorClock FinalizeLog(VectorClock vc);
    
}
