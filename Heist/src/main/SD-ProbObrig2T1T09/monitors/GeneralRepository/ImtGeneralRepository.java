/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

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
    public void updateMThiefState(int state);

    /**
     * Method to finalize the log.
     */
    public void FinalizeLog();
    
}
