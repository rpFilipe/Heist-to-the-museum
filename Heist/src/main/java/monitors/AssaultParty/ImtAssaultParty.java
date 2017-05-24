/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 */
public interface ImtAssaultParty {

    /**
     *  Method to set the target room parameters to this Assault Party.
     * @param id id of the Room.
     * @param distance from the Concentration Site to the Room.
     * @param vc
     * @return 
     */
    public VectorClock setRoom(int id, int distance, VectorClock vc);

    /**
     * Method to signal the first Ordinary Thief that joined the party party to start crawling inwards.
     * @param vc
     * @return 
     */
    public VectorClock sendAssaultParty(VectorClock vc);
}
