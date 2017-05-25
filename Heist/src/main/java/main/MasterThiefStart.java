/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import interfaces.AssaultPartyInterface;
import interfaces.ConcentrationSiteInterface;
import interfaces.ControlAndCollectionSiteInterface;
import interfaces.GeneralRepositoryInterface;
import interfaces.MuseumInterface;
import interfaces.Register;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import structures.Constants;

/**
 *
 * @author Ricardo Filipe
 */
public class MasterThiefStart {
    
    private static String rmiServerHostname;
    private static int rmiServerPort;
    private static int N_ASSAULT_PARTY_SIZE;
     
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in); 
        
        N_ASSAULT_PARTY_SIZE = Constants.N_ASSAULT_PARTIES;

         /* get location of the generic registry service */
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);

        GeneralRepositoryInterface genRepInterface = getGeneralRepository(registry);
        ConcentrationSiteInterface concSiteInterface = getConcentrationSite(registry);;
        MuseumInterface museumInterface = getMuseum(registry);
        ControlAndCollectionSiteInterface contCollSiteInterface = getControlAndCollectionSite(registry);
        AssaultPartyInterface assaultPartyInterface[] = new AssaultPartyInterface[N_ASSAULT_PARTY_SIZE];
        
        for(int i=0; i<N_ASSAULT_PARTY_SIZE; i++)
            assaultPartyInterface[i] = getAssaultParty(registry, "AssaultParty"+i);
        
        MasterThief masterThief = new MasterThief(museumInterface, concSiteInterface, 
                contCollSiteInterface, assaultPartyInterface, genRepInterface);
        
        System.out.println("Number of Master Thieves: 1 ");
       
        masterThief.start();
        System.out.printf("MasterThief start: \n");
        
         try { 
            masterThief.join ();
            System.out.printf("MasterThief join: \n");
        } catch (InterruptedException e) {}

        System.out.println("Alert Logger that I have finished!");

        try {
            genRepInterface.signalShutdown();
        } catch (RemoteException ex) {
            Logger.getLogger(MasterThiefStart.class.getName()).log(Level.SEVERE, null, ex);
        }
         /* print the result */
         System.out.println("Done!");
      }

    private static Registry getRegistry(String rmiServerHostname, int rmiServerPort) {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
        } catch (RemoteException ex) {
            System.err.print("Deu bronca");
            System.exit(1);
        }
        System.out.println("O registo RMI foi criado!");
        return registry;
    }

    private static Register getRegister(Registry registry) {
        Register reg = null;
        try {
            reg = (Register) registry.lookup("RegisterHandler");
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }
        return reg;
    }

    private static GeneralRepositoryInterface getGeneralRepository(Registry registry) {
        GeneralRepositoryInterface genRepo = null;
        /* look for the remote object by name in the remote host registry */
        String nameEntry = "GeneralRepository";

        try {
            /* Locate General Repository */
            genRepo = (GeneralRepositoryInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            System.err.print("Deu bronca");
        } catch (NotBoundException ex) {
            System.err.print("Deu bronca");
        }
        return genRepo;
    }

    private static ConcentrationSiteInterface getConcentrationSite(Registry registry) {
        ConcentrationSiteInterface concSite = null;
        /* look for the remote object by name in the remote host registry */
        String nameEntry = "ConcentrationSite";

        try {
            /* Locate General Repository */
            concSite = (ConcentrationSiteInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            System.err.print("Deu bronca");
        } catch (NotBoundException ex) {
            System.err.print("Deu bronca");
        }
        return concSite;
    }

    private static ControlAndCollectionSiteInterface getControlAndCollectionSite(Registry registry) {
        ControlAndCollectionSiteInterface contCollSite = null;
        /* look for the remote object by name in the remote host registry */
        String nameEntry = "ControlAndCollectionSite";

        try {
            /* Locate General Repository */
            contCollSite = (ControlAndCollectionSiteInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            System.err.print("Deu bronca");
        } catch (NotBoundException ex) {
            System.err.print("Deu bronca");
        }
        return contCollSite;
    }

    private static MuseumInterface getMuseum(Registry registry) {
        MuseumInterface museum = null;
        /* look for the remote object by name in the remote host registry */
        String nameEntry = "Museum";

        try {
            /* Locate General Repository */
            museum = (MuseumInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            System.err.print("Deu bronca");
        } catch (NotBoundException ex) {
            System.err.print("Deu bronca");
        }
        return museum;
    }

    private static AssaultPartyInterface getAssaultParty(Registry registry, String assParty) {
        AssaultPartyInterface assaultParty = null;
        /* look for the remote object by name in the remote host registry */
        String nameEntry = assParty;

        try {
            /* Locate General Repository */
            assaultParty = (AssaultPartyInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            System.err.print("Deu bronca");
        } catch (NotBoundException ex) {
            System.err.print("Deu bronca");
        }
        return assaultParty;
    }
}
