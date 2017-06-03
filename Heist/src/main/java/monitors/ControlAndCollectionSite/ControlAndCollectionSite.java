/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

import States.PartyStates;
import States.RoomStates;
import interfaces.ControlAndCollectionSiteInterface;
import interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import structures.Constants;
import static structures.Constants.getHost;
import static structures.Constants.getNameEntry;
import static structures.Constants.getPort;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class ControlAndCollectionSite implements ControlAndCollectionSiteInterface {

    private int canvasCollected;
    private int canvasToCollect;
    private int[] roomStates;
    private int[] partyStates;
    private boolean masterThiefBusy;
    private boolean thiefArrived;
    private int targetRoom;
    private int partyToDeploy;
    private int[] partyArrivedThiefs;
    private boolean partyFree;
    private int thievesWaiting;
    private ImonitorsGeneralRepository genRepo;
    private int N_ROOMS;
    private int N_ASSAULT_PARTIES;
    private int ASSAULT_PARTY_SIZE;
    private VectorClock vc;
    private VectorClock clkToSend;

    /**
     * Create a Control And Collection Instance.
     *
     * @param genRepo General Repository instance
     * @param nrooms Number of Rooms in the Simulation
     * @param n_assault_parties Number of Assault Parties in the Simulation
     * @param assault_party_size Number of Ordinary Thieves in each Assault
     * Party.
     */
    public ControlAndCollectionSite(ImonitorsGeneralRepository genRepo, int nrooms, int n_assault_parties, int assault_party_size) {
        N_ROOMS = nrooms;
        N_ASSAULT_PARTIES = n_assault_parties;
        ASSAULT_PARTY_SIZE = assault_party_size;
        roomStates = new int[N_ROOMS];
        partyFree = true;
        partyArrivedThiefs = new int[N_ASSAULT_PARTIES];
        canvasToCollect = 0;
        canvasCollected = 0;
        thievesWaiting = 0;
        partyStates = new int[N_ASSAULT_PARTIES];
        masterThiefBusy = false;
        for (int i = 0; i < N_ROOMS; i++) {
            roomStates[i] = RoomStates.NOT_VISITED;
        }
        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            partyStates[i] = PartyStates.EMPTY;
        }
        thiefArrived = false;
        targetRoom = 0;
        this.genRepo = genRepo;
        this.vc = new VectorClock(7, 0);
    }

    /**
     * Method to send the Master Thief to a idle state waiting for a Ordinary
     * Thief to return from the Museum.
     */
    @Override
    public synchronized VectorClock takeARest(VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        masterThiefBusy = false;
        notifyAll();

        while (!thiefArrived) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        thiefArrived = false;
        return clkToSend;
    }

    /**
     * Method to get the canvas from the Ordinary Thief.
     */
    @Override
    public synchronized VectorClock collectCanvas(VectorClock vc) throws RemoteException,InterruptedException{
        //canvasToCollect--;
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        return clkToSend;
    }

    /**
     * Method used to give the content of the canvas cylinder to the Master
     * Thief.
     *
     * @param thiefId Id of the Thief that invoked the method.
     * @param hasCanvas Content of the cylinder.
     * @param roomId Id of the Room that was being target.
     * @param partyId Id of the Assault Party that Ordinary Thief belonged to.
     */
    @Override
    public synchronized VectorClock handACanvas(int thiefId, boolean hasCanvas, int roomId, int partyId, VectorClock vc) throws RemoteException, InterruptedException {
        //canvasToCollect++;
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        while (masterThiefBusy) {
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error hansAcanvas");
            }
        }

        if (hasCanvas) {
            canvasCollected++;
        }

        if (!hasCanvas) {
            roomStates[roomId] = RoomStates.ROOM_EMPTY;
        } else if (roomStates[roomId] != RoomStates.ROOM_EMPTY) {
            roomStates[roomId] = RoomStates.ROB_AGAIN;
        }

        partyArrivedThiefs[partyId]++;

        if (partyArrivedThiefs[partyId] == ASSAULT_PARTY_SIZE) {
            masterThiefBusy = true;
            partyStates[partyId] = PartyStates.EMPTY;
            genRepo.clearParty(partyId, vc);
            partyArrivedThiefs[partyId] = 0;
            thiefArrived = true;
            notifyAll();
        }
        return clkToSend;
    }

    /**
     * Method to get the next Room to target.
     *
     * @return Id of the target Room.
     */
    @Override
    public synchronized Pair<VectorClock, Integer> getTargetRoom(VectorClock vc) throws RemoteException,InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        int room = chooseTargetRoom();
        if (room != -1) {
            roomStates[room] = RoomStates.BEING_STOLEN;
        }
        return new Pair(clkToSend, room);
    }

    /**
     * Method to get the Assault Party to be deployed..
     *
     * @return Id of the Assault Party to be prepared.
     */
    @Override
    public synchronized Pair<VectorClock, Integer> getPartyToDeploy(VectorClock vc) throws RemoteException,InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        int party = choosePartyToDeploy();
        if (party != -1) {
            partyStates[party] = PartyStates.DEPLOYED;
        }
        return new Pair(clkToSend, party);
    }

    private synchronized int chooseTargetRoom() {
        for (int i = 0; i < N_ROOMS; i++) {
            if (roomStates[i] == RoomStates.NOT_VISITED || roomStates[i] == RoomStates.ROB_AGAIN) {
                return i;
            }
        }
        return -1;
    }

    private synchronized int choosePartyToDeploy() {
        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            if (partyStates[i] == PartyStates.EMPTY) {
                partyArrivedThiefs[i] = 0;
                return i;
            }
        }
        return -1;
    }

    private synchronized boolean allRoomsEmpty() {
        for (int i = 0; i < roomStates.length; i++) {
            if (roomStates[i] != RoomStates.ROOM_EMPTY) {
                return false;
            }
        }
        return true;
    }

    private boolean allPartiesFree() {
        for (int i = 0; i < partyStates.length; i++) {
            if (partyStates[i] != PartyStates.EMPTY) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if all the MasterThief can rest for a bit.
     *
     * @return MasterThief has to wait
     */
    @Override
    public synchronized Pair<VectorClock, Boolean> waitingNedded(VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        partyToDeploy = choosePartyToDeploy();
        targetRoom = chooseTargetRoom();
        boolean b = (canvasToCollect > 0 || partyToDeploy == -1 || targetRoom == -1);
        return new Pair(clkToSend, b);
    }

    /**
     * Check if all the Rooms have been cleared.
     *
     * @return heist completed
     */
    @Override
    public synchronized Pair<VectorClock, Boolean> isHeistCompleted(VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        genRepo.setCollectedCanvas(canvasCollected, vc);
        boolean b = allRoomsEmpty() && allPartiesFree();
        return new Pair(clkToSend, b);
    }

    @Override
    public void signalShutdown() throws RemoteException {
        
        String xmlFile = Constants.xmlFile;
        
        String rmiServerHostname = getHost("Rmi", xmlFile);
        int rmiServerPort = getPort("Rmi", xmlFile);
        String nameEntryObject = getNameEntry("ControlAndCollectionSite", xmlFile);
        
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);

        try {
            // Unregister ourself
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("Control And Collection Site registration exception: " + e.getMessage());
            Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            System.out.println("Control And CollectionSite not bound exception: " + e.getMessage());
            Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            // Unexport; this will also remove us from the RMI runtime
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            Logger.getLogger(ControlAndCollectionSite.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Control And Collection Site closed.");
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
}