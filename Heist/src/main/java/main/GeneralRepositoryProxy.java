/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.GeneralRepository.IotGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepositoryProxy implements ImtGeneralRepository, IotGeneralRepository{

    public GeneralRepositoryProxy(String configServerAddr, int configServerPort) {
    }

    @Override
    public void updateMThiefState(int state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void FinalizeLog() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addThief(int thiefId, int speed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateThiefState(int thiefId, int state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateThiefCylinder(int thiefId, boolean hasCanvas) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateThiefSituation(int thiefId, char situation) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
