/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.ControlAndCollectionSite.IotControlAndCollectionSite;

/**
 *
 * @author Ricardo Filipe
 */
class ControlAndCollectionSiteProxy implements ImtControlAndCollectionSite, IotControlAndCollectionSite{

    public ControlAndCollectionSiteProxy(String configServerAddr, int configServerPort) {
    }
    
    

    @Override
    public int getTargetRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getPartyToDeploy() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void takeARest() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void collectCanvas() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isHeistCompleted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean waitingNedded() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void handACanvas(int id, boolean canvas, int roomId, int partyId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
