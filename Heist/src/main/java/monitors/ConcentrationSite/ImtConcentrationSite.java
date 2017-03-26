/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ConcentrationSite;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface ImtConcentrationSite {

    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     */
    public void startOperations();

    /**
     * Get the number of Ordinary Thieves that are idling.
     * @return number of Ordinary Thieves waiting.
     */
    public int getNumberThivesWaiting();

    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     */
    public void prepareAssaultParty(int partyId);

    /**
     * Notify that the assault has ended.
     */
    public void sumUpResults(); 
}
