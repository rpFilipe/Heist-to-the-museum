/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ConcentrationSite.IotConcentrationSite;

/**
 *
 * @author Ricardo Filipe
 */
class ConcentrationSiteProxy implements ImtConcentrationSite, IotConcentrationSite{

    public ConcentrationSiteProxy(String configServerAddr, int configServerPort) {
    }

    
    @Override
    public void startOperations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareAssaultParty(int partyId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sumUpResults() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean amINeeded(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getPartyId(int thiefId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void prepareExcursion(int id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
