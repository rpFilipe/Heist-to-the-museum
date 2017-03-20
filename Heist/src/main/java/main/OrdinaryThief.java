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

/**
 *
 * @author Ricardo Filipe
 */
public class OrdinaryThief extends Thread {

    private final int id;
    private final int speed;
    private AssaultParty[] assaultGroup; //TODO fazer isto mas para as interfaces
    private final monitors.Museum museum;
    private final monitors.ControlAndCollectionSite controlAndCollectionSite;
    private final monitors.ConcentrationSite concentrationSite;
    private int state;

    public OrdinaryThief(int id, Museum museum,
            ConcentrationSite concentrationSite,
            ControlAndCollectionSite controlAndCollectionSite,
            AssaultParty[] assaultGroup) {
        this.id = id;
        this.speed = new Random().nextInt(Constants.MAX_CRAWL_DISTANCE) + 1;
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultGroup = assaultGroup;
    }

    @Override
    public void run() {
        this.state = OrdinaryThiefState.OUTSIDE;
        
        while (concentrationSite.amINeeded(this.id)) {
            int partyId = concentrationSite.getPartyId(id);
            assaultGroup[partyId].joinParty(id, speed);
            
            this.state = OrdinaryThiefState.CRAWLING_INWARDS;
            concentrationSite.prepareExcursion(this.id);
            
            assaultGroup[partyId].crawlIn(this.id, this.speed);
            int roomId = assaultGroup[partyId].getTargetRoom();
            
            this.state = OrdinaryThiefState.AT_A_ROOM;
            
            boolean canvas = museum.rollACanvas(roomId);
            assaultGroup[partyId].reverseDirection(this.id);
            
            this.state = OrdinaryThiefState.CRAWLING_OUTWARDS;
            
            assaultGroup[partyId].crawlOut(this.id, this.speed);

            controlAndCollectionSite.handACanvas(canvas, roomId, partyId);
        }
    }
}
