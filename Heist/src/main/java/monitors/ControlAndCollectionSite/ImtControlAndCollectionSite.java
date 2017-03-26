/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface ImtControlAndCollectionSite {
    /**
     * Method to get the Assault Party to be deployed.. 
     * @return Id of the Assault Party to be prepared.
     */
    public int getTargetRoom();

    /**
     * Method to get the Assault Party to be deployed.. 
     * @return Id of the Assault Party to be prepared.
     */
    public int getPartyToDeploy();

    /**
     * Method to send the Master Thief to a idle state waiting for a Ordinary
     * Thief to return from the Museum.
     */
    public void takeARest();

    /**
     * Method to get the canvas from the Ordinary Thief.
     */
    public void collectCanvas();

    /**
     * Evaluates the situation and decide what to do next.
     * @return Master Thief next state
     */
    public int appraiseSit();
    
}
