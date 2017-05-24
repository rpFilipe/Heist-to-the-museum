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
import structures.*;

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
    private VectorClock vc;

    /**
     * Create a new Assault Party
     * @param tid Id of the Assault Party.
     * @param genRepo General Repository instance.
     */
    public AssaultParty(int tid, ImonitorsGeneralRepository genRepo) {
        crawlingQueue = new LinkedList<>();
        ASSAULT_PARTY_SIZE = Constants.ASSAULT_PARTY_SIZE;
        positions = new int[ASSAULT_PARTY_SIZE];
        testPositions = new int[ASSAULT_PARTY_SIZE];
        teamId = tid;
        nThievesReadyToReturn = 0;
        this.vc = new VectorClock(7, 0);
        this.genRepo = genRepo;
    }

    /**
     * Method to signal the first Ordinary Thief that joined the party party to start crawling inwards.
     */
    @Override
    public synchronized VectorClock sendAssaultParty(VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        thiefCrawlongIdx = crawlingQueue.peek().id;
        notifyAll();
        
        return returnClk;
    }

    /**
     * Method to Thieves crawl from the Concentration Site to the Museum interior.
     * @param id of the Ordinary Thief that invoked the method.
     */
    @Override
    public synchronized VectorClock crawlIn(int id, VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        
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
                return returnClk;
            }
            
            currentThiefInfo = crawlingQueue.poll();
            positions[currentThiefInfo.positionInArray]+=currentThiefInfo.speed;
            if(positions[currentThiefInfo.positionInArray] > roomDistance)
                positions[currentThiefInfo.positionInArray] = roomDistance;
            //positions[currentThiefInfo.positionInArray]++;
            genRepo.updateThiefPosition(thiefCrawlongIdx, positions[currentThiefInfo.positionInArray],vc);
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
        return returnClk;
    }

    /**
     * Method to Thieves crawl from the Museum to the Concentration Site.
     * @param id of the Ordinary Thief that invoked the method.
     */
    @Override
    public synchronized VectorClock crawlOut(int id, VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        
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
                return returnClk;
            }

            currentThiefInfo = crawlingQueue.poll();
            positions[currentThiefInfo.positionInArray]-=currentThiefInfo.speed;
            if(positions[currentThiefInfo.positionInArray] <0)
                positions[currentThiefInfo.positionInArray] = 0;
            //positions[currentThiefInfo.positionInArray]--;
            genRepo.updateThiefPosition(thiefCrawlongIdx, positions[currentThiefInfo.positionInArray],vc);
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
        return returnClk;
    }

    /**
     * Method to add a Ordinary Thief joins this Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     * @param speed maximum crawling distance per step.
     */
    @Override
    public synchronized VectorClock joinParty(int id, int speed, VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        
        ThiefInfo ti = new ThiefInfo(id, speed, positionInArray);
        crawlingQueue.add(ti);
        positionInArray++;
        genRepo.updateThiefSituation(id, 'P',vc);
        
        return returnClk;
    }

    /**
     *  Method to set the target room parameters to this Assault Party.
     * @param id id of the Room.
     * @param distance from the Concentration Site to the Room.
     */
    @Override
    public VectorClock setRoom(int id, int distance,VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
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
        genRepo.setRoomIdAP(teamId, roomId,vc);
        
        return returnClk;
    }

    /**
     * Get the current target room assigned to this Assault Party.
     * @return Room id
     */
    @Override
    public synchronized Pair< VectorClock, Integer> getTargetRoom(VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        return new Pair(returnClk,this.roomId);
    }

    /**
     * Method to change the direction in crawling.
     */
    @Override
    public synchronized VectorClock reverseDirection(VectorClock vc) {
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        nThievesReadyToReturn++;
        //outsideReached = false;
        if (nThievesReadyToReturn == ASSAULT_PARTY_SIZE) {
            thiefCrawlongIdx = crawlingQueue.peek().id;
            notifyAll();
        }
        return returnClk;
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