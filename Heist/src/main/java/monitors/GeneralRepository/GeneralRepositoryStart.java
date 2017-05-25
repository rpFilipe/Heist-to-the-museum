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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart;

/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepositoryStart {

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
            genRepInterface = (GeneralRepositoryInterface) UnicastRemoteObject.exportObject((Remote) genRep, SERVER_PORT);
        } catch (RemoteException e) {
            System.out.println("Excepção na geração do stub para o general repository: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O stub para o general repository foi gerado!");

        try {
            reg.bind("GeneralRepository", genRepInterface);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo do general repository: " + e.getMessage());
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O general repository já está registado: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O general repository foi registado!");

    }

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
}
