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
    Pair<VectorClock, Integer> serverResponseInt;
    Pair<VectorClock, Boolean> serverResponseBool;

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
                    serverResponseBool = this.controlAndCollectionSite.isHeistCompleted(this.myClk.clone());
                    this.myClk.update(serverResponseBool.first);
                    boolean a = serverResponseBool.second;

                    this.myClk.incrementClock();
                    serverResponseBool = this.controlAndCollectionSite.waitingNedded(this.myClk.clone());
                    this.myClk.update(serverResponseBool.first);
                    boolean b = serverResponseBool.second;

                    this.myClk.incrementClock();
                    serverResponseInt = this.concentrationSite.appraiseSit(a, b, this.myClk.clone());
                    this.myClk.update(serverResponseInt.first);

                    this.state = serverResponseInt.second;         
                    break;
                case MasterThiefStates.ASSEMBLING_A_GROUP:
                    this.myClk.incrementClock();
                    serverResponseInt = this.controlAndCollectionSite.getTargetRoom(this.myClk.clone());
                    int targetRoom = serverResponseInt.second;
                    this.myClk.update(serverResponseInt.first);

                    this.myClk.incrementClock();
                    serverResponseInt = this.controlAndCollectionSite.getPartyToDeploy(this.myClk.clone());
                    int partyToDeploy = serverResponseInt.second;
                    this.myClk.update(serverResponseInt.first);

                    this.myClk.incrementClock();
                    serverResponseInt = this.museum.getRoomDistance(targetRoom, this.myClk.clone());
                    int roomDistance = serverResponseInt.second;  
                    this.myClk.update(serverResponseInt.first);

                    this.myClk.incrementClock();
                    this.receivedClk = this.assaultParty[partyToDeploy].setRoom(targetRoom, roomDistance, this.myClk.clone());
                    this.myClk.update(receivedClk);

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
