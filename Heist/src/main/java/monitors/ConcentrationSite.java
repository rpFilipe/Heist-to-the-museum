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
    
    public ConcentrationSite() {
        thievesWaiting = new LinkedList<>();
        isNeeded = new boolean[Constants.N_ORD_THIEVES];
        resultsReady = false;
        partyReady = false;
        nThievesInParty = 0;
    }
    
    public synchronized boolean amINeeded(int id){
        //System.out.println("monitors.ConcentrationSite.amINeeded()");
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
    
    public synchronized void prepareExcursion(int thiefId){
        nThievesInParty++;
        if(nThievesInParty == Constants.ASSAULT_PARTY_SIZE)
        {
            partyReady = true;
            notifyAll();
            nThievesInParty = 0;
        }
    }
    
    public synchronized void prepareAssaultParty(int partyId){
        
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

    public synchronized void sumUpResults(){
        System.out.println("monitors.ConcentrationSite.sumUpResults()");
        while(thievesWaiting.size() != Constants.N_ORD_THIEVES){
            try {
                wait();
            } catch (InterruptedException ex) {
                Logger.getLogger(ConcentrationSite.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        resultsReady = true;
        notifyAll();
    }
    
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
    
    public synchronized int getNumberThivesWaiting(){
        return thievesWaiting.size();
    }
    
    public synchronized int getPartyId(int thiefId){
        return thiefAssaultParty[thiefId];
    }
}
