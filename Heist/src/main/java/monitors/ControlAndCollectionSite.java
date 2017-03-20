/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import States.MasterThiefStates;
import States.PartyStates;
import States.RoomStates;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Constants;
import main.MasterThief;
import monitors.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class ControlAndCollectionSite {

    private boolean heistCompleted;
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

    public ControlAndCollectionSite() {
        roomStates = new int[Constants.N_ROOMS];
        partyFree = true;
        partyArrivedThiefs = new int[Constants.N_ASSAULT_PARTIES];
        canvasToCollect = 0;
        canvasCollected = 0;
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
    }

    public synchronized int appraiseSit(int nWaitingThieves) {
        //System.out.printf("monitors.ControlAndCollectionSite.appraiseSit(%d)\n", nWaitingThieves);

        targetRoom = chooseTargetRoom();
        partyToDeploy = choosePartyToDeploy();
        
        //System.out.printf("targetRoom = %d | partyID = %d", targetRoom, partyToDeploy);
        
        if(isHeistCompleted()){
            System.out.printf("Stolen paitings = %d\n", canvasCollected);
            return MasterThiefStates.PRESENTING_THE_REPORT;
        }
        
        if(canvasToCollect > 0 || partyToDeploy == -1 || targetRoom == -1 || nWaitingThieves < Constants.ASSAULT_PARTY_SIZE){
            return MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        }
        
        /*if(targetRoom != -1 && partyToDeploy != -1){
            roomStates[targetRoom] = RoomStates.BEING_STOLEN;
            partyStates[partyToDeploy] = PartyStates.BEING_FORMED;
            return MasterThiefStates.ASSEMBLING_A_GROUP;
        }*/
        
        
        roomStates[targetRoom] = RoomStates.BEING_STOLEN;
        partyStates[partyToDeploy] = PartyStates.BEING_FORMED;
        return MasterThiefStates.ASSEMBLING_A_GROUP;
    }

    public synchronized void takeARest() {
        masterThiefBusy = false;
        notifyAll();

        while (!thiefArrived) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error Take a rest");
            }
        }
        thiefArrived = false;
    }

    public synchronized void collectCanvas() {
        canvasToCollect--;
    }

    public synchronized void handACanvas(boolean hasCanvas, int roomId, int partyId) {
        canvasToCollect++;

        while (masterThiefBusy) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error hansAcanvas");
            }
        }
        masterThiefBusy = true;
        
        if(hasCanvas){
            canvasCollected++;
        }
            

        if (!hasCanvas) {
            roomStates[roomId] = RoomStates.ROOM_EMPTY;
        } else if (roomStates[roomId] != RoomStates.ROOM_EMPTY) {
            roomStates[roomId] = RoomStates.ROB_AGAIN;
        }

        partyArrivedThiefs[partyId]++;
        
        if (partyArrivedThiefs[partyId] == Constants.ASSAULT_PARTY_SIZE) {
            partyStates[partyId] = PartyStates.EMPTY;
            partyArrivedThiefs[partyId] = 0;
        }

        thiefArrived = true;
        notifyAll();
    }

    public synchronized int getTargetRoom() {
        return this.targetRoom;
    }

    public synchronized int getPartyToDeploy() {
        return this.partyToDeploy;
    }

    private int chooseTargetRoom() {
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            if (roomStates[i] == RoomStates.NOT_VISITED || roomStates[i] == RoomStates.ROB_AGAIN) {
                return i;
            }
        }
        return -1;
    }

    private int choosePartyToDeploy() {
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if (partyStates[i] == PartyStates.EMPTY) {
                partyArrivedThiefs[i] = 0;
                return i;
            }
        }
        return -1;
    }
    
    private boolean allPartiesFree(){
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if(partyStates[i] != PartyStates.EMPTY)
                return false;
        }
        return true;
    }

    private boolean isHeistCompleted() {
        if(targetRoom == -1 && allPartiesFree())
            return true;
        return false;
    }
}
