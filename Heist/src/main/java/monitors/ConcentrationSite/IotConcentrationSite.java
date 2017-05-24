/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ConcentrationSite;

import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public interface IotConcentrationSite {

    /**
     * Method to check if the Ordinary Thief id needed.
     * @param id of the Ordinary Thief.
     * @return is the Ordinary Thief needed.
     */
    public Pair<VectorClock, Boolean> amINeeded(int id, VectorClock vc);

    /**
     * Get the id of the Assault Party associated with a Ordinary Thief.
     * @param thiefId Id of the Thief.
     * @return the Assault Party id.
     */
    public Pair<VectorClock, Integer> getPartyId(int thiefId, VectorClock vc);

    /**
     * Method to signal thar the Ordinary Thief has joined the Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     */
    public VectorClock prepareExcursion(int id, VectorClock vc);
}
