/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import States.OrdinaryThiefState;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty.IotAssaultParty;
import monitors.ConcentrationSite.IotConcentrationSite;
import monitors.ControlAndCollectionSite.IotControlAndCollectionSite;
import monitors.GeneralRepository.IotGeneralRepository;
import monitors.Museum.IotMuseum;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class OrdinaryThief extends Thread {

    private final int id;
    private final int speed;
    private IotAssaultParty[] assaultGroup; //TODO fazer isto mas para as interfaces
    private IotMuseum museum;
    private IotControlAndCollectionSite controlAndCollectionSite;
    private IotConcentrationSite concentrationSite;
    private IotGeneralRepository genRepo;
    private int state;
    private VectorClock myClk;
    private VectorClock receivedClk;
    Pair<VectorClock, Integer> serverResponseInt;
    Pair<VectorClock, Boolean> serverResponseBool;

    /**
     * Create a new OrdinaryThief
     *
     * @param id of the OrdinaryThief
     * @param max_speed Maximum speed of the Ordinary Thief
     * @param min_speed Minimum speed of the Ordinary Thief
     * @param museum museum interface
     * @param concentrationSite ConcentrationSite interface
     * @param controlAndCollectionSite ControlAndCollectionSite interface
     * @param assaultGroup AssaultParty interface
     * @param genRepo GeneralRepository interface
     */
    public OrdinaryThief(int id, int max_speed, int min_speed,
            IotMuseum museum,
            IotConcentrationSite concentrationSite,
            IotControlAndCollectionSite controlAndCollectionSite,
            IotAssaultParty[] assaultGroup,
            IotGeneralRepository genRepo) {
        this.id = id;
        this.speed = new Random().nextInt(max_speed - min_speed) + min_speed;
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultGroup = assaultGroup;
        this.genRepo = genRepo;
        this.myClk = new VectorClock(7, id + 1);
        try {
            this.genRepo.addThief(id, speed, myClk);
        } catch (RemoteException ex) {
            Logger.getLogger(OrdinaryThief.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrdinaryThief.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        try {
            this.state = OrdinaryThiefState.OUTSIDE;
            myClk.incrementClock();
            serverResponseBool = concentrationSite.amINeeded(this.id, myClk.clone());
            myClk.update(serverResponseBool.first);
            boolean amINeeded = serverResponseBool.second;

            while (amINeeded) {

                myClk.incrementClock();
                serverResponseInt = concentrationSite.getPartyId(id, myClk);
                myClk.update(serverResponseInt.first);
                int partyId = serverResponseInt.second;

                myClk.incrementClock();
                receivedClk = assaultGroup[partyId].joinParty(id, speed, myClk.clone());
                myClk.update(receivedClk);

                this.state = OrdinaryThiefState.CRAWLING_INWARDS;

                myClk.incrementClock();
                receivedClk = genRepo.updateThiefState(id, state, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = concentrationSite.prepareExcursion(this.id, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = assaultGroup[partyId].crawlIn(this.id, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                serverResponseInt = assaultGroup[partyId].getTargetRoom(myClk.clone());
                int roomId = serverResponseInt.second;
                myClk.update(serverResponseInt.first);

                this.state = OrdinaryThiefState.AT_A_ROOM;
                myClk.incrementClock();
                receivedClk = genRepo.updateThiefState(id, state, myClk.clone());
                myClk.update(receivedClk);

                /* Isto para todas as mensagens*/
                myClk.incrementClock();
                serverResponseBool = museum.rollACanvas(roomId, myClk.clone());
                boolean canvas = serverResponseBool.second;
                myClk.update(serverResponseBool.first);
                /**
                 * ************************************
                 */

                myClk.incrementClock();
                receivedClk = genRepo.updateThiefCylinder(id, canvas, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = assaultGroup[partyId].reverseDirection(myClk.clone());
                myClk.update(receivedClk);

                this.state = OrdinaryThiefState.CRAWLING_OUTWARDS;

                myClk.incrementClock();
                receivedClk = genRepo.updateThiefState(id, state, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = assaultGroup[partyId].crawlOut(this.id, myClk.clone());
                myClk.update(receivedClk);

                this.state = OrdinaryThiefState.OUTSIDE;

                myClk.incrementClock();
                receivedClk = genRepo.updateThiefState(id, state, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = genRepo.updateThiefCylinder(id, false, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = genRepo.updateThiefSituation(id, 'W', myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                receivedClk = controlAndCollectionSite.handACanvas(id, canvas, roomId, partyId, myClk.clone());
                myClk.update(receivedClk);

                myClk.incrementClock();
                serverResponseBool = concentrationSite.amINeeded(this.id, myClk.clone());
                myClk.update(serverResponseBool.first);
                amINeeded = serverResponseBool.second;
            }
        } catch (RemoteException ex) {
            Logger.getLogger(OrdinaryThief.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(OrdinaryThief.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
