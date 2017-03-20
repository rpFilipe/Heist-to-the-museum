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
    
    private int nWaitingThieves;
    private boolean partyReady;
    private Queue<Integer> thievesWaiting;
    private boolean[] isNeeded;
    private boolean resultsReady;
    private boolean masterStartSignal;
    private int nThievesInParty;
    private int[] thiefAssaultParty = new int[Constants.N_ORD_THIEVES];
    
    public ConcentrationSite() {
        nWaitingThieves = 0;
        thievesWaiting = new LinkedList<>();
        isNeeded = new boolean[Constants.N_ORD_THIEVES];
        resultsReady = false;
        partyReady = false;
        nThievesInParty = 0;
        masterStartSignal = false;
    }
    
    /**
     *
     * @param id
     */
    public synchronized boolean amINeeded(int id){
        System.out.println("monitors.ConcentrationSite.amINeeded()");
        thievesWaiting.add(id);
        nWaitingThieves++;
        notifyAll();
        
        while(!isNeeded[id] && !resultsReady){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error amdINedded");
            }
        } isNeeded[id] = false;
        
        if (resultsReady)
            return false;
        return true;
    }
    
    /**
     *
     * @param roomId
     */
    public synchronized void prepareExcursion(int thiefId){
        System.out.println("monitors.ConcentrationSite.prepareExcursion()");
        // TODO
        // Bloqueia e o ultimo antes de bloquear acorda o MASTER
        
        nThievesInParty++;
        if(nThievesInParty == Constants.ASSAULT_PARTY_SIZE)
        {
            partyReady = true;
            notifyAll();
            nThievesInParty = 0;
        }
    }
    
    /**
     *
     */
    public synchronized void prepareAssaultParty(int partyId){
        System.out.println("monitors.ConcentrationSite.prepareAssaultParty()");
        
        for (int i = 0; i < Constants.ASSAULT_PARTY_SIZE; i++) {
            int thiefToWake = thievesWaiting.poll();
            thiefAssaultParty[thiefToWake] = partyId;
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
     *
     */
    public synchronized void sumUpResults(){
        System.out.println("monitors.ConcentrationSite.sumUpResults()");
        resultsReady = true;
        notifyAll();
    }
    
    /**
     *
     */
    public synchronized void startOperations(){
        System.out.println("monitors.ConcentrationSite.startOperations()");
        while(thievesWaiting.size() < Constants.N_ORD_THIEVES){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
                System.out.println("Error startOperations");
            }
        }
    }
    
    public synchronized int getNumberThivesWaiting(){
        System.out.println("monitors.ConcentrationSite.getNumberThivesWaiting()");
        return thievesWaiting.size();
    }
    
    public int getPartyId(int thiefId){
        return thiefAssaultParty[thiefId];
    }
}
