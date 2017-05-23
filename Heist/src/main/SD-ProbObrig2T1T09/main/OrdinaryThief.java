/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import States.OrdinaryThiefState;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty.IotAssaultParty;
import monitors.ConcentrationSite.IotConcentrationSite;
import monitors.ControlAndCollectionSite.IotControlAndCollectionSite;
import monitors.GeneralRepository.IotGeneralRepository;
import monitors.Museum.IotMuseum;

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

    /**
     * Create a new OrdinaryThief
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
        this.speed = new Random().nextInt(max_speed - min_speed) +min_speed;
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultGroup = assaultGroup;
        this.genRepo = genRepo;
        this.genRepo.addThief(id, speed);
    }

    @Override
    public void run() {
        this.state = OrdinaryThiefState.OUTSIDE;
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
            assaultGroup[partyId].reverseDirection();
            
            this.state = OrdinaryThiefState.CRAWLING_OUTWARDS;
            genRepo.updateThiefState(id, state);
            
            assaultGroup[partyId].crawlOut(this.id);
            this.state = OrdinaryThiefState.OUTSIDE;
            genRepo.updateThiefState(id, state);
            genRepo.updateThiefCylinder(id, false);
            genRepo.updateThiefSituation(id, 'W');
            controlAndCollectionSite.handACanvas(id, canvas, roomId, partyId);
        }
    }
}
