/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Random;
import monitors.AssaultParty;
import monitors.Museum;
import monitors.ConcentrationSite;
import monitors.ControlAndCollectionSite;

/**
 *
 * @author Ricardo Filipe
 */
public class OrdinaryThief extends Thread{
    
    
    
    private final int id;
    private final int speed;
    private AssaultParty[] assaultGroup; //TODO fazer isto mas para as interfaces
    private final monitors.Museum museum;
    private final monitors.ControlAndCollectionSite controlAndCollectionSite;
    private final monitors.ConcentrationSite concentrationSite;
    private char state;
    
    public OrdinaryThief(int id, Museum museum, 
                         ConcentrationSite concentrationSite,
                         ControlAndCollectionSite controlAndCollectionSite,
                         AssaultParty[] assaultGroup){
        this.id = id;
        this.speed = new Random().nextInt(Constants.MAX_CRAWL_DISTANCE)+1;
        this.museum = museum;
        this.concentrationSite = concentrationSite;
        this.controlAndCollectionSite = controlAndCollectionSite;
        this.assaultGroup = assaultGroup;
    }

    @Override
    public void run() {
        //System.out.println("OUTSIDE");
      while (concentrationSite.amINeeded(this.id))
    { 
       int partyId = concentrationSite.getPartyId(id);
      assaultGroup[partyId].joinParty(id, speed);
      concentrationSite.prepareExcursion(this.id);
      assaultGroup[partyId].crawlIn(this.id, this.speed);
      int roomId = assaultGroup[partyId].getTargetRoom();
      boolean canvas = museum.rollACanvas(roomId);
      assaultGroup[partyId].reverseDirection();
      assaultGroup[partyId].crawlOut(this.id, this.speed);
      
      controlAndCollectionSite.handACanvas(canvas, roomId, partyId);
    }  
    }
}
