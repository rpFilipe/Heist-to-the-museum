package monitors.Museum;

import java.util.Random;
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class Museum implements IotMuseum, ImtMuseum{
    private int N_ROOMS;
    private int MAX_ROOM_DISTANCE;
    private int MIN_ROOM_DISTANCE;
    private int MAX_PAITING_PER_ROOM;
    private int MIN_PAITING_PER_ROOM;
    private static int roomsCreated = 0;
    private Room[] rooms;
    private int totalPaitings;
    private ImonitorsGeneralRepository genRepo;
    
    /**
     *  Constructor to create a new Museum
     * @param genRepo General Repository Instance
     * @param N_ROOMS Number of Rooms in the simulation.
     * @param MAX_ROOM_DISTANCE Maximum distance a Room can be in the simulation.
     * @param MIN_ROOM_DISTANCE Minimum distance a Room can be in the simulation.
     * @param MAX_PAITING_PER_ROOM Maximum number of paintings in a given Room.
     * @param MIN_PAITING_PER_ROOM Minimum number of paintings in a given Room.
     */
    public Museum(ImonitorsGeneralRepository genRepo,
            int N_ROOMS,
            int MAX_ROOM_DISTANCE,
            int MIN_ROOM_DISTANCE, 
            int MAX_PAITING_PER_ROOM, 
            int MIN_PAITING_PER_ROOM){
        this.N_ROOMS = N_ROOMS;
        this.MAX_ROOM_DISTANCE = MAX_ROOM_DISTANCE;
        this.MIN_ROOM_DISTANCE = MIN_ROOM_DISTANCE;
        this.MAX_PAITING_PER_ROOM = MAX_PAITING_PER_ROOM;
        this.MIN_PAITING_PER_ROOM = MIN_PAITING_PER_ROOM;
        rooms =  new Room[N_ROOMS];
        totalPaitings = 0;
        roomsCreated = 0;
        this.genRepo = genRepo;
        for (int i = 0; i < N_ROOMS; i++) {
            rooms[i] = new Room();
            totalPaitings += rooms[i].paintings;
        }
        System.out.printf("The museum has %d paitings", totalPaitings);
    }
    
    private class Room{
        private int distance; 
        private int paintings;
        private int id;
        
        public Room() {
            this.distance = new Random().nextInt(MAX_ROOM_DISTANCE-MIN_ROOM_DISTANCE) + MIN_ROOM_DISTANCE;
            this.paintings = new Random().nextInt(MAX_PAITING_PER_ROOM-MIN_PAITING_PER_ROOM) + MIN_PAITING_PER_ROOM;
            this.id = roomsCreated;
            genRepo.setRoomAtributes(id, distance, paintings);
            roomsCreated++;
        }
             
        private synchronized boolean rollACanvas(){
           boolean res = paintings > 0;
           paintings--;
           if(paintings < 0)
               paintings = 0;
           genRepo.setRoomCanvas(id, paintings);
           return res;
        }
        
        public int getDistance() {
            return distance;
        }
    }
    
    /**
     * This method is used to get a canvas from a room.
     * @param room
     * @return true if the room still has canvas to be stolen.
     */
    @Override
    public boolean rollACanvas(int room){
        return rooms[room].rollACanvas();
    }
    
    /**
     * This method returns the distance of a room to the outside.
     * @param id of the room
     * @return
     */
    @Override
    public int getRoomDistance(int id){
        return rooms[id].getDistance();
    }
}
