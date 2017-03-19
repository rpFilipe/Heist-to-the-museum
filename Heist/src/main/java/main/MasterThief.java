/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import States.MasterThiefStates;
import monitors.AssaultParty;
import monitors.ConcentrationSite;
import monitors.ControlAndCollectionSite;
import monitors.Museum;

/**
 *
 * @author Ricardo Filipe
 */
public class MasterThief extends Thread{

    public void setState() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private boolean heistCompleted = false;
    private int state = MasterThiefStates.PLANNING_THE_HEIST;
     private final Museum museum;
    private final ControlAndCollectionSite controlAndCollectionSite;
    private final ConcentrationSite concentrationSite;
    private final AssaultParty[] assaultParty;

    public MasterThief(Museum museum, 
                       ConcentrationSite concentrationSite,
                       ControlAndCollectionSite controlAndCollectionSite,
                       AssaultParty[] assaultParty) {
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultParty = assaultParty;
        
    }
    
    

    @Override
    public void run() {
        while (true) {
            switch (state) {
                case MasterThiefStates.PLANNING_THE_HEIST:
                    concentrationSite.startOperations();
                    //System.out.println("PLANNING_THE_HEIST");
                    // startOperations
                    state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.DECIDING_WHAT_TO_DO:
                    //System.out.println("DECIDING_WHAT_TO_DO");
                    int nThivesWaiting = concentrationSite.getNumberThivesWaiting();
                    this.state = controlAndCollectionSite.appraiseSit(nThivesWaiting);
                    break;
                case MasterThiefStates.ASSEMBLING_A_GROUP:
                    //System.out.println("ASSEMBLING_A_GROUP");
                    int targetRoom = controlAndCollectionSite.getTargetRoom();
                    int partyId = controlAndCollectionSite.getPartyToDeploy();
                    System.out.printf("TargetRoom - %d | partyId - %d\n", targetRoom, partyId);
                    int roomDistance = museum.getRoomDistance(targetRoom);
                    
                    assaultParty[partyId].setRoom(targetRoom, roomDistance);
                    concentrationSite.prepareAssaultParty(partyId);
                    
                    
                    assaultParty[partyId].sendAssaultParty();
                    state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL:
                    //System.out.println("WAITING_FOR_GROUP_ARRIVAL");
                    controlAndCollectionSite.takeARest();
                    controlAndCollectionSite.collectCanvas();
                    this.state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.PRESENTING_THE_REPORT:
                    //System.out.println("PRESENTING_THE_REPORT");
                    concentrationSite.sumUpResults();
                    System.out.println("HEIST MOTHER FUCKONG COMPLETE");
                    return;
            }
        }
    }
}