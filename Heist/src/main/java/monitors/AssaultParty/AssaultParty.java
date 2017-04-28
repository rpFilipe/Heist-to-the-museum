/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class AssaultParty implements IotAssaultParty, ImtAssaultParty{

    private int teamId;
    private int roomDistance;
    private int roomId;
    private int positionInArray;
    private int[] positions;
    private int[] testPositions;
    private int nThievesReadyToReturn;
    private Queue<ThiefInfo> crawlingQueue;
    private boolean roomReached = false;
    private boolean outsideReached = false;
    private ThiefInfo currentThiefInfo;
    private int thiefCrawlongIdx = -1;
    private ImonitorsGeneralRepository genRepo;
    private int ASSAULT_PARTY_SIZE;

    /**
     * Create a new Assault Party
     * @param tid Party Identifier
     * @param genRepo instance of the General Repository
     */
    public AssaultParty(int tid, ImonitorsGeneralRepository genRepo, int aps) {
        crawlingQueue = new LinkedList<>();
        ASSAULT_PARTY_SIZE = aps;
        positions = new int[ASSAULT_PARTY_SIZE];
        testPositions = new int[ASSAULT_PARTY_SIZE];
        teamId = tid;
        nThievesReadyToReturn = 0;
        this.genRepo = genRepo;
    }

    /**
     * Method to signal the first Ordinary Thief that joined the party party to start crawling inwards.
     */
    @Override
    public synchronized void sendAssaultParty() {
        thiefCrawlongIdx = crawlingQueue.peek().id;
        notifyAll();
    }

    /**
     * Method to Thieves crawl from the Concentration Site to the Museum interior.
     * @param id of the Ordinary Thief that invoked the method.
     */
    @Override
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
            positions[currentThiefInfo.positionInArray]+=currentThiefInfo.speed;
            if(positions[currentThiefInfo.positionInArray] > roomDistance)
                positions[currentThiefInfo.positionInArray] = roomDistance;
            //positions[currentThiefInfo.positionInArray]++;
            genRepo.updateThiefPosition(thiefCrawlongIdx, positions[currentThiefInfo.positionInArray]);
            crawlingQueue.add(currentThiefInfo);
            thiefCrawlongIdx = (crawlingQueue.peek()).id;

            // Check if all thieves arrived to the room
            int sum = IntStream.of(positions).sum();
            if (sum == roomDistance * ASSAULT_PARTY_SIZE) {
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
    @Override
    public synchronized void crawlOut(int id) {
        while (!outsideReached) {
            while (thiefCrawlongIdx != id && !outsideReached) {
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, ex);
                    System.out.println("crawlOut");
                }
            }

            if (outsideReached) {
                return;
            }

            currentThiefInfo = crawlingQueue.poll();
            positions[currentThiefInfo.positionInArray]-=currentThiefInfo.speed;
            if(positions[currentThiefInfo.positionInArray] <0)
                positions[currentThiefInfo.positionInArray] = 0;
            //positions[currentThiefInfo.positionInArray]--;
            genRepo.updateThiefPosition(thiefCrawlongIdx, positions[currentThiefInfo.positionInArray]);
            crawlingQueue.add(currentThiefInfo);
            thiefCrawlongIdx = (crawlingQueue.peek()).id;

            // Check if all thieves arrived to the room
            int sum = IntStream.of(positions).sum();
            if (sum == 0) {
                outsideReached = true;
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
    @Override
    public synchronized void joinParty(int id, int speed) {
        ThiefInfo ti = new ThiefInfo(id, speed, positionInArray);
        crawlingQueue.add(ti);
        positionInArray++;
        genRepo.updateThiefSituation(id, 'P');
    }

    /**
     *  Method to set the target room parameters to this Assault Party.
     * @param id id of the Room.
     * @param distance from the Concentration Site to the Room.
     */
    @Override
    public void setRoom(int id, int distance) {
        this.roomDistance = distance;
        this.roomReached = false;
        this.outsideReached = false;
        this.roomId = id;
        this.nThievesReadyToReturn = 0;
        this.crawlingQueue.clear();
        for (int i = 0; i < positions.length; i++) {
            positions[i] = 0;
        }
        positionInArray = 0;
        genRepo.setRoomIdAP(teamId, roomId);
    }

    /**
     * Get the current target room assigned to this Assault Party.
     * @return Room id
     */
    @Override
    public int getTargetRoom() {
        return this.roomId;
    }

    /**
     * Method to change the direction in crawling.
     */
    @Override
    public synchronized void reverseDirection() {
        nThievesReadyToReturn++;
        //outsideReached = false;
        if (nThievesReadyToReturn == ASSAULT_PARTY_SIZE) {
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