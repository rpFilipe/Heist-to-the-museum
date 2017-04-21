/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

/**
 *
 * @author ricardo
 */
public interface ImonitorsGeneralRepository {
    public void clearParty(int partyId);
    public void setCollectedCanvas(int toalCanvas);
    public void setPartyElement(int partyId, int thiefId, int elemId);
    public void setRoomAtributes(int roomId, int distance, int paitings);
    public void updateThiefPosition(int thiefId, int position);
    public void updateThiefSituation(int thiefId, char situation);
    public void setRoomIdAP(int partyId,int room);
    public void setRoomCanvas(int id, int paitings);
}
