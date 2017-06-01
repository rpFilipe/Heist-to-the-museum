/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

import States.MasterThiefStates;
import States.OrdinaryThiefState;
import interfaces.AssaultPartyInterface;
import interfaces.ConcentrationSiteInterface;
import interfaces.ControlAndCollectionSiteInterface;
import interfaces.GeneralRepositoryInterface;
import interfaces.MuseumInterface;
import interfaces.Register;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart;
import structures.Constants;
import structures.VectorClock;

/**
 * General Repository Instance.
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */


public class GeneralRepository implements GeneralRepositoryInterface {

    private static PrintWriter pw;
    private final File log;
    String filename;
    private int masterThiefState;
    private int[] roomId;
    private Room[] rooms;
    private HashMap<Integer, Thief> thiefMap;
    private int[][] partyElement;
    private int canvasCollected = 0;
    private int N_ASSAULT_PARTIES;
    private int N_ROOMS;
    private int ASSAULT_PARTY_SIZE;
    private int N_ORD_THIEVES;
    private boolean DEBUG = false;
    private VectorClock vc;

    /**
     * Creates a new General Repository
     * @param logname Name of the File to store the simulation log.
     */
    public GeneralRepository(String logname) {
        N_ASSAULT_PARTIES = Constants.N_ASSAULT_PARTIES;
        N_ROOMS = Constants.N_ROOMS;
        ASSAULT_PARTY_SIZE = Constants.ASSAULT_PARTY_SIZE;
        N_ORD_THIEVES = Constants.N_ORD_THIEVES;
        masterThiefState = MasterThiefStates.PLANNING_THE_HEIST;
        roomId = new int[N_ASSAULT_PARTIES];
        rooms = new Room[N_ROOMS];
        thiefMap = new HashMap<>();
        partyElement = new int[N_ASSAULT_PARTIES][ASSAULT_PARTY_SIZE];
        this.vc = new VectorClock(7, 0); // 1 master + 6 ordinary

        for (int i = 0; i < roomId.length; i++) {
            roomId[i] = -1;
        }

        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            for (int j = 0; j < ASSAULT_PARTY_SIZE; j++) {
                partyElement[i][j] = -1;
            }

        }
        int ts = (int) System.currentTimeMillis();
        filename = logname;

        this.log = new File(filename);
        InitializeLog();

    }

    private synchronized void FirstLine() {
        Thief currentT;
        pw.printf("\n%4d", masterThiefState);
        for (int i = 0; i < N_ORD_THIEVES; i++) {
            currentT = thiefMap.get(i);
            pw.printf("    %4d %1c %2d", currentT.state, currentT.situation, currentT.speed);
        }
    }

    private synchronized void SecondLine() {
        Thief currentT;
        pw.printf("   ");
        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            if (roomId[i] == -1) {
                pw.printf("     ");
            } else {
                pw.printf(" %3d ", roomId[i] + 1);
            }
            for (int j = 0; j < ASSAULT_PARTY_SIZE; j++) {
                if (partyElement[i][j] == -1) {
                    pw.printf("           ");
                } else {
                    currentT = thiefMap.get(partyElement[i][j]);
                    pw.printf(" %2d %3d %2d ", currentT.id + 1, currentT.position, currentT.canvas);
                }
            }
        }
        pw.printf(" ");
        for (Room room : rooms) {
            pw.printf("    %2d %2d", room.paitings_left, room.distance);
        }
        pw.println();
    }

    /**
     * Update the State of the Ordinary Thief and print the updated Status in
     * the log file.
     *
     * @param thiefId Id of the Ordinary Thief
     * @param state updated State
     */
    @Override
    public synchronized VectorClock updateThiefState(int thiefId, int state, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        Thief t = thiefMap.get(thiefId);
        t.state = state;
        printStatus();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to update state the Master Thief in the General Repository.
     *
     * @param state Current state the Master Thief.
     */
    @Override
    public synchronized VectorClock updateMThiefState(int state, VectorClock vc) {
        this.vc.update(vc);
        masterThiefState = state;
        printStatus();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to update state the Thief's situation in the General Repository.
     *
     * @param thiefId Id of the Thief.
     * @param situation Current situation.
     */
    @Override
    public synchronized VectorClock updateThiefSituation(int thiefId, char situation, VectorClock vc) {
        this.vc.update(vc);
        Thief t = thiefMap.get(thiefId);
        t.situation = situation;
        printStatus();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to set the target room to a specific Assault Party in the General
     * Repository.
     *
     * @param partyId Id of the Party.
     * @param room target room of the Assault Party.
     */
    @Override
    public synchronized VectorClock setRoomIdAP(int partyId, int room, VectorClock vc) {
        this.vc.update(vc);
        roomId[partyId] = room;
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to set the number of painting stolen in the entire heist in the
     * General Repository.
     *
     * @param toalCanvas Number of canvas "borrowed" from the Museum.
     */
    @Override
    public synchronized VectorClock setCollectedCanvas(int toalCanvas, VectorClock vc) {
        this.vc.update(vc);
        canvasCollected = toalCanvas;
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to set the Room attributes in the General Repository.
     *
     * @param roomId Id of the Room.
     * @param distance Distance from the outside to the Room.
     * @param paitings Number of paintings present on the room.
     */
    @Override
    public synchronized VectorClock setRoomAtributes(int roomId, int distance, int paitings, VectorClock vc) {
        this.vc.update(vc);
        rooms[roomId] = new Room(paitings, distance);
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to add the Ordinary Thief attributes in the General Repository.
     *
     * @param thiefId Id of the Thief.
     * @param speed Maximum displacement of the Thief.
     */
    @Override
    public synchronized VectorClock addThief(int thiefId, int speed, VectorClock vc) {
        this.vc.update(vc);
        System.out.println("Thief added");
        thiefMap.put(thiefId, new Thief(thiefId, speed));
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to put a Ordinary Thief a specified Assault Party in the General
     * Repository.
     *
     * @param partyId Id of the Assault Party.
     * @param thiefId Id of the Thief.
     * @param elemId Thief Id in the Assault Party.
     */
    @Override
    public synchronized VectorClock setPartyElement(int partyId, int thiefId, int elemId, VectorClock vc) {
        this.vc.update(vc);
        partyElement[partyId][elemId] = thiefId;
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to clear the elements of a Assault Party in the General
     * Repository.
     *
     * @param partyId Id of the Assault Party.
     */
    @Override
    public synchronized VectorClock clearParty(int partyId, VectorClock vc) {
        this.vc.update(vc);
        for (int i = 0; i < ASSAULT_PARTY_SIZE; i++) {
            partyElement[partyId][i] = -1;
            roomId[partyId] = -1;
        }
        printStatus();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to update the position of a Ordinary Thief in the General
     * Repository.
     *
     * @param thiefId Id of the Thief thar Invoked the method.
     * @param position Current position of the Thief.
     */
    @Override
    public synchronized VectorClock updateThiefPosition(int thiefId, int position, VectorClock vc) {
        this.vc.update(vc);
        Thief currentT = thiefMap.get(thiefId);
        currentT.position = position;
        printStatus();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Method to update the contents of a Ordinary Thief cylinder in the General
     * Repository.
     *
     * @param thiefId Id of the thief that invoked the method.
     * @param hasCanvas the state of the thief cylinder.
     */
    @Override
    public synchronized VectorClock updateThiefCylinder(int thiefId, boolean hasCanvas, VectorClock vc) {
        this.vc.update(vc);
        Thief currentT = thiefMap.get(thiefId);
        currentT.canvas = hasCanvas ? 1 : 0;
        printStatus();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    private synchronized void printStatus() {
        if (DEBUG) {
            return;
        }
        FirstLine();
        pw.println();
        SecondLine();
    }

    private synchronized void InitializeLog() {
        if (DEBUG) {
            return;
        }
        try {
            pw = new PrintWriter(log);
            pw.println("                               Heist to the Museum - Description of the internal state ");
            pw.println();

            String head = "MstT   ";
            for (int i = 1; i <= 6; i++) {
                head += "Thief " + Integer.toString(i);
                head += "      ";
            }
            pw.println(head);

            head = "Stat    ";
            for (int i = 1; i <= N_ORD_THIEVES; i++) {
                head += "Stat S MD";
                head += "    ";
            }
            pw.println(head);

            head = "";
            for (int i = 1; i <= N_ASSAULT_PARTIES; i++) {
                head += "                    Assault party " + Integer.toString(i);
                head += "   ";
            }
            head += "                        Museum";
            pw.println(head);

            head = "           ";
            for (int i = 1; i <= ASSAULT_PARTY_SIZE; i++) {
                head += "Elem " + Integer.toString(i);
                head += "     ";
            }

            head += "     ";
            for (int i = 1; i <= ASSAULT_PARTY_SIZE; i++) {
                head += "Elem " + Integer.toString(i);
                head += "     ";
            }

            head += "  ";
            for (int i = 1; i <= N_ROOMS; i++) {
                head += "Room " + Integer.toString(i);
                head += "   ";
            }

            pw.println(head);

            head = "  ";
            for (int i = 1; i <= N_ASSAULT_PARTIES; i++) {
                head += "  RId";
                for (int j = 1; j <= ASSAULT_PARTY_SIZE; j++) {
                    head += "  Id Pos Cv";
                }
            }
            head += "  ";
            for (int i = 1; i <= N_ROOMS; i++) {
                head += "    NP DT";
            }
            pw.println(head);

            pw.flush();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        }
    }

    /**
     * Method to finalize the log.
     */
    @Override
    public synchronized VectorClock FinalizeLog(VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        
        if (DEBUG) {
            return returnClk;
        }
        pw.printf("My friends, tonight's effort produced %2d priceless paintings!", canvasCollected);
        pw.println("\nLegend:");
        pw.println("MstT Sta - state of the master thief");
        pw.println("Thief # Stat - state of the ordinary thief # (# - 1 .. 6)");
        pw.println("Thief # S - situation of the ordinary thief  # (# - 1 .. 6) either 'W' (waiting to join a party) or 'P' (in party) ");
        pw.println("Thief # MD - maximum displacement of the ordinary thief # (# - 1 .. 6) a random number between 2 and 6 ");
        pw.println("Assault party # RId - assault party # (# - 1,2) elem # (# - 1 .. 3) room identification (1 .. 5)");
        pw.println("Assault party # Elem # Id - assault party # (# - 1,2) elem # (# - 1 .. 3) member identification (1 .. 6) ");
        pw.println("Assault party # Elem # Pos - assault party # (# - 1,2) elem # (# - 1 .. 3) present position (0 .. DT RId) ");
        pw.println("Assault party # Elem # Cv - assault party # (# - 1,2) elem # (# - 1 .. 3) carrying a canvas (0,1)");
        pw.println("Museum Room # NP - room identification (1 .. 5) number of paintings presently hanging on the walls");
        pw.println("Museum Room # DT - room identification (1 .. 5) distance from outside gathering site, a random number between 15 and 30");
        pw.flush();
        pw.close();
        
        return returnClk;
    }

    /**
     * Method to set the number of paintings in the General Repository.
     *
     * @param id Id of the Room
     * @param paitings Number of paintings in the Room.
     */
    @Override
    public synchronized VectorClock setRoomCanvas(int id, int paitings, VectorClock vc) {
        this.vc.update(vc);
        rooms[id].paitings_left = paitings;
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
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
    
    @Override
    public void terminateServers(){
        
        /* Just for test - Put in the file for example */
        String rmiServerHostname = "localhost";
        int rmiServerPort = 4000;
        String nameEntryBase = "RegisterHandler";
        String nameEntryObject = "GeneralRepository";
        
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);
        
        /* Shutdown Concentration Site */
        try
        {
            ConcentrationSiteInterface concSiteInterface = (ConcentrationSiteInterface) registry.lookup ("ConcentrationSite");
            concSiteInterface.signalShutdown();
        }
        catch (RemoteException e)
        { 
            System.out.println("Exception thrown while locating concentration site: " + e.getMessage () + "!");
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (NotBoundException e)
        { 
            System.out.println("Concentration Site is not registered: " + e.getMessage () + "!");
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        
        /* Shutdown Control And Collection Site */
        try
        {
            ControlAndCollectionSiteInterface contCollSiteInterface = (ControlAndCollectionSiteInterface) registry.lookup ("controlAndCollectionSite");
            contCollSiteInterface.signalShutdown();
        }
        catch (RemoteException e)
        { 
            System.out.println("Exception thrown while locating control and collection site: " + e.getMessage () + "!");
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (NotBoundException e)
        { 
            System.out.println("Control And Collection Site is not registered: " + e.getMessage () + "!");
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        
        /* Shutdown Control And Collection Site */
        try
        {
            ControlAndCollectionSiteInterface contCollSiteInterface = (ControlAndCollectionSiteInterface) registry.lookup ("controlAndCollectionSite");
            contCollSiteInterface.signalShutdown();
        }
        catch (RemoteException e)
        { 
            System.out.println("Exception thrown while locating control and collection site: " + e.getMessage () + "!");
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        catch (NotBoundException e)
        { 
            System.out.println("Control And Collection Site is not registered: " + e.getMessage () + "!");
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        
        /* Shutdown Assault Parties */
        for(int i=0; i<Constants.N_ASSAULT_PARTIES; i++){
            try{
                AssaultPartyInterface assaultPartyInterface = (AssaultPartyInterface) registry.lookup ("AssaultParty"+i);
                assaultPartyInterface.signalShutdown();
            } catch (RemoteException e) { 
                System.out.println("Exception thrown while locating assault party: " + e.getMessage () + "!");
                Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
            }
            catch (NotBoundException e)
            { 
                System.out.println("AssaultParty"+i+" is not registered: " + e.getMessage () + "!");
                Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
            }
        }        
        
       /* Shutdown General Repository */
        try {
            reg = (Register) registry.lookup(nameEntryBase);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }
        try {
            // Unregister ourself
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("GeneralRepository registration exception: " + e.getMessage());
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            System.out.println("GeneralRepository not bound exception: " + e.getMessage());
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            // Unexport; this will also remove us from the RMI runtime
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            Logger.getLogger(GeneralRepository.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //FinalizeLog();
        
        System.out.println("General Repository Log written succesfully!!");
    }

    private class Room {

        private Room(int paitings, int distance) {
            this.distance = distance;
            this.paitings_left = paitings;
        }
        private int paitings_left, distance;
    }

    private class Thief {

        private int id, position, canvas, state, speed;
        private char situation;

        private Thief(int id, int speed) {
            this.id = id;
            this.position = 0;
            this.speed = speed;
            this.situation = 'W';
            this.canvas = 0;
            this.state = OrdinaryThiefState.OUTSIDE;
        }
    }
}
