/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;
import interfaces.GeneralRepositoryInterface;
import interfaces.MuseumInterface;
import interfaces.AssaultPartyInterface;
import interfaces.Register;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
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

        
        SERVER_PORT = Integer.parseInt(args[0]);
        PARTY_ID = Integer.parseInt(args[1]);
        rmiServerHostname = args[2];
        rmiServerPort = Integer.parseInt(args[3]);

        /* look for the remote object by name in the remote host registry */
        String nameEntry = "GeneralRepository";
        GeneralRepositoryInterface genRepo = null;
        Registry registry = null;

        /* Locate General Repository */
        try {
            registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
            genRepo = (GeneralRepositoryInterface) registry.lookup(nameEntry);
        } catch (RemoteException e) {
            System.out.println("Exception thrown while locating log: " + e.getMessage() + "!");
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("Log is not registered: " + e.getMessage() + "!");
            System.exit(1);
        }

        /* instanciação e instalação do gestor de segurança */
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        AssaultParty assaultParty = new AssaultParty (PARTY_ID, (ImonitorsGeneralRepository) genRepo);
        AssaultPartyInterface assaultpartyInterface = null;

        try {
            assaultpartyInterface = (AssaultPartyInterface) UnicastRemoteObject.exportObject((Remote) assaultParty, SERVER_PORT);
        } catch (RemoteException e) {
            System.out.println("Excepção na geração do stub para o Museum: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O stub para o museum foi gerado!");

        Register reg = null;
        try {
            registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
        } catch (RemoteException e) {
            System.out.println("Excepção na criação do registo RMI: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O registo RMI foi criado!");

        try {
            reg = (Register) registry.lookup("RegisterHandler");
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }

        try {
            reg.bind("Museum", assaultpartyInterface);
        } catch (RemoteException e) {
            System.out.println("Excepção no registo do museum: " + e.getMessage());
            System.exit(1);
        } catch (AlreadyBoundException e) {
            System.out.println("O museum já está registado: " + e.getMessage());
            System.exit(1);
        }

        System.out.println("O bench foi registado!");

    }

}
