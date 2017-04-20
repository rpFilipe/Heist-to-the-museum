/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.Museum.ImtMuseum;
/**
 *
 * @author Ricardo Filipe
 */
public class MasterThiefStart {
    
    private static String configServerAddr;
    private static int configServerPort;
    
    public static void main(String[] args) {
        
        configServerAddr = args[0];
        configServerPort = Integer.parseInt(args[1]);
        
        GeneralRepositoryProxy genRepo = new GeneralRepositoryProxy(configServerAddr, configServerPort);
        MuseumProxy museum = new MuseumProxy(configServerAddr, configServerPort);
        ControlAndCollectionSiteProxy controlCollectionSite = new ControlAndCollectionSiteProxy(configServerAddr, configServerPort);
        ConcentrationSiteProxy concentrationSite = new ConcentrationSiteProxy(configServerAddr, configServerPort);
        AssaultPartyProxy[] assaultParty = new AssaultPartyProxy[Constants.N_ASSAULT_PARTIES];
        

        for(int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultPartyProxy(i, configServerAddr, configServerPort);
        }
        
        MasterThief mt = new MasterThief( (ImtMuseum)museum,
                (ImtConcentrationSite)concentrationSite,
                (ImtControlAndCollectionSite)controlCollectionSite,
                (ImtAssaultParty[])assaultParty,
                (ImtGeneralRepository)genRepo);
        
        mt.start();
        
        try {
            mt.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(MasterThiefStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
