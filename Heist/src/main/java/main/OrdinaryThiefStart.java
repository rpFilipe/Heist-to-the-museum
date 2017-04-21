/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

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
 */
public class OrdinaryThiefStart {
    
    private static final int N_ORD_THIEVES = 6;
    private static String configServerAddr;
    private static int configServerPort;
    
    public static void main(String[] args) {
        
        configServerAddr = args[0];
        configServerPort = Integer.parseInt(args[1]);
        
        GeneralRepositoryProxy genRepo = new GeneralRepositoryProxy(configServerAddr, configServerPort);
        //GeneralRepository genRepo = new GeneralRepository();
        MuseumProxy museum = new MuseumProxy(configServerAddr, configServerPort);
        ControlAndCollectionSiteProxy controlCollectionSite = new ControlAndCollectionSiteProxy(configServerAddr, configServerPort);
        ConcentrationSiteProxy concentrationSite = new ConcentrationSiteProxy(configServerAddr, configServerPort);
        AssaultPartyProxy[] assaultParty = new AssaultPartyProxy[Constants.N_ASSAULT_PARTIES];
        OrdinaryThief[] ordinaryThives = new OrdinaryThief[N_ORD_THIEVES];
        

        for(int i = 0; i < Constants.N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultPartyProxy(i, configServerAddr, configServerPort);
        }
        
        for (int i = 0; i < N_ORD_THIEVES; i++) {
            ordinaryThives[i] = new OrdinaryThief(i, 
                    (IotMuseum)museum,
                    (IotConcentrationSite)concentrationSite,
                    (IotControlAndCollectionSite)controlCollectionSite,
                    (IotAssaultParty[])assaultParty,
                    (IotGeneralRepository)genRepo);
        }
        
        for (OrdinaryThief ot : ordinaryThives) {
            ot.start();
        }
        for (OrdinaryThief ot : ordinaryThives) {
            try {
                ot.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(OrdinaryThiefStart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
