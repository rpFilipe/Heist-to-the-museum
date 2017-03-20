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
    private int canvasCollected = 0;
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

        if(isHeistCompleted(nWaitingThieves)){
            System.out.println("Heist Is completed!");
            return MasterThiefStates.PRESENTING_THE_REPORT;
        }
        
        if(canvasToCollect > 0){
            System.out.printf("Still has %d canvas to collect\n", canvasToCollect);
            return MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        }
        
        targetRoom = chooseTargetRoom();
        
        if(targetRoom == -1 && allPartiesFree()){
            System.out.printf("no more rooms to go and all parties are free | %d thieves waiting\n", nWaitingThieves);
            return MasterThiefStates.DECIDING_WHAT_TO_DO;
        }
        
        if(targetRoom == -1 && !allPartiesFree()){
            System.out.printf("no more rooms to go but a party is deployed\n");
            return MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        }
        
        partyToDeploy = choosePartyToDeploy();
        
        if(partyToDeploy == -1){
            System.out.printf("rooms to go but no party available\n");
            return MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        }
        
        System.out.printf("targetRoom = %d | partyToDeploy = %d\n", targetRoom, partyToDeploy);
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
                roomStates[i] = RoomStates.BEING_STOLEN;
                return i;
            }

        }
        return -1;
    }

    private int choosePartyToDeploy() {
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if (partyStates[i] == PartyStates.EMPTY) {
                partyStates[i] = PartyStates.BEING_FORMED;
                partyArrivedThiefs[i] = 0;
                return i;
            }
        }
        return -1;
    }

    private boolean isHeistCompleted(int nWaitingThives) {
        if (canvasToCollect > 0) {
            return false;
        }
        System.err.println("Canvas OK");
        if(nWaitingThives != Constants.N_ORD_THIEVES)
            return false;
        System.err.println("THIEVES OK");
        
        if(chooseTargetRoom() != -1)
            return false;
        System.err.println("ROOMS OK");
        
        return true;
    }
    
    private boolean allPartiesFree(){
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if(partyStates[i] != PartyStates.EMPTY)
                return false;
        }
        return true;
    }
}
