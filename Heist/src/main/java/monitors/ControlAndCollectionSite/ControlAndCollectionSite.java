/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

import States.MasterThiefStates;
import States.PartyStates;
import States.RoomStates;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Constants;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class ControlAndCollectionSite implements IotControlAndCollectionSite, ImtControlAndCollectionSite{

    private int canvasCollected;
    private int canvasToCollect;
    private int[] roomStates;
    private int[] partyStates;
    private boolean masterThiefBusy;
    private boolean thiefArrived;
    private int targetRoom;
    private int partyToDeploy;
    private int[] partyArrivedThiefs;
    private boolean partyFree;
    private int thievesWaiting;
    private GeneralRepository genRepo;

    /**
     * Create a Control And Collection Instance.
     * @param genRepo
     */
    public ControlAndCollectionSite(GeneralRepository genRepo) {
        roomStates = new int[Constants.N_ROOMS];
        partyFree = true;
        partyArrivedThiefs = new int[Constants.N_ASSAULT_PARTIES];
        canvasToCollect = 0;
        canvasCollected = 0;
        thievesWaiting = 0;
        partyStates = new int[Constants.N_ASSAULT_PARTIES];
        masterThiefBusy = false;
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            roomStates[i] = RoomStates.NOT_VISITED;
        }
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            partyStates[i] = PartyStates.EMPTY;
        }
        thiefArrived = false;
        targetRoom = 0;
        this.genRepo = genRepo;
    }

    /**
     * Method to send the Master Thief to a idle state waiting for a Ordinary
     * Thief to return from the Museum.
     */
    @Override
    public synchronized void takeARest() {
        masterThiefBusy = false;
        notifyAll();

        while (!thiefArrived) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        thiefArrived = false;
    }

    /**
     * Method to get the canvas from the Ordinary Thief.
     */
    @Override
    public synchronized void collectCanvas() {
        //canvasToCollect--;
    }

    /**
     * Method used to give the content of the canvas cylinder to the Master Thief.
     * @param thiefId Id of the Thief that invoked the method.
     * @param hasCanvas Content of the cylinder.
     * @param roomId Id of the Room that was being target.
     * @param partyId Id of the Assault Party that Ordinary Thief belonged to.
     */
    @Override
    public synchronized void handACanvas(int thiefId, boolean hasCanvas, int roomId, int partyId) {
        //canvasToCollect++;

        while (masterThiefBusy) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error hansAcanvas");
            }
        }
        
        if(hasCanvas)
            canvasCollected++;
            
        
        if (!hasCanvas) 
            roomStates[roomId] = RoomStates.ROOM_EMPTY;
        else if (roomStates[roomId] != RoomStates.ROOM_EMPTY) 
            roomStates[roomId] = RoomStates.ROB_AGAIN;

        partyArrivedThiefs[partyId]++;
        
        if (partyArrivedThiefs[partyId] == Constants.ASSAULT_PARTY_SIZE) {
            masterThiefBusy = true;
            partyStates[partyId] = PartyStates.EMPTY;
            genRepo.clearParty(partyId);
            partyArrivedThiefs[partyId] = 0;
            thiefArrived = true;
            notifyAll();
        }
    }

    /**
     * Method to get the next Room to target.
     * @return Id of the target Room.
     */
    @Override
    public synchronized int getTargetRoom() {
        int room = chooseTargetRoom();
        if(room != -1)
            roomStates[room] = RoomStates.BEING_STOLEN;
        return room;
    }

    /**
     * Method to get the Assault Party to be deployed.. 
     * @return Id of the Assault Party to be prepared.
     */
    @Override
    public synchronized int getPartyToDeploy() {
        int party = choosePartyToDeploy();
        if(party != -1)
            partyStates[party] = PartyStates.DEPLOYED;
        return party;
    }

    
    private synchronized int chooseTargetRoom() {
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            if (roomStates[i] == RoomStates.NOT_VISITED || roomStates[i] == RoomStates.ROB_AGAIN)
            {
                return i;
            }
        }
        return -1;
    }

    private synchronized int choosePartyToDeploy() {
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if (partyStates[i] == PartyStates.EMPTY) {
                partyArrivedThiefs[i] = 0;
                return i;
            }
        }
        return -1;
    }

    private synchronized boolean allRoomsEmpty() {
        for (int i = 0; i < roomStates.length; i++) {
            if(roomStates[i] != RoomStates.ROOM_EMPTY)
                return false;
        }
        return true;
    }
    
    private boolean allPartiesFree(){
        for (int i = 0; i < partyStates.length; i++) {
            if(partyStates[i] != PartyStates.EMPTY)
                return false;
        }
        return true;
    }

    /**
     * Check if all the MasterThief can rest for a bit.
     * @return MasterThief has to wait
     */
    @Override
    public synchronized boolean waitingNedded() {
        partyToDeploy = choosePartyToDeploy();
        targetRoom = chooseTargetRoom();
        return(canvasToCollect > 0 || partyToDeploy == -1 || targetRoom == -1);
    }

    /**
     * Check if all the Rooms have been cleared.
     * @return heist completed
     */
    @Override
    public synchronized boolean isHeistCompleted() {
        genRepo.setCollectedCanvas(canvasCollected);
        return allRoomsEmpty() && allPartiesFree();
    }
}
