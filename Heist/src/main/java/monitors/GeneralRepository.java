/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import main.Constants;
import java.util.Random;

/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepository {
    
    private int stolenPaitings = 0;
    private int targetRoom = 0;
    private boolean[] roomsChecked = new boolean[Constants.N_ROOMS];
    private int [] roomsDistance = new int[Constants.N_ROOMS];
    
    public GeneralRepository(){
        // Generate rooms distances
        for (int i = 0; i < Constants.N_ROOMS; i++) {
            roomsChecked[i] = false;
            roomsDistance[i] = generateRoomDistance(Constants.MIN_ROOM_DISTANCE, Constants.MAX_ROOM_DISTANCE);
        }
    }

    public int getStolenPaitings() {return stolenPaitings;}
    public int getTargetRoom() {return targetRoom;}
    public int getRoomDistance(int id) {return roomsDistance[id];}
    
    public synchronized int setNewTargetRoom() {return ++targetRoom;}
    public synchronized void incStolenPaitings() {stolenPaitings++;}
    
    private int generateRoomDistance(int lowInterval, int highInterval){
        return new Random().nextInt(highInterval-lowInterval) + lowInterval;
    }
}
