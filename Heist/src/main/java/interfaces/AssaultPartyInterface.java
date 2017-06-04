/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.AssaultParty.IotAssaultParty;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface AssaultPartyInterface extends ImtAssaultParty, IotAssaultParty, Remote {
    /**
     * This function is used for the log to signal the AssaultParty to shutdown.
     * 
     * @throws RemoteException may throw during a execution of a remote method call
     */
    public void signalShutdown() throws RemoteException;
}
