/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author Ricardo Filipe
 */
public class MonAssaultParty {
    
    private char teamId;
    private int roomDistance;
    private int roomId;
    private HashMap<Integer, ThiefInfo> thiefList =  new HashMap<Integer, ThiefInfo>();
    
    public MonAssaultParty(char tid){
        teamId = tid;
    }
    
    
    public synchronized void crawlIn(int id){
        ThiefInfo t = thiefList.get(id);
        HashMap<Integer, ThiefInfo> tmp = new HashMap<>(thiefList);
        tmp.remove(id);
        
        Iterator it =  tmp.entrySet().iterator();
        
        while (it.hasNext()) {
            //TODO ver se é possivel avançar
        }
    }
    
    public synchronized void crawlOut(int id){
        
    }
    
    public void addThief(int id, int speed){
        ThiefInfo ti = new ThiefInfo(id, speed);
        thiefList.put(id, ti);
    }
    
    public void setRoom(int id, int distance){
        this.roomDistance = distance;
        this.roomId = id;
    }
    
    private class ThiefInfo{
        
        private int id, speed, distance;

        public ThiefInfo(int id, int speed) {
            this.id = id;
            this.speed = speed;
            this.distance = 0;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getDistance() {
            return distance;
        }

        public void setDistance(int distance) {
            this.distance = distance;
        }
    }
}
