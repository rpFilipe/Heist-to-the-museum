/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
    private static final int N_RUNS = 1;
    // P1G9;
    public static void main(String[] args) {
        for (int j = 1; j <= N_RUNS; j++) {

            // Initializing the monitors
            GeneralRepository genRepo = new GeneralRepository();
            Museum museum = new Museum(genRepo);
            ControlAndCollectionSite controlCollectionSite = new ControlAndCollectionSite(genRepo);
            ConcentrationSite concentrationSite = new ConcentrationSite(genRepo);
            AssaultParty[] assaultParty = new AssaultParty[Constants.N_ASSAULT_PARTIES];
            for (int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
                assaultParty[i] = new AssaultParty(i, genRepo);
            }

            // Initializing the entities
            MasterThief masterThief = new MasterThief(museum, concentrationSite, controlCollectionSite, assaultParty, genRepo);
            OrdinaryThief[] ordinaryThieves = new OrdinaryThief[Constants.N_ORD_THIEVES];
            for (int i = 0; i < Constants.N_ORD_THIEVES; i++) {
                ordinaryThieves[i] = new OrdinaryThief(i, museum, concentrationSite, controlCollectionSite, assaultParty, genRepo);
            }

            // Starting the entities
            for (int i = 0; i < Constants.N_ORD_THIEVES; i++) {
                ordinaryThieves[i].start();
            }
            masterThief.start();

            try {
                // Waiting for entities to die
                for (int i = 0; i < Constants.N_ORD_THIEVES; i++) {
                    ordinaryThieves[i].join();
                }
                masterThief.join();
            } catch (InterruptedException ex) {
            }
            
            System.out.println("Run successfuly run - "+j);
        }
    }
}
