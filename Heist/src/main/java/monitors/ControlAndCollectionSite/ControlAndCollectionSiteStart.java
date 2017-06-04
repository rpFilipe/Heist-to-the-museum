/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

import interfaces.ControlAndCollectionSiteInterface;
import interfaces.GeneralRepositoryInterface;
import interfaces.Register;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import structures.Constants;
import static structures.Constants.getNameEntry;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public class ControlAndCollectionSiteStart {

    private static int SERVER_PORT;
    private static String rmiServerHostname;
    private static int rmiServerPort;
    private static int N_ROOMS; 
    private static int N_ASSAULT_PARTIES;
    private static int ASSAULT_PARTY_SIZE;

    /**
     * This class will launch one server listening one port and processing the
     * events.
     * @param args args
     */
    public static void main(String[] args) {

        System.out.println("Starting Control And Collection Site");
        N_ROOMS = Constants.N_ROOMS;
        N_ASSAULT_PARTIES = Constants.N_ASSAULT_PARTIES;
        ASSAULT_PARTY_SIZE = Constants.ASSAULT_PARTY_SIZE;
        
        SERVER_PORT = Integer.parseInt(args[0]);
        rmiServerHostname = args[1];
        rmiServerPort = Integer.parseInt(args[2]);

        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);
        GeneralRepositoryInterface genRepo = getGeneralRepository(registry);

        /* instanciação e instalação do gestor de segurança */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        ControlAndCollectionSite ccs = new ControlAndCollectionSite(genRepo, N_ROOMS, N_ASSAULT_PARTIES, ASSAULT_PARTY_SIZE);
        ControlAndCollectionSiteInterface ccsInterface = null;

        try {
            ccsInterface = (ControlAndCollectionSiteInterface) UnicastRemoteObject.exportObject(ccs, SERVER_PORT);
        } catch (RemoteException e) {
            System.out.println("Excepção na geração do stub para o Control And Collection Site: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O stub para o Control And Collection Site foi gerado!");

        try {
            String xmlFile = Constants.xmlFile;
            String nameEntryObject = getNameEntry("ControlAndCollectionSite", xmlFile);
            reg.bind(nameEntryObject, ccsInterface);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo do Control And Collection Site: " + e.getMessage());
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O Control And Collection Site já está registado: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O Control And Collection Site foi registado!");

    }

    /**
     * This function is used to register it with the local registry service.
     * @param rmiServerHostname Rmi Server Host Name.
     * @param rmiServerPort Rmi Server port.
     * @return registry.
     */
    private static Registry getRegistry(String rmiServerHostname, int rmiServerPort) {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
        } catch (RemoteException ex) {
            Logger.getLogger(ControlAndCollectionSiteStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        System.out.println("O registo RMI foi criado!");
        return registry;
    }

    /**
    This function us used to return a reference, a stub, for the remote object associated with the specified name.
    * @param registry registry.
    * @return the register reg.
    */
    private static Register getRegister(Registry registry) {
        Register reg = null;
        String xmlFile = Constants.xmlFile;
        String nameEntryObject = getNameEntry("Rmi", xmlFile);
        try {
            reg = (Register) registry.lookup(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }
        return reg;

    }

    /**
    This function us used to return a reference, a stub, for the remote object associated with the specified name.
    * @param registry registry.
    * @return the general repository interface.
    */
    private static GeneralRepositoryInterface getGeneralRepository(Registry registry) {
        GeneralRepositoryInterface genRepo = null;
        /* look for the remote object by name in the remote host registry */
        String xmlFile = Constants.xmlFile;
        String nameEntry = getNameEntry("GeneralRepository", xmlFile);

        try {
            /* Locate General Repository */
            genRepo = (GeneralRepositoryInterface) registry.lookup(nameEntry);
        } catch (RemoteException ex) {
            Logger.getLogger(ControlAndCollectionSiteStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (NotBoundException ex) {
            Logger.getLogger(ControlAndCollectionSiteStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        return genRepo;
    }
}