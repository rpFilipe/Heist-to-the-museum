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
public class MasterThief extends Thread {

    private int state = MasterThiefStates.PLANNING_THE_HEIST;
    private  Museum museum;
    private  ControlAndCollectionSite controlAndCollectionSite;
    private  ConcentrationSite concentrationSite;
    private  AssaultParty[] assaultParty;
    private boolean heistCompletes = false;

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
        int nThivesWaiting = 0;
        while (!heistCompletes) {
            switch (state) {
                case MasterThiefStates.PLANNING_THE_HEIST:
                    concentrationSite.startOperations();
                    state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.DECIDING_WHAT_TO_DO:
                    nThivesWaiting = concentrationSite.getNumberThivesWaiting();
                    this.state = controlAndCollectionSite.appraiseSit(nThivesWaiting);
                    break;
                case MasterThiefStates.ASSEMBLING_A_GROUP:
                    int targetRoom = controlAndCollectionSite.getTargetRoom();
                    int partyId = controlAndCollectionSite.getPartyToDeploy();

                    int roomDistance = museum.getRoomDistance(targetRoom);

                    assaultParty[partyId].setRoom(targetRoom, roomDistance);
                    concentrationSite.prepareAssaultParty(partyId);

                    assaultParty[partyId].sendAssaultParty();
                    state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL:
                    controlAndCollectionSite.takeARest();
                    controlAndCollectionSite.collectCanvas();
                    this.state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.PRESENTING_THE_REPORT:
                    System.out.println("ReportReady - " + nThivesWaiting);
                    concentrationSite.sumUpResults();
                    System.out.println("HEIST MOTHER FUCKONG COMPLETE");
                    heistCompletes = true;
                    break;
            }
        }
    }
}
