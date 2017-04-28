/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Communication.SettingsProxy;
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
public class OrdinaryThievesStart {
    
    private static int N_ORD_THIEVES;
    private static int N_ASSAULT_PARTIES;
    private static String configServerAddr;
    private static int configServerPort;
    
    public static void main(String[] args) {
        
        configServerAddr = args[0];
        configServerPort = Integer.parseInt(args[1]);
        GeneralRepositoryProxy genRepo = new GeneralRepositoryProxy(configServerAddr, configServerPort);
        SettingsProxy sp = new SettingsProxy(configServerAddr, configServerPort);
        
        N_ORD_THIEVES = sp.getN_ORD_THIEVES();
        N_ASSAULT_PARTIES = N_ORD_THIEVES/sp.getASSAULT_PARTY_SIZE();
        int max_speed = sp.getMAX_THIEF_SPEED();
        int min_speed = sp.getMIN_THIEF_SPEED();
        
        MuseumProxy museum = new MuseumProxy(configServerAddr, configServerPort);
        ControlAndCollectionSiteProxy controlCollectionSite = new ControlAndCollectionSiteProxy(configServerAddr, configServerPort);
        ConcentrationSiteProxy concentrationSite = new ConcentrationSiteProxy(configServerAddr, configServerPort);
        AssaultPartyProxy[] assaultParty = new AssaultPartyProxy[N_ASSAULT_PARTIES];
        OrdinaryThief[] ordinaryThives = new OrdinaryThief[N_ORD_THIEVES];
        

        for(int i = 0; i < N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultPartyProxy(i, configServerAddr, configServerPort);
        }
        
        for (int i = 0; i < N_ORD_THIEVES; i++) {
            ordinaryThives[i] = new OrdinaryThief(i, max_speed, min_speed, 
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
                Logger.getLogger(OrdinaryThievesStart.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    
}
