/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import monitors.AssaultParty.AssaultParty;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.AssaultParty.IotAssaultParty;
import monitors.ConcentrationSite.ConcentrationSite;
import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ConcentrationSite.IotConcentrationSite;
import monitors.ControlAndCollectionSite.ControlAndCollectionSite;
import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.ControlAndCollectionSite.IotControlAndCollectionSite;
import monitors.GeneralRepository.GeneralRepository;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.GeneralRepository.IotGeneralRepository;
import monitors.Museum.ImtMuseum;
import monitors.Museum.IotMuseum;
import monitors.Museum.Museum;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class Main {
    private static final int N_RUNS = 10000;
    // P1G9;

    /**
     * Main program
     * @param args Arguments of the main simulation.
     */
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
            MasterThief masterThief = new MasterThief((ImtMuseum)museum,
                    (ImtConcentrationSite)concentrationSite,
                    (ImtControlAndCollectionSite)controlCollectionSite,
                    (ImtAssaultParty[])assaultParty,
                    (ImtGeneralRepository)genRepo);
            OrdinaryThief[] ordinaryThieves = new OrdinaryThief[Constants.N_ORD_THIEVES];
            for (int i = 0; i < Constants.N_ORD_THIEVES; i++) {
                ordinaryThieves[i] = new OrdinaryThief(i,
                        (IotMuseum) museum,
                        (IotConcentrationSite)concentrationSite,
                        (IotControlAndCollectionSite)controlCollectionSite,
                        (IotAssaultParty[])assaultParty,
                        (IotGeneralRepository)genRepo);
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
