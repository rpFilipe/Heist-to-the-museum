package monitors;

import java.util.logging.Level;
import java.util.logging.Logger;
import main.Constants;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Ricardo Filipe
 */
public class ConcentrationSite {
    
    private boolean partyReady;
    private  Queue<Integer> thievesWaiting;
    private  boolean[] isNeeded;
    private boolean resultsReady;
    private int nThievesInParty;
    private int[] thiefAssaultParty = new int[Constants.N_ORD_THIEVES];
    private GeneralRepository genRepo;
    
    /**
     *  Create a new Concentration Site.
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
    public synchronized void prepareExcursion(int thiefId){
        nThievesInParty++;
        if(nThievesInParty == Constants.ASSAULT_PARTY_SIZE)
        {
            partyReady = true;
            notifyAll();
            nThievesInParty = 0;
        }
    }
    
    /**
     * Wake up the Ordinary Thieves necessary to join the Assault Party.
     * @param partyId Id of the target Assault Party.
     */
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
    public synchronized void sumUpResults(){
        resultsReady = true;
        notifyAll();
    }
    
    /**
     * Wait for all the Ordinary Thieves to be Outside and start the assault.
     */
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
     * Get the number of Ordinary Thieves that are idling.
     * @return number of Ordinary Thieves waiting.
     */
    public synchronized int getNumberThivesWaiting(){
        return thievesWaiting.size();
    }
    
    /**
     * Get the id of the Assault Party associated with a Ordinary Thief.
     * @param thiefId Id of the Thief.
     * @return the Assault Party id.
     */
    public synchronized int getPartyId(int thiefId){
        return thiefAssaultParty[thiefId];
    }
}
