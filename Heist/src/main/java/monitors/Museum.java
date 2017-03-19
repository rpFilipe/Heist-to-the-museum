/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import main.Constants;
import java.util.Random;
import main.OrdinaryThief;

/**
 *
 * @author Ricardo Filipe
 */
public class Museum{
    private Room rooms[] =  new Room[Constants.N_ROOMS];
    
    public Museum(){
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            rooms[i] = new Room();
        }
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
    
    public boolean rollACanvas(int room){
        System.out.println("monitors.Museum.rollACanvas() - " + room);
       // ((OrdinaryThief) Thread.currentThread()).s
        return rooms[room].rollACanvas();
    }
    
    public int getRoomDistance(int id){
        return rooms[id].getDistance();
    }
}
