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
    private int nThievesReadyToReturn;
    private Queue<ThiefInfo> crawlingQueue;
    private boolean roomReached = false;
    private ThiefInfo currentThiefInfo;
    private int thiefCrawlongIdx = -1;

    public AssaultParty(int tid) {
        crawlingQueue = new LinkedList<>();
        positions = new int[Constants.ASSAULT_PARTY_SIZE];
        teamId = tid;
        nThievesReadyToReturn = 0;
    }

    public synchronized void sendAssaultParty() {
        // TODO
        thiefCrawlongIdx = crawlingQueue.peek().id;
        notifyAll();
    }

    public synchronized void crawlIn(int id, int speed) {

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

    public synchronized void crawlOut(int id, int speed) {
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

    public synchronized void joinParty(int id, int speed) {
        ThiefInfo ti = new ThiefInfo(id, speed, positionInArray);
        crawlingQueue.add(ti);
        positionInArray++;
    }

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

    public int getTargetRoom() {
        return this.roomId;
    }

    public synchronized void reverseDirection(int thiefId) {
        nThievesReadyToReturn++;

        if (nThievesReadyToReturn == Constants.ASSAULT_PARTY_SIZE) {
            thiefCrawlongIdx = crawlingQueue.peek().id;
            notifyAll();
        }
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
