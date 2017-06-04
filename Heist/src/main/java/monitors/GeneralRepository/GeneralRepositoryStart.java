/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

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
import monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart;
import structures.Constants;
import static structures.Constants.getNameEntry;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public class GeneralRepositoryStart {

    private static int SERVER_PORT;
    private static String rmiServerHostname;
    private static int rmiServerPort;

    /**
     * This class will launch one server listening one port and processing the
     * events.
     * @param args args
     */
    public static void main(String[] args) {
        
        System.out.println("Starting General Repository");

        SERVER_PORT = Integer.parseInt(args[0]);
        rmiServerHostname = args[2];
        rmiServerPort = Integer.parseInt(args[3]);

        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);

        /* instanciação e instalação do gestor de segurança */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        GeneralRepository genRep = new GeneralRepository("HeistToTheMuseum_Log");
        GeneralRepositoryInterface genRepInterface = null;

        try {
            genRepInterface = (GeneralRepositoryInterface) UnicastRemoteObject.exportObject(genRep, SERVER_PORT);
        } catch (RemoteException e) {
            System.out.println("Excepção na geração do stub para o general repository: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O stub para o general repository foi gerado!");

        try {
            String xmlFile = Constants.xmlFile;
            reg.bind(getNameEntry("GeneralRepository", xmlFile), genRepInterface);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo do general repository: " + e.getMessage());
            System.exit(1);
        } catch (AlreadyBoundException ex) {
            Logger.getLogger(GeneralRepositoryStart.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("O general repository foi registado!");

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
        try {
            reg = (Register) registry.lookup(getNameEntry("Rmi", xmlFile));
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }
        return reg;
    }
}
