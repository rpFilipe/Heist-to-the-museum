/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import com.mycompany.heist.Constants;
import java.util.Random;

/**
 *
 * @author Ricardo Filipe
 */
public class MonMuseum{
    private Room rooms[] =  new Room[Constants.N_ROOMS];
    
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
    }
    
    public boolean rollACanvas(int room){
        return rooms[room].rollACanvas();
    }
}
