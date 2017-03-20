package monitors;

import main.Constants;
import java.util.Random;

/**
 *
 * @author Ricardo Filipe
 */
public class Museum{
    private Room rooms[] =  new Room[Constants.N_ROOMS];
    private int totalPaitings = 0;
    
    /**
     *  Constructor to create a new Museum
     */
    public Museum(){
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            rooms[i] = new Room();
            totalPaitings += rooms[i].paintings;
        }
        System.out.printf("The museum has %d paitings", totalPaitings);
    }
    
    private class Room{
        private int distance; 
        private int paintings;
        
        public Room() {
            this.distance = new Random().nextInt(Constants.MAX_ROOM_DISTANCE-Constants.MIN_ROOM_DISTANCE) + Constants.MIN_ROOM_DISTANCE;
            this.paintings = new Random().nextInt(Constants.MAX_PAITING_PER_ROOM-Constants.MIN_PAITING_PER_ROOM) + Constants.MIN_PAITING_PER_ROOM;
        }
             
        private synchronized boolean rollACanvas(){
           boolean res = paintings > 0;
           paintings--;
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
    public boolean rollACanvas(int room){
        return rooms[room].rollACanvas();
    }
    
    /**
     * This method returns the distance of a room to the outside.
     * @param id of the room
     * @return
     */
    public int getRoomDistance(int id){
        return rooms[id].getDistance();
    }
}
