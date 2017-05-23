/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Proxies.AssaultPartyProxy;
import Proxies.GeneralRepositoryProxy;
import Proxies.ConcentrationSiteProxy;
import Proxies.ControlAndCollectionSiteProxy;
import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import Proxies.SettingsProxy;
import interfaces.MuseumInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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

    private static String rmiServerHostname;
    private static int rmiServerPort;
    private static int N_ASSAULT_PARTIES;

    /**
     * Start the Master Thief Life.
     *
     * @param args
     */
    public static void main(String[] args) {

        rmiServerHostname = args[0];
        rmiServerPort = Integer.parseInt(args[1]);

        SettingsProxy sp = new SettingsProxy(configServerAddr, configServerPort);

        N_ASSAULT_PARTIES = sp.getN_ORD_THIEVES() / sp.getASSAULT_PARTY_SIZE();

        Registry reg = null;
        MuseumInterface museum = null;

        try {
            Registry registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
            museum = (MuseumInterface) registry.lookup("museum");
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating log: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Log is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        GeneralRepositoryProxy genRepo = new GeneralRepositoryProxy(configServerAddr, configServerPort);
        ControlAndCollectionSiteProxy controlCollectionSite = new ControlAndCollectionSiteProxy(configServerAddr, configServerPort);
        ConcentrationSiteProxy concentrationSite = new ConcentrationSiteProxy(configServerAddr, configServerPort);
        AssaultPartyProxy[] assaultParty = new AssaultPartyProxy[N_ASSAULT_PARTIES];

        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultPartyProxy(i, configServerAddr, configServerPort);
        }

        MasterThief mt = new MasterThief((ImtMuseum) museum,
                (ImtConcentrationSite) concentrationSite,
                (ImtControlAndCollectionSite) controlCollectionSite,
                (ImtAssaultParty[]) assaultParty,
                (ImtGeneralRepository) genRepo);

        mt.start();

        try {
            mt.join();
            terminateServers(museum);
        } catch (InterruptedException ex) {
            Logger.getLogger(MasterThiefStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void terminateServers(MuseumInterface museum) {
        try {
            museum.signalShutdown();
        } catch (RemoteException ex) {
            Logger.getLogger(MasterThiefStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
