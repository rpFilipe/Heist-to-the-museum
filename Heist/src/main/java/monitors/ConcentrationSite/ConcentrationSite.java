package monitors.ConcentrationSite;

import States.MasterThiefStates;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.Constants;
import java.util.LinkedList;
import java.util.Queue;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 * @author Marc Wagner
 */
public class ConcentrationSite implements IotConcentrationSite, ImtConcentrationSite{
    
    private boolean partyReady;
    private  Queue<Integer> thievesWaiting;
    private  boolean[] isNeeded;
    private boolean resultsReady;
    private int nThievesInParty;
    private int[] thiefAssaultParty = new int[Constants.N_ORD_THIEVES];
    private GeneralRepository genRepo;
    
    /**
     *  Create a new Concentration Site.
     * @param genRepo
     */
    public ConcentrationSite(GeneralRepository genRepo) {
        thievesWaiting = new LinkedList<>();
        isNeeded = new boolean[Constants.N_ORD_THIEVES];
        resultsReady = false;
        partyReady = false;
        nThievesInParty = 0;
        this.genRepo = genRepo;
    }
    
    /**
     * Method to check if the Ordinary Thief id needed.
     * @param id of the Ordinary Thief.
     * @return is the Ordinary Thief needed.
     */
    @Override
    public synchronized boolean amINeeded(int id){
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
        
        return !resultsReady;
    }
    
    /**
     * Method to signal thar the Ordinary Thief has joined the Assault Party.
     * @param thiefId Id of the Ordinary Thief.
     */
    @Override
    public synchronized void prepareExcursion(int thiefId){
        nThievesInParty++;
        if(nThievesInParty == Constants.ASSAULT_PARTY_SIZE)
        {
            partyReady = true;
            nThievesInParty = 0;
            notifyAll();
        }
    }
    
    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     */
    @Override
    public synchronized void prepareAssaultParty(int partyId){
        for (int i = 0; i < Constants.ASSAULT_PARTY_SIZE; i++) {
            int thiefToWake = thievesWaiting.poll();
            thiefAssaultParty[thiefToWake] = partyId;
            genRepo.setPartyElement(partyId, thiefToWake, i);
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
    }

    /**
     * Notify that the assault has ended.
     */
    @Override
    public synchronized void sumUpResults(){
        resultsReady = true;
        notifyAll();
    }
    
    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     */
    @Override
    public synchronized void startOperations(){
        while(thievesWaiting.size() < Constants.N_ORD_THIEVES){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error startOperations");
            }
        }
    }

    /**
     * Get the id of the Assault Party associated with a Ordinary Thief.
     * @param thiefId Id of the Thief.
     * @return the Assault Party id.
     */
    @Override
    public synchronized int getPartyId(int thiefId){
        return thiefAssaultParty[thiefId];
    }

    /**
     *
     * @param isHeistCompleted
     * @param isWaitingNedded
     * @return
     */
    @Override
    public synchronized int appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded) {
        if(isHeistCompleted){
            while(thievesWaiting.size() < Constants.N_ORD_THIEVES){
                try {
                    wait();
                } catch (InterruptedException ex) {
                    Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return MasterThiefStates.PRESENTING_THE_REPORT;
        }
        
        if(isWaitingNedded){
            return MasterThiefStates.WAITING_FOR_GROUP_ARRIVAL;
        }
        
        while(thievesWaiting.size() < Constants.ASSAULT_PARTY_SIZE){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return MasterThiefStates.ASSEMBLING_A_GROUP;
    }
}
