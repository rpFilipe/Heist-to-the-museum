/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import monitors.GeneralRepository.GeneralRepository;
import monitors.Museum.ImtMuseum;
import monitors.Museum.IotMuseum;
import monitors.Museum.Museum;

/**
 *
 * @author Ricardo Filipe
 */
public class MuseumProxy implements ImtMuseum, IotMuseum{

    public MuseumProxy() {
    }

    @Override
    public int getRoomDistance(int targetRoom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean rollACanvas(int roomId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
