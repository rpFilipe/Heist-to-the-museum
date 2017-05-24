/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import States.MasterThiefStates;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.Museum.ImtMuseum;
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
            try {
                this.myClk.incrementClock();
                this.receivedClk = this.genRepo.updateMThiefState(state, this.myClk.clone());
                this.myClk.update(this.receivedClk);
                
                switch (state) {
                    case MasterThiefStates.PLANNING_THE_HEIST:
                        this.myClk.incrementClock();
                        this.receivedClk = this.concentrationSite.startOperations(this.myClk.clone());
                        state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                        break;
                    case MasterThiefStates.DECIDING_WHAT_TO_DO:
                        //int nwating = concentrationSite.getNumberThivesWaiting();
                        boolean a = controlAndCollectionSite.isHeistCompleted();
                        boolean b = controlAndCollectionSite.waitingNedded();
                        this.state = this.concentrationSite.appraiseSit(a, b);
                        break;
                    case MasterThiefStates.ASSEMBLING_A_GROUP:
                        int targetRoom = controlAndCollectionSite.getTargetRoom();
                        int partyToDeploy = controlAndCollectionSite.getPartyToDeploy();
                        
                        /* Isto para todas as mensagens*/
                        this.myClk.incrementClock();
                        this.receivedClk = museum.getRoomDistance(targetRoom, this.myClk.clone());
                        int roomDistance = receivedClk.getReturnIntValue();
                        myClk.update(receivedClk);
                        /***************************************/
                        
                        assaultParty[partyToDeploy].setRoom(targetRoom, roomDistance);
                        concentrationSite.prepareAssaultParty(partyToDeploy);
                        
                        assaultParty[partyToDeploy].sendAssaultParty();
                        state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                        break;
                    case MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL:
                        controlAndCollectionSite.takeARest();
                        controlAndCollectionSite.collectCanvas();
                        this.state = MasterThiefStates.DECIDING_WHAT_TO_DO;
                        break;
                    case MasterThiefStates.PRESENTING_THE_REPORT:
                        concentrationSite.sumUpResults();
                        genRepo.FinalizeLog();
                        return;
                }
            } catch (RemoteException ex) {
                Logger.getLogger(MasterThief.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
