/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty;
import monitors.ConcentrationSite;
import monitors.ControlAndCollectionSite;
import monitors.GeneralRepository;
import monitors.Museum;

/**
 *
 * @author Ricardo Filipe
 */
public class Main {
    public static void main(String[] args) {
        // Initializing the monitors
        GeneralRepository genRepo = new GeneralRepository();
        Museum museum = new Museum();
        ControlAndCollectionSite controlCollectionSite = new ControlAndCollectionSite();
        ConcentrationSite concentrationSite = new ConcentrationSite();
        AssaultParty[] assaultParty = new AssaultParty[Constants.N_ASSAULT_PARTIES];
        for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultParty(i);
        }
        
        // Initializing the entities
        MasterThief masterThief = new MasterThief(museum, concentrationSite, controlCollectionSite, assaultParty);
        OrdinaryThief[] ordinaryThieves = new OrdinaryThief[Constants.N_ORD_THIEVES];
        for (int i = 0; i < Constants.N_ORD_THIEVES; i++)
            ordinaryThieves[i] = new OrdinaryThief(i, museum, concentrationSite, controlCollectionSite, assaultParty);
        
        // Starting the entities
        for (int i = 0; i < Constants.N_ORD_THIEVES; i++)
            ordinaryThieves[i].start();
         masterThief.start();
        
        try {
            // Waiting for entities to die
            for (int i = 0; i < Constants.N_ORD_THIEVES; i++)
                ordinaryThieves[i].join();
             masterThief.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
