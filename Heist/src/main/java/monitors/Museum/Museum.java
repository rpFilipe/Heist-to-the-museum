package monitors.Museum;

import interfaces.MuseumInterface;
import interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import structures.Constants;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class Museum implements MuseumInterface {

    private int N_ROOMS;
    private int MAX_ROOM_DISTANCE;
    private int MIN_ROOM_DISTANCE;
    private int MAX_PAITING_PER_ROOM;
    private int MIN_PAITING_PER_ROOM;
    private static int roomsCreated = 0;
    private Room[] rooms;
    private int totalPaitings;
    private ImonitorsGeneralRepository genRepo;
    private VectorClock vc;

    /**
     * Constructor to create a new Museum
     *
     * @param genRepo General Repository Instance
     * @param N_ROOMS Number of Rooms in the simulation.
     * @param MAX_ROOM_DISTANCE Maximum distance a Room can be in the
     * simulation.
     * @param MIN_ROOM_DISTANCE Minimum distance a Room can be in the
     * simulation.
     * @param MAX_PAITING_PER_ROOM Maximum number of paintings in a given Room.
     * @param MIN_PAITING_PER_ROOM Minimum number of paintings in a given Room.
     */
    public Museum(ImonitorsGeneralRepository genRepo){
        this.N_ROOMS = Constants.N_ROOMS;
        this.MAX_ROOM_DISTANCE = Constants.MAX_ROOM_DISTANCE;
        this.MIN_ROOM_DISTANCE = Constants.MIN_ROOM_DISTANCE;
        this.MAX_PAITING_PER_ROOM = Constants.MAX_PAITING_PER_ROOM;
        this.MIN_PAITING_PER_ROOM = Constants.MIN_PAITING_PER_ROOM;
        rooms = new Room[N_ROOMS];
        totalPaitings = 0;
        roomsCreated = 0;
        this.vc = new VectorClock(7, 0);
        this.genRepo = genRepo;
        for (int i = 0; i < N_ROOMS; i++) {
            rooms[i] = new Room();
            totalPaitings += rooms[i].paintings;
        }
        System.out.printf("\nThe museum has %d paitings\n", totalPaitings);
    }

    private class Room {

        private int distance;
        private int paintings;
        private int id;

        public Room(){
            this.distance = new Random().nextInt(MAX_ROOM_DISTANCE - MIN_ROOM_DISTANCE) + MIN_ROOM_DISTANCE;
            this.paintings = new Random().nextInt(MAX_PAITING_PER_ROOM - MIN_PAITING_PER_ROOM) + MIN_PAITING_PER_ROOM;
            this.id = roomsCreated;
            try {
                genRepo.setRoomAtributes(id, distance, paintings, vc);
            } catch (RemoteException ex) {
                Logger.getLogger(Museum.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Museum.class.getName()).log(Level.SEVERE, null, ex);
            }
            roomsCreated++;
        }

        private synchronized boolean rollACanvas() throws RemoteException, InterruptedException {
            boolean res = paintings > 0;
            paintings--;
            if (paintings < 0) {
                paintings = 0;
            }
            genRepo.setRoomCanvas(id, paintings, vc);
            return res;
        }

        public int getDistance() {
            return distance;
        }
    }

    /**
     * This method is used to get a canvas from a room.
     *
     * @param room
     * @return true if the room still has canvas to be stolen.
     */
    @Override
    public Pair<VectorClock, Boolean> rollACanvas(int room, VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        boolean returnValue = rooms[room].rollACanvas();
        return new Pair(returnClk, returnValue);
    }

    /**
     * This method returns the distance of a room to the outside.
     *
     * @param id of the room
     * @return
     */
    @Override
    public Pair<VectorClock, Integer> getRoomDistance(int id, VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        int returnValue = rooms[id].getDistance();
        return new Pair(returnClk, returnValue);
    }

    @Override
    public void signalShutdown() throws RemoteException {
        
        /* Just for test - Put in the file for example */
        String rmiServerHostname = "localhost";
        int rmiServerPort = 22110;
        String nameEntryObject = "Museum";
        
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);

        try {
            // Unregister ourself
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("Museum registration exception: " + e.getMessage());
            Logger.getLogger(Museum.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            System.out.println("Museum not bound exception: " + e.getMessage());
            Logger.getLogger(Museum.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            // Unexport; this will also remove us from the RMI runtime
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            Logger.getLogger(Museum.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Museum closed.");
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
}
