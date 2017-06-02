/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;
import interfaces.GeneralRepositoryInterface;
import interfaces.AssaultPartyInterface;
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
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class AssaultPartyStart {

    private static int PARTY_ID;
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
        System.out.println("Starting Assault Party " + args[1]);
        SERVER_PORT = Integer.parseInt(args[0]);
        PARTY_ID = Integer.parseInt(args[1]);
        rmiServerHostname = args[2];
        rmiServerPort = Integer.parseInt(args[3]);
        
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);
        GeneralRepositoryInterface genRepo = getGeneralRepository(registry);

        /* instanciação e instalação do gestor de segurança */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        AssaultParty assaultParty = new AssaultParty (PARTY_ID, (ImonitorsGeneralRepository) genRepo);
        AssaultPartyInterface assaultpartyInterface = null;

        try {
            assaultpartyInterface = (AssaultPartyInterface) UnicastRemoteObject.exportObject(assaultParty, SERVER_PORT);
        } catch (RemoteException e) {
            System.out.println("Excepção na geração do stub para a assault Party: " + e.getMessage());
            System.exit(1);
        }

        System.out.printf("O stub para a assault Party %d foi gerado!", PARTY_ID);

        try {
            reg.bind("AssaultParty"+PARTY_ID, assaultpartyInterface);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo da assautl party: " + e.getMessage());
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("A assault party já está registada: " + e.getMessage());
            System.exit(1);
        }
        System.out.printf("\nA assault party %d foi registada!\n", PARTY_ID);
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
