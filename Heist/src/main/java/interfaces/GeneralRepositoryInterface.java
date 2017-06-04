/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.GeneralRepository.IotGeneralRepository;

/**
 * @author Ricardo Filipe 72727
 * @author Tiago Henriques 73046
 * @author Miguel Oliveira 72638
 */
public interface GeneralRepositoryInterface extends ImtGeneralRepository, IotGeneralRepository, ImonitorsGeneralRepository, Remote
{
    /**
     * This function is used for order the General Repository Monitor to shutdown all other Monitors and himself as well.
     * @throws RemoteException may throw during a execution of a remote method call
     */
    public void terminateServers() throws RemoteException;
}
