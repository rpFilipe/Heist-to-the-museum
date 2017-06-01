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
 *
 * @author Ricardo Filipe
 */
public interface GeneralRepositoryInterface extends ImtGeneralRepository, IotGeneralRepository, ImonitorsGeneralRepository, Remote
{
    public void terminateServers() throws RemoteException;
}
