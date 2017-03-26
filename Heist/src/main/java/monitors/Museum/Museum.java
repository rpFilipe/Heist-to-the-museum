package monitors.Museum;

import main.Constants;
import java.util.Random;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class Museum implements IotMuseum, ImtMuseum{
    private static int roomsCreated = 0;
    private Room rooms[] =  new Room[Constants.N_ROOMS];
    private int totalPaitings;
    private GeneralRepository genRepo;
    
    /**
     *  Constructor to create a new Museum
     * @param genRepo
     */
    public Museum(GeneralRepository genRepo){
        totalPaitings = 0;
        roomsCreated = 0;
        this.genRepo = genRepo;
        for (int i = 0; i < Constants.N_ROOMS; i++) {
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
            this.distance = new Random().nextInt(Constants.MAX_ROOM_DISTANCE-Constants.MIN_ROOM_DISTANCE) + Constants.MIN_ROOM_DISTANCE;
            this.paintings = new Random().nextInt(Constants.MAX_PAITING_PER_ROOM-Constants.MIN_PAITING_PER_ROOM) + Constants.MIN_PAITING_PER_ROOM;
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
