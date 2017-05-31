package monitors.ConcentrationSite;

import States.MasterThiefStates;
import interfaces.ConcentrationSiteInterface;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.LinkedList;
import java.util.Queue;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import structures.Pair;
import structures.VectorClock;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
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
    
    /**
     *  Create a new Concentration Site.
     * @param genRepo
     * @param n_ord_thieves
     * @param assault_party_size
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
     * @return is the Ordinary Thief needed.
     */
    @Override
    public synchronized Pair<VectorClock, Boolean> amINeeded(int id, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        thievesWaiting.add(id);
        notifyAll();
        
        while(!isNeeded[id] && !resultsReady){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error amdINedded");
            }
        } isNeeded[id] = false;
        
        VectorClock returnClk = this.vc.clone();
        return new Pair(returnClk, !resultsReady);
    }
    
    /**
     * Method to signal thar the Ordinary Thief has joined the Assault Party.
     * @param thiefId Id of the Ordinary Thief.
     */
    @Override
    public synchronized VectorClock prepareExcursion(int thiefId, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        nThievesInParty++;
        if(nThievesInParty == ASSAULT_PARTY_SIZE)
        {
            partyReady = true;
            nThievesInParty = 0;
            notifyAll();
        }
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }
    
    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     */
    @Override
    public synchronized VectorClock prepareAssaultParty(int partyId, VectorClock vc) throws RemoteException, InterruptedException {
        this.vc.update(vc);
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
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Notify that the assault has ended.
     */
    @Override
    public synchronized VectorClock sumUpResults(VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        resultsReady = true;
        notifyAll();
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }
    
    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     */
    @Override
    public synchronized VectorClock startOperations(VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        while(thievesWaiting.size() < N_ORD_THIEVES){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error startOperations");
            }
        }
        
        VectorClock returnClk = this.vc.clone();
        return returnClk;
    }

    /**
     * Get the id of the Assault Party associated with a Ordinary Thief.
     * @param thiefId Id of the Thief.
     * @return the Assault Party id.
     */
    @Override
    public synchronized Pair<VectorClock, Integer> getPartyId(int thiefId, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        return new Pair(returnClk, thiefAssaultParty[thiefId]);
    }

    /**
     *
     * @param isHeistCompleted
     * @param isWaitingNedded
     * @return
     */
    @Override
    public synchronized Pair<VectorClock, Integer> appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded, VectorClock vc) throws RemoteException,InterruptedException{
        this.vc.update(vc);
        VectorClock returnClk = this.vc.clone();
        
        if(isHeistCompleted){
            while(thievesWaiting.size() < N_ORD_THIEVES){
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return new Pair(returnClk, MasterThiefStates.PRESENTING_THE_REPORT);
        }
        
        if(isWaitingNedded){            
            return new Pair(returnClk, MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL);
        }
        
        while(thievesWaiting.size() < ASSAULT_PARTY_SIZE){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return new Pair(returnClk, MasterThiefStates.ASSEMBLING_A_GROUP);
    }

    @Override
    public void signalShutdown() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
