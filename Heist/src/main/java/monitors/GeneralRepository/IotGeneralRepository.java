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
public interface IotGeneralRepository {

    /**
     * Method to add the Ordinary Thief attributes in the General Repository.
     * @param thiefId Id of the Thief. 
     * @param speed Maximum displacement of the Thief.
     */
    public void addThief(int thiefId, int speed);

    /**
     * Update the State of the Ordinary Thief and print the updated Status in the log file.
     * @param thiefId Id of the Ordinary Thief 
     * @param state updated State
     */
    public void updateThiefState(int thiefId, int state);

    /**
     * Method to update the contents of a Ordinary Thief cylinder in the General Repository.
     * @param thiefId Id of the thief that invoked the method.
     * @param hasCanvas the state of the thief cylinder.
     */
    public void updateThiefCylinder(int thiefId, boolean hasCanvas);

    /**
     * Method to update state the Thief's situation in the General Repository.
     * @param thiefId Id of the Thief.
     * @param situation Current situation.
     */
    public void updateThiefSituation(int thiefId, char situation);
    
}
