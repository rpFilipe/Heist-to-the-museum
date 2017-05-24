/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import interfaces.GeneralRepositoryInterface;
import interfaces.MuseumInterface;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty.IotAssaultParty;
import monitors.ConcentrationSite.IotConcentrationSite;
import monitors.ControlAndCollectionSite.IotControlAndCollectionSite;
import monitors.GeneralRepository.IotGeneralRepository;
import monitors.Museum.IotMuseum;
import monitors.Museum.MuseumStart;

/**
 *
 * @author Ricardo Filipe
 */
public class OrdinaryThievesStart {

    private static int N_ORD_THIEVES;
    private static int N_ASSAULT_PARTIES;
    private static String rmiServerHostname;
    private static int rmiServerPort;

    /**
     * Start the Ordinary Thief Life.
     *
     * @param args
     */
    public static void main(String[] args) {

        rmiServerHostname = args[0];
        rmiServerPort = Integer.parseInt(args[1]);

        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);

        MuseumInterface museum = getMuseum(registry);
        GeneralRepositoryInterface genRepo = getGeneralRepository(registry);

        ControlAndCollectionSiteProxy controlCollectionSite = new ControlAndCollectionSiteProxy(configServerAddr, configServerPort);
        ConcentrationSiteProxy concentrationSite = new ConcentrationSiteProxy(configServerAddr, configServerPort);
        AssaultPartyProxy[] assaultParty = new AssaultPartyProxy[N_ASSAULT_PARTIES];
        OrdinaryThief[] ordinaryThives = new OrdinaryThief[N_ORD_THIEVES];

        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultPartyProxy(i, configServerAddr, configServerPort);
        }

        for (int i = 0; i < N_ORD_THIEVES; i++) {
            ordinaryThives[i] = new OrdinaryThief(i, max_speed, min_speed,
                    (IotMuseum) museum,
                    (IotConcentrationSite) concentrationSite,
                    (IotControlAndCollectionSite) controlCollectionSite,
                    (IotAssaultParty[]) assaultParty,
                    (IotGeneralRepository) genRepo);
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

    private static MuseumInterface getMuseum(Registry registry) {
        MuseumInterface museum = null;
        try {
            museum = (MuseumInterface) registry.lookup("museum");
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating log: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Log is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }
        return museum;
    }

    private static Registry getRegistry(String rmiServerHostname, int rmiServerPort) {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
        } catch (RemoteException ex) {
            Logger.getLogger(OrdinaryThievesStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        System.out.println("O registo RMI foi criado!");
        return registry;
    }

    private static GeneralRepositoryInterface getGeneralRepository(Registry registry) {
        GeneralRepositoryInterface genRepo = null;
        /* look for the remote object by name in the remote host registry */
        String nameEntry = "GeneralRepository";

        try {
            /* Locate General Repository */
            genRepo = (GeneralRepositoryInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            Logger.getLogger(MuseumStart.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotBoundException ex) {
            Logger.getLogger(MuseumStart.class.getName()).log(Level.SEVERE, null, ex);
        }
        return genRepo;
    }
}
