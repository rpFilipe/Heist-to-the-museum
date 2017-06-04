package monitors.ConcentrationSite;

import States.MasterThiefStates;
import interfaces.ConcentrationSiteInterface;
import interfaces.Register;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Queue;
import monitors.ControlAndCollectionSite.ControlAndCollectionSiteStart;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import structures.Constants;
import static structures.Constants.getHost;
import static structures.Constants.getNameEntry;
import static structures.Constants.getPort;
import structures.Pair;
import structures.VectorClock;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public class ConcentrationSite implements ConcentrationSiteInterface{
    private int N_ORD_THIEVES;
    private int ASSAULT_PARTY_SIZE;
    private boolean partyReady;
    private  Queue<Integer> thievesWaiting;
    private  boolean[] isNeeded;
    private boolean resultsReady;
    private int nThievesInParty;
    private int[] thiefAssaultParty;
    private ImonitorsGeneralRepository genRepo;
    private VectorClock vc;
    private VectorClock clkToSend;
    
    /**
     *  Create a new Concentration Site.
     * @param genRepo general repository
     * @param n_ord_thieves number of ordinary thieves
     * @param assault_party_size assault party size
     */
    public ConcentrationSite(ImonitorsGeneralRepository genRepo, int n_ord_thieves, int assault_party_size) {
        N_ORD_THIEVES = n_ord_thieves;
        ASSAULT_PARTY_SIZE = assault_party_size;
        thievesWaiting = new LinkedList<>();
        isNeeded = new boolean[N_ORD_THIEVES];
        resultsReady = false;
        partyReady = false;
        nThievesInParty = 0;
        thiefAssaultParty = new int[N_ORD_THIEVES];
        this.genRepo = genRepo;
        this.vc = new VectorClock(7, 0); // 1 master + 6 ordinary
    }
    
    /**
     * Method to check if the Ordinary Thief id needed.
     * @param id of the Ordinary Thief.
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    @Override
    public synchronized Pair<VectorClock, Boolean> amINeeded(int id, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        thievesWaiting.add(id);
        clkToSend = vc.incrementClock();
        notifyAll();
        
        while(!isNeeded[id] && !resultsReady){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error amdINedded");
            }
        } isNeeded[id] = false;
        
        return new Pair(clkToSend, !resultsReady);
    }
    
    /**
     * Method to signal thar the Ordinary Thief has joined the Assault Party.
     * @param thiefId Id of the Ordinary Thief.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    @Override
    public synchronized VectorClock prepareExcursion(int thiefId, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        nThievesInParty++;
        if(nThievesInParty == ASSAULT_PARTY_SIZE)
        {
            partyReady = true;
            nThievesInParty = 0;
            notifyAll();
        }
        
        return clkToSend;
    }
    
    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    @Override
    public synchronized VectorClock prepareAssaultParty(int partyId, VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        for (int i = 0; i < ASSAULT_PARTY_SIZE; i++) {
            int thiefToWake = thievesWaiting.poll();
            thiefAssaultParty[thiefToWake] = partyId;
            genRepo.setPartyElement(partyId, thiefToWake, i, vc);
            isNeeded[thiefToWake] = true;
        }
        notifyAll();
        
        // espera que tenha assaltantes suficientes para começar a nova party e acordaos quando estiverem prontos
        while(!partyReady){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error prepareAssaultParty");
            }
        } partyReady = false;
        
        return clkToSend;
    }

    /**
     * Notify that the assault has ended.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception 
     */
    @Override
    public synchronized VectorClock sumUpResults(VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        resultsReady = true;
        notifyAll();
        
        return clkToSend;
    }
    
    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     * @param vc VectorClock
     * @return VectorClock
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    @Override
    public synchronized VectorClock startOperations(VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        while(thievesWaiting.size() < N_ORD_THIEVES){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error startOperations");
            }
        }
        
        return clkToSend;
    }

    /**
     * Get the id of the Assault Party associated with a Ordinary Thief.
     * @param thiefId Id of the Thief.
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    @Override
    public synchronized Pair<VectorClock, Integer> getPartyId(int thiefId, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        return new Pair(clkToSend, thiefAssaultParty[thiefId]);
    }

    /**
     * Appraise sit.
     * @param isHeistCompleted is Heist Completed ?
     * @param isWaitingNedded is the thief waiting needed ?
     * @param vc VectorClock
     * @return Pair
     * @throws java.rmi.RemoteException exception
     * @throws java.lang.InterruptedException exception
     */
    @Override
    public synchronized Pair<VectorClock, Integer> appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        clkToSend = vc.incrementClock();
        
        if(isHeistCompleted){
            while(thievesWaiting.size() < N_ORD_THIEVES){
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return new Pair(clkToSend, MasterThiefStates.PRESENTING_THE_REPORT);
        }
        
        if(isWaitingNedded){            
            return new Pair(clkToSend, MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL);
        }
        
        while(thievesWaiting.size() < ASSAULT_PARTY_SIZE){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new Pair(clkToSend, MasterThiefStates.ASSEMBLING_A_GROUP);
    }
    
    /**
     * This function is used for the log to signal the ConcentrationSite to shutdown.
     * @throws RemoteException may throw during a execution of a remote method call
     */
    @Override
    public void signalShutdown() throws RemoteException {
        
        String xmlFile = Constants.xmlFile;
        
        String rmiServerHostname = getHost("Rmi", xmlFile);
        int rmiServerPort = getPort("Rmi", xmlFile);
        String nameEntryObject = getNameEntry("ConcentrationSite", xmlFile);
        
        Registry registry = getRegistry(rmiServerHostname, rmiServerPort);
        Register reg = getRegister(registry);

        try {
            // Unregister ourself
            reg.unbind(nameEntryObject);
        } catch (RemoteException e) {
            System.out.println("Concentration Site registration exception: " + e.getMessage());
            Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, e);
        } catch (NotBoundException e) {
            System.out.println("Concentration Site not bound exception: " + e.getMessage());
            Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, e);
        }

        try {
            // Unexport; this will also remove us from the RMI runtime
            UnicastRemoteObject.unexportObject(this, true);
        } catch (NoSuchObjectException ex) {
            Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Concentration Site closed.");
    }
    
    /**
     * This function is used to register it with the local registry service.
     * @param rmiServerHostname Rmi Server Host Name.
     * @param rmiServerPort Rmi Server port.
     * @return registry.
     */
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

    /**
    This function us used to return a reference, a stub, for the remote object associated with the specified name.
    * @param registry registry.
    * @return the register reg.
    */
    private static Register getRegister(Registry registry) {
        Register reg = null;
        String xmlFile = Constants.xmlFile;
        try {
            reg = (Register) registry.lookup(getNameEntry("Rmi", xmlFile));
        } catch (RemoteException e) {
            System.out.println("RegisterRemoteObject lookup exception: " + e.getMessage());
            System.exit(1);
        } catch (NotBoundException e) {
            System.out.println("RegisterRemoteObject not bound exception: " + e.getMessage());
            System.exit(1);
        }
        return reg;
    }
}
