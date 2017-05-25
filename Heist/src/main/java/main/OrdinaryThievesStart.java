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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import structures.Constants;

/**
 *
 * @author Ricardo Filipe
 */
public class OrdinaryThievesStart {

    private static int N_ORD_THIEVES;
    private static int N_ASSAULT_PARTY_SIZE;
    private static int MAX_SPEED;
    private static int MIN_SPEED;
    private static String rmiServerHostname;
    private static int rmiServerPort;

    /**
     * Start the Ordinary Thief Life.
     *
     * @param args
     */
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        N_ORD_THIEVES = Constants.N_ORD_THIEVES;
        N_ASSAULT_PARTY_SIZE = Constants.ASSAULT_PARTY_SIZE;
        MAX_SPEED = Constants.MAX_THIEF_SPEED;
        MIN_SPEED = Constants.MIN_THIEF_SPEED;
        rmiServerHostname = args[0];
        rmiServerPort = Integer.parseInt(args[1]);

        ArrayList<OrdinaryThief> ordThieves = new ArrayList<>(N_ORD_THIEVES);
        /* get location of the generic registry service */
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);

        GeneralRepositoryInterface genRepInterface = getGeneralRepository(registry);
        ConcentrationSiteInterface concSiteInterface = getConcentrationSite(registry);;
        ControlAndCollectionSiteInterface contCollSiteInterface = getControlAndCollectionSite(registry);
        MuseumInterface museumInterface = getMuseum(registry);
        AssaultPartyInterface assaultPartyInterface[] = new AssaultPartyInterface[N_ASSAULT_PARTY_SIZE];
        
        for(int i=0; i<N_ASSAULT_PARTY_SIZE; i++)
            assaultPartyInterface[i] = getAssaultParty(registry, "AssaultParty"+i);
        
        for(int i=0; i<ordThieves.size(); i++){
            ordThieves.add(new OrdinaryThief(i, MAX_SPEED, MIN_SPEED, museumInterface, concSiteInterface, 
                    contCollSiteInterface, assaultPartyInterface, genRepInterface));
        }

        System.out.println("Number of ordinary thieves: " +ordThieves.size());  
       
        for(int i = 0; i < ordThieves.size(); i++){
            ordThieves.get(i).start();
            System.out.printf("OrdinaryThief start: %d\n", i);
        }
        
        for(int i = 0; i < ordThieves.size(); i++){
            try {
                ordThieves.get(i).join();
                System.out.printf("OrdinaryThief join: %d\n", i);
            } catch(InterruptedException e) {}
        }
        
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
