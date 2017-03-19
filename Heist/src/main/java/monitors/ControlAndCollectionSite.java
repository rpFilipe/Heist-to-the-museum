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

    public ControlAndCollectionSite() {
        roomStates = new int[Constants.N_ROOMS];
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
    
    public synchronized int appraiseSit(int nWaitingThieves){
        System.out.println("monitors.ControlAndCollectionSite.appraiseSit()");
        int nextState;
        
        if (canvasToCollect > 0)
            nextState = MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        
        else if(isHeistCompleted(nWaitingThieves))
            nextState = MasterThiefStates.PRESENTING_THE_REPORT;
        
        
        else if(nWaitingThieves < Constants.ASSAULT_PARTY_SIZE)
            nextState = MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        
        else{
            targetRoom = chooseTargetRoom();
            if(targetRoom == -1){
                nextState = MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
            }
            else{
            partyToDeploy = choosePartyToDeploy();
            nextState = MasterThiefStates.ASSEMBLING_A_GROUP;
            }    
        }
        
               
        return nextState;
        
    }
    
    public synchronized void takeARest(){
        System.out.println("monitors.ControlAndCollectionSite.takeARest()");
        masterThiefBusy = false;
        notifyAll();
        
        while(!thiefArrived){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error Take a rest");
            }
        } thiefArrived = false;
    }
    
    public synchronized void collectCanvas(){
        System.out.println("monitors.ControlAndCollectionSite.collectCanvas()");
        masterThiefBusy = true;
        canvasToCollect--;
    }
    
    public synchronized void handACanvas(boolean hasCanvas, int roomId, int partyId){
        System.out.printf("monitors.ControlAndCollectionSite.handACanvas(%b - %d - %d)\n", hasCanvas, roomId, partyId);
        canvasToCollect++;
        
        while(masterThiefBusy){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error hansAcanvas");
            }
        } masterThiefBusy = true;
        
        if(!hasCanvas)
            roomStates[roomId] = RoomStates.ROOM_EMPTY;
        else if(roomStates[roomId] != RoomStates.ROOM_EMPTY)
            roomStates[roomId] = RoomStates.ROB_AGAIN;
        
        partyStates[partyId] = PartyStates.EMPTY;
        
        thiefArrived = true;
        notifyAll();
    }
    
    public int getTargetRoom(){
        System.out.println("monitors.ControlAndCollectionSite.getTargetRoom()");
        return this.targetRoom;
    }
    
    public int getPartyToDeploy(){
        System.out.println("monitors.ControlAndCollectionSite.getPartyToDeploy()");
        return  this.partyToDeploy;
    }
    
    private int chooseTargetRoom(){
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            if(roomStates[i] == RoomStates.NOT_VISITED || roomStates[i] == RoomStates.ROB_AGAIN)
            {
                roomStates[i] = RoomStates.BEING_STOLEN;
                return i;
            }
                
        }
        return -1;
    }
    
    private int choosePartyToDeploy(){
        System.out.println("monitors.ControlAndCollectionSite.getPartyToDeploy()");
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if(partyStates[i] == PartyStates.EMPTY){
                partyStates[i] = PartyStates.DEPLOYED;
                return i;
            }
                
        }
        return -1;
    }
    
    private boolean isHeistCompleted(int nWaitingThieves){
        if(nWaitingThieves < Constants.N_ORD_THIEVES)
            return false;
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            if(roomStates[i] != RoomStates.ROOM_EMPTY)
                return false;
        }
        return true;
    }
}
