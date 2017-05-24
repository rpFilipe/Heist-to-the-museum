/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

import java.rmi.RemoteException;
import structures.VectorClock;

/**
 *
 * @author ricardo
 */
public interface ImonitorsGeneralRepository {

    /**
     * Remove all Ordinary Thieves of the Party.
     * @param partyId id of the Assault Party.
     */
    public VectorClock clearParty(int partyId, VectorClock vc);

    /**
     * Save the number of stolen canvas in the General Repository.
     * @param toalCanvas Number of Canvas Stolen.
     */
    public VectorClock setCollectedCanvas(int toalCanvas, VectorClock vc);

    /**
     * Set a Ordinary Thief in a specific Assault Party. 
     * @param partyId Id of the Assault Party.
     * @param thiefId Id of the Ordinary Thief.
     * @param elemId Member in said Assault Party.
     */
    public VectorClock setPartyElement(int partyId, int thiefId, int elemId, VectorClock vc);

    /**
     * Save the Room characteristics in the General Repository.
     * @param roomId Id of the Room.
     * @param distance Distance of the Concentration Site to the Room.
     * @param paitings Number of paintings in the Room.
     */
    public VectorClock setRoomAtributes(int roomId, int distance, int paitings, VectorClock vc);

    /**
     * Update Ordinary Thief position in the General Repository.
     * @param thiefId Id of the Ordinary Thief.
     * @param position Current position of the Ordinary Thief
     */
    public VectorClock updateThiefPosition(int thiefId, int position, VectorClock vc);

    /**
     * Update Ordinary Thief situation in the General Repository.
     * @param thiefId Id of the Ordinary Thief.
     * @param situation Value to set.
     */
    public VectorClock updateThiefSituation(int thiefId, char situation, VectorClock vc);

    /**
     * Set the target Room of an Assault Party.
     * @param partyId Id of the Party.
     * @param room Id of the Room.
     */
    public VectorClock setRoomIdAP(int partyId,int room, VectorClock vc);

    /**
     * Set the number of paintings in a Room.
     * @param id Id of the Room.
     * @param paitings Number of paintings.
     */
    public VectorClock setRoomCanvas(int id, int paitings, VectorClock vc);
}
