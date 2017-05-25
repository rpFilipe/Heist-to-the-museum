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

/**
 *
 * @author Ricardo Filipe
 */
public class ControlAndCollectionSiteStart {

    private static int SERVER_PORT;
    private static String rmiServerHostname;
    private static int rmiServerPort;

    /**
     * This class will launch one server listening one port and processing the
     * events.
     *
     * @param args
     */
    public static void main(String[] args) {

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

        ControlAndCollectionSite ccs = new ControlAndCollectionSite(genRepo, SERVER_PORT, SERVER_PORT, SERVER_PORT);
        ControlAndCollectionSiteInterface ccsInterface = null;

        try {
            ccsInterface = (ControlAndCollectionSite) UnicastRemoteObject.exportObject(ccs, SERVER_PORT);
        } catch (RemoteException e) {
            System.out.println("Excepção na geração do stub para o Museum: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O stub para o museum foi gerado!");

        try {
            reg.bind("controlAndCollectionSite", ccsInterface);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo do museum: " + e.getMessage());
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O museum já está registado: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O bench foi registado!");

    }

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
            Logger.getLogger(ControlAndCollectionSiteStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (NotBoundException ex) {
            Logger.getLogger(ControlAndCollectionSiteStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        return genRepo;
    }
}
