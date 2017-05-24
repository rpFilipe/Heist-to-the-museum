/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import States.MasterThiefStates;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.Museum.ImtMuseum;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class MasterThief extends Thread {

    private int state = MasterThiefStates.PLANNING_THE_HEIST;
    private ImtMuseum museum;
    private ImtControlAndCollectionSite controlAndCollectionSite;
    private ImtConcentrationSite concentrationSite;
    private ImtAssaultParty[] assaultParty;
    private ImtGeneralRepository genRepo;
    private VectorClock myClk;
    private VectorClock receivedClk;

    /**
     * Create a new MasterThief
     *
     * @param museum museum interface
     * @param concentrationSite ConcentrationSite interface
     * @param controlAndCollectionSite ControlAndCollectionSite interface
     * @param assaultParty AssaultParty interface
     * @param genRepo GeneralRepository interface
     */
    public MasterThief(ImtMuseum museum,
            ImtConcentrationSite concentrationSite,
            ImtControlAndCollectionSite controlAndCollectionSite,
            ImtAssaultParty[] assaultParty,
            ImtGeneralRepository genRepo) {
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultParty = assaultParty;
        this.genRepo = genRepo;
        myClk = new VectorClock(7, 0);
    }

    @Override
    public void run() {
        while (true) {
            this.myClk.incrementClock();
            this.receivedClk = this.genRepo.updateMThiefState(state, this.myClk.clone());
            this.myClk.update(this.receivedClk);

            switch (state) {
                case MasterThiefStates.PLANNING_THE_HEIST:
                    this.myClk.incrementClock();
                    this.receivedClk = this.concentrationSite.startOperations(this.myClk.clone());
                    this.myClk.update(this.receivedClk);
                    state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.DECIDING_WHAT_TO_DO:
                    //int nwating = concentrationSite.getNumberThivesWaiting();
                    this.myClk.incrementClock();
                    Pair<VectorClock, Boolean> a = this.controlAndCollectionSite.isHeistCompleted(this.myClk.clone());
                    this.myClk.update(a.first);

                    this.myClk.incrementClock();
                    Pair<VectorClock, Boolean> b = this.controlAndCollectionSite.waitingNedded(this.myClk.clone());
                    this.myClk.update(a.first);

                    this.myClk.incrementClock();
                    Pair<VectorClock, Integer> pairAppraiseSit = this.concentrationSite.appraiseSit(a.second, b.second, this.myClk.clone());
                    this.myClk.update(pairAppraiseSit.first);

                    this.state = pairAppraiseSit.second;         
                    break;
                case MasterThiefStates.ASSEMBLING_A_GROUP:
                    this.myClk.incrementClock();
                    Pair<VectorClock, Integer> pairGetTargetRoom = this.controlAndCollectionSite.getTargetRoom(this.myClk.clone());
                    int targetRoom = pairGetTargetRoom.second;
                    this.myClk.update(pairGetTargetRoom.first);

                    this.myClk.incrementClock();
                    Pair<VectorClock, Integer> pairGetPartyToDeploy = this.controlAndCollectionSite.getPartyToDeploy(this.myClk.clone());
                    int partyToDeploy = pairGetPartyToDeploy.second;
                    this.myClk.update(pairGetPartyToDeploy.first);

                    this.myClk.incrementClock();
                    Pair<VectorClock, Integer> pairGetRoomDistance = this.museum.getRoomDistance(targetRoom, this.myClk.clone());
                    int roomDistance = pairGetRoomDistance.second;  
                    this.myClk.update(pairGetPartyToDeploy.first);

                    this.myClk.incrementClock();
                    this.receivedClk = this.assaultParty[partyToDeploy].setRoom(targetRoom, roomDistance, this.myClk.clone());
                    //this.myClk.update();

                    this.myClk.incrementClock();
                    this.receivedClk = this.concentrationSite.prepareAssaultParty(partyToDeploy, this.myClk.clone());
                    this.myClk.update(receivedClk);

                    this.myClk.incrementClock();
                    this.receivedClk = this.assaultParty[partyToDeploy].sendAssaultParty(this.myClk.clone());
                    this.myClk.update(receivedClk);

                    state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL:
                    this.myClk.incrementClock();
                    this.receivedClk = this.controlAndCollectionSite.takeARest(this.myClk.clone());
                    this.myClk.update(receivedClk);

                    this.myClk.incrementClock();
                    this.receivedClk = this.controlAndCollectionSite.collectCanvas(this.myClk.clone());
                    this.myClk.update(receivedClk);

                    this.state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                    break;
                case MasterThiefStates.PRESENTING_THE_REPORT:
                    this.myClk.incrementClock();
                    this.receivedClk = this.concentrationSite.sumUpResults(this.myClk.clone());
                    this.myClk.update(receivedClk);

                    this.myClk.incrementClock();
                    this.receivedClk = this.genRepo.FinalizeLog(this.myClk.clone());
                    this.myClk.update(receivedClk);
                    return;
            }
        }
    }
}
