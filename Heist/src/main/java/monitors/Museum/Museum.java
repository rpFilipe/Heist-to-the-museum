package monitors.Museum;

import interfaces.MuseumInterface;
import java.rmi.RemoteException;
import java.util.Random;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
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
    public Museum(ImonitorsGeneralRepository genRepo,
            int N_ROOMS,
            int MAX_ROOM_DISTANCE,
            int MIN_ROOM_DISTANCE,
            int MAX_PAITING_PER_ROOM,
            int MIN_PAITING_PER_ROOM) {
        this.N_ROOMS = N_ROOMS;
        this.MAX_ROOM_DISTANCE = MAX_ROOM_DISTANCE;
        this.MIN_ROOM_DISTANCE = MIN_ROOM_DISTANCE;
        this.MAX_PAITING_PER_ROOM = MAX_PAITING_PER_ROOM;
        this.MIN_PAITING_PER_ROOM = MIN_PAITING_PER_ROOM;
        rooms = new Room[N_ROOMS];
        totalPaitings = 0;
        roomsCreated = 0;
        this.vc = new VectorClock(7, 0);
        this.genRepo = genRepo;
        for (int i = 0; i < N_ROOMS; i++) {
            rooms[i] = new Room();
            totalPaitings += rooms[i].paintings;
        }
        System.out.printf("The museum has %d paitings", totalPaitings);
    }

    private class Room {

        private int distance;
        private int paintings;
        private int id;

        public Room() {
            this.distance = new Random().nextInt(MAX_ROOM_DISTANCE - MIN_ROOM_DISTANCE) + MIN_ROOM_DISTANCE;
            this.paintings = new Random().nextInt(MAX_PAITING_PER_ROOM - MIN_PAITING_PER_ROOM) + MIN_PAITING_PER_ROOM;
            this.id = roomsCreated;
            genRepo.setRoomAtributes(id, distance, paintings, vc);
            roomsCreated++;
        }

        private synchronized boolean rollACanvas() {
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
    public Pair<VectorClock, Boolean> rollACanvas(int room, VectorClock vc) {
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
