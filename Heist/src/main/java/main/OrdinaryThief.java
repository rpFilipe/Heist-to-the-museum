/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import States.OrdinaryThiefState;
import java.util.Random;
import monitors.AssaultParty;
import monitors.Museum;
import monitors.ConcentrationSite;
import monitors.ControlAndCollectionSite;
import monitors.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class OrdinaryThief extends Thread {

    private final int id;
    private final int speed;
    private AssaultParty[] assaultGroup; //TODO fazer isto mas para as interfaces
    private  monitors.Museum museum;
    private  monitors.ControlAndCollectionSite controlAndCollectionSite;
    private  monitors.ConcentrationSite concentrationSite;
    private int state;
    private GeneralRepository genRepo;

    public OrdinaryThief(int id, Museum museum,
            ConcentrationSite concentrationSite,
            ControlAndCollectionSite controlAndCollectionSite,
            AssaultParty[] assaultGroup,
            GeneralRepository genRepo) {
        this.id = id;
        this.speed = new Random().nextInt(Constants.MAX_THIEF_SPEED - Constants.MIN_THIEF_SPEED) + Constants.MIN_THIEF_SPEED;
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultGroup = assaultGroup;
        this.genRepo = genRepo;
        this.genRepo.addThief(id, speed);
        //this.genRepo.setThiefSpeed(id, speed);
    }

    @Override
    public void run() {
        this.state = OrdinaryThiefState.OUTSIDE;
        //genRepo.updateThiefState(id, state);
        while (concentrationSite.amINeeded(this.id)) {
            int partyId = concentrationSite.getPartyId(id);
            assaultGroup[partyId].joinParty(id, speed);
            
            this.state = OrdinaryThiefState.CRAWLING_INWARDS;
            genRepo.updateThiefState(id, state);
            concentrationSite.prepareExcursion(this.id);
            
            assaultGroup[partyId].crawlIn(this.id);
            int roomId = assaultGroup[partyId].getTargetRoom();
            
            this.state = OrdinaryThiefState.AT_A_ROOM;
            genRepo.updateThiefState(id, state);
            
            boolean canvas = museum.rollACanvas(roomId);
            genRepo.updateThiefCylinder(id, canvas);
            assaultGroup[partyId].reverseDirection(this.id);
            
            this.state = OrdinaryThiefState.CRAWLING_OUTWARDS;
            genRepo.updateThiefState(id, state);
            
            assaultGroup[partyId].crawlOut(this.id);
            genRepo.updateThiefSituation(id, 'W');
            controlAndCollectionSite.handACanvas(canvas, roomId, partyId);
            this.state = OrdinaryThiefState.OUTSIDE;
            genRepo.updateThiefState(id, state);
            genRepo.updateThiefCylinder(id, false);
        }
    }
}
