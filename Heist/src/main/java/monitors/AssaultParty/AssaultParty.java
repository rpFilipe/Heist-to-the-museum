/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;


import interfaces.AssaultPartyInterface;
import interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import structures.*;
import static structures.Constants.getHost;
import static structures.Constants.getNameEntry;
import static structures.Constants.getPort;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class AssaultParty implements AssaultPartyInterface{

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
    private VectorClock clkToSend;

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
        clkToSend = vc.incrementClock();
        thiefCrawlongIdx = crawlingQueue.peek().id;
        notifyAll();
        
        return clkToSend;
    }

    /**
     * Method to Thieves crawl from the Concentration Site to the Museum interior.
     * @param id of the Ordinary Thief that invoked the method.
     */
    @Override
    public synchronized VectorClock crawlIn(int id, VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        
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
                return clkToSend;
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
        return clkToSend;
    }

    /**
     * Method to Thieves crawl from the Museum to the Concentration Site.
     * @param id of the Ordinary Thief that invoked the method.
     */
    @Override
    public synchronized VectorClock crawlOut(int id, VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        
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
                return clkToSend;
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
        return clkToSend;
    }

    /**
     * Method to add a Ordinary Thief joins this Assault Party.
     * @param id of the Ordinary Thief that invoked the method.
     * @param speed maximum crawling distance per step.
     */
    @Override
    public synchronized VectorClock joinParty(int id, int speed, VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        
        ThiefInfo ti = new ThiefInfo(id, speed, positionInArray);
        crawlingQueue.add(ti);
        positionInArray++;
        genRepo.updateThiefSituation(id, 'P',vc);
        
        return clkToSend;
    }

    /**
     *  Method to set the target room parameters to this Assault Party.
     * @param id id of the Room.
     * @param distance from the Concentration Site to the Room.
     */
    @Override
    public VectorClock setRoom(int id, int distance,VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
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
        
        return clkToSend;
    }

    /**
     * Get the current target room assigned to this Assault Party.
     * @return Room id
     */
    @Override
    public synchronized Pair< VectorClock, Integer> getTargetRoom(VectorClock vc) {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        return new Pair(clkToSend,this.roomId);
    }

    /**
     * Method to change the direction in crawling.
     */
    @Override
    public synchronized VectorClock reverseDirection(VectorClock vc) {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        nThievesReadyToReturn++;
        //outsideReached = false;
        if (nThievesReadyToReturn == ASSAULT_PARTY_SIZE) {
            thiefCrawlongIdx = crawlingQueue.peek().id;
            notifyAll();
        }
        return clkToSend;
    }

    @Override
    public void signalShutdown() throws RemoteException {
        
        String xmlFile = Constants.xmlFile;
        
        String rmiServerHostname = getHost("Rmi", xmlFile);
        int rmiServerPort = getPort("Rmi", xmlFile);
        String nameEntryObject = getNameEntry("AssaultParty"+this.teamId, xmlFile);
        
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);
        
        try {
            // Unregister ourself
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("AssaultParty registration exception: " + e.getMessage());
            Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            System.out.println("AssaultParty not bound exception: " + e.getMessage());
            Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            // Unexport; this will also remove us from the RMI runtime
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            Logger.getLogger(AssaultParty.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.printf("Assault Party %d closed.\n", this.teamId);
    }
    
    private static Registry getRegistry(String rmiServerHostname, int rmiServerPort) {
        Registry registry = null;
        try {
            registry = LocateRegistry.getRegistry(rmiServerHostname, rmiServerPort);
        } catch (RemoteException ex) {
            Logger.getLogger(ControlAndCollectionSiteStart.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        System.out.println("O registo RMI foi criado!");
        return registry;
    }

    private static Register getRegister(Registry registry) {
        Register reg = null;
        String xmlFile = Constants.xmlFile;
        String nameEntryObject = getNameEntry("Rmi", xmlFile);
        try {
            reg = (Register) registry.lookup(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }
        return reg;
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