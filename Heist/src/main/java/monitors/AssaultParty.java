/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors;

import java.util.LinkedList;
import main.Constants;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 *
 * @author Ricardo Filipe
 */
public class AssaultParty {

    private int teamId;
    private int roomDistance;
    private int roomId;
    private int positionInArray;
    private int[] positions;
    private int[] testPositions;
    private int nThievesReadyToReturn;
    private Queue<ThiefInfo> crawlingQueue;
    private boolean roomReached = false;
    private ThiefInfo currentThiefInfo;
    private int thiefCrawlongIdx = -1;

    /**
     * Create a new Assault Party
     * @param tid - Party Identifier
     */
    public AssaultParty(int tid) {
        crawlingQueue = new LinkedList<>();
        positions = new int[Constants.ASSAULT_PARTY_SIZE];
        testPositions = new int[Constants.ASSAULT_PARTY_SIZE];
        teamId = tid;
        nThievesReadyToReturn = 0;
    }

    /**
     * Method to signal the first Ordinary Thief that joined the party party to start crawling inwards.
     */
    public synchronized void sendAssaultParty() {
        // TODO
        thiefCrawlongIdx = crawlingQueue.peek().id;
        notifyAll();
    }

    /**
     * Method to Thieves crawl from the Concentration Site to the Museum interior.
     * @param id of the Ordinary Thief that invoked the method.
     */
    public synchronized void crawlIn(int id) {

        while (!roomReached) {
            while (thiefCrawlongIdx != id && !roomReached) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("crawlIn");
                }
            }

            if (roomReached) {
                return;
            }
            
            currentThiefInfo = crawlingQueue.poll();
            positions[currentThiefInfo.positionInArray]++;
            crawlingQueue.add(currentThiefInfo);
            thiefCrawlongIdx = (crawlingQueue.peek()).id;

            // Check if all thieves arrived to the room
            int sum = IntStream.of(positions).sum();
            if (sum == roomDistance * Constants.ASSAULT_PARTY_SIZE) {
                roomReached = true;
                thiefCrawlongIdx = -1;
            }
            notifyAll();
        }
    }

    /**
     * Method to Thieves crawl from the Museum to the Concentration Site.
     * @param id of the Ordinary Thief that invoked the method.
     */
    public synchronized void crawlOut(int id) {
        while (!roomReached) {
            while (thiefCrawlongIdx != id && !roomReached) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("crawlOut");
                }
            }

            if (roomReached) {
                return;
            }

            positions[currentThiefInfo.positionInArray]--;
            currentThiefInfo = crawlingQueue.poll();
            crawlingQueue.add(currentThiefInfo);
            thiefCrawlongIdx = (crawlingQueue.peek()).id;

            // Check if all thieves arrived to the room
            int sum = IntStream.of(positions).sum();
            if (sum == 0) {
                roomReached = true;
                thiefCrawlongIdx = -1;
            }
            notifyAll();
        }

    }

    /**
     * Method to add a Ordinary Thief joins this Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     * @param speed maximum crawling distance per step.
     */
    public synchronized void joinParty(int id, int speed) {
        ThiefInfo ti = new ThiefInfo(id, speed, positionInArray);
        crawlingQueue.add(ti);
        positionInArray++;
    }

    /**
     *  Method to set the target room parameters to this Assault Party.
     * @param id id of the Room.
     * @param distance from the Concentration Site to the Room.
     */
    public void setRoom(int id, int distance) {
        this.roomDistance = distance;
        this.roomReached = false;
        this.roomId = id;
        this.crawlingQueue.clear();
        for (int i = 0; i < positions.length; i++) {
            positions[i] = 0;
        }
        positionInArray = 0;
    }

    /**
     * Get the current target room assigned to this Assault Party.
     * @return Room id
     */
    public int getTargetRoom() {
        return this.roomId;
    }

    /**
     * Method to change the direction in crawling.
     * @param thiefId
     */
    public synchronized void reverseDirection(int thiefId) {
        nThievesReadyToReturn++;

        if (nThievesReadyToReturn == Constants.ASSAULT_PARTY_SIZE) {
            thiefCrawlongIdx = crawlingQueue.peek().id;
            notifyAll();
        }
    }
    
    private boolean isDisplacementValid(){
        for (int i = 0; i < Constants.ASSAULT_PARTY_SIZE-1; i++) {
            int displacement = Math.abs(testPositions[i+1] - testPositions[i]);
            if(displacement > Constants.MAX_DISPLACEMENT)
                return false;
        }
            return true;
    }

    private class ThiefInfo {

        private int id, speed, distance, positionInArray;

        public ThiefInfo(int id, int speed, int positionInArray) {
            this.id = id;
            this.speed = speed;
            this.distance = 0;
            this.positionInArray = positionInArray;
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
