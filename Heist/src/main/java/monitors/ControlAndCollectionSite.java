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
    
    public synchronized int appraiseSit(int nWaitingThieves){
        System.out.printf("monitors.ControlAndCollectionSite.appraiseSit() - %d\n", nWaitingThieves);
        int nextState;
        
        if (canvasToCollect > 0)
        {
            System.out.printf("canvasToCollect - %d\n", canvasToCollect);
            nextState = MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        }
        
        else if(isHeistCompleted(nWaitingThieves))
            nextState = MasterThiefStates.PRESENTING_THE_REPORT;
        
        
        else if(nWaitingThieves < Constants.ASSAULT_PARTY_SIZE)
        {
            nextState = MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
            System.out.println("nWaitingThieves");
        }
        
        else{
            targetRoom = chooseTargetRoom();
            partyToDeploy = choosePartyToDeploy();
            if(targetRoom == -1 || partyToDeploy == -1){
                nextState = MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
            }
            else{
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
        
        partyArrivedThiefs[partyId]++;
        if(partyArrivedThiefs[partyId] == Constants.ASSAULT_PARTY_SIZE)
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
    
    private synchronized int chooseTargetRoom(){
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            if(roomStates[i] == RoomStates.NOT_VISITED || roomStates[i] == RoomStates.ROB_AGAIN)
            {
                roomStates[i] = RoomStates.BEING_STOLEN;
                return i;
            }
                
        }
        return -1;
    }
    
    private synchronized int  choosePartyToDeploy()
    {
        System.out.println("monitors.ControlAndCollectionSite.getPartyToDeploy()");
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            if(partyStates[i] == PartyStates.EMPTY){
                partyStates[i] = PartyStates.BEING_FORMED;
                partyArrivedThiefs[i] = 0;
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
