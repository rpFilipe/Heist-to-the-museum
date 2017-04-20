/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.GeneralRepository.IotGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepositoryProxy implements ImtGeneralRepository, IotGeneralRepository {

    String SERVER_ADDR;
    int SERVER_PORT;

    public GeneralRepositoryProxy(String configServerAddr, int configServerPort) {
        SERVER_ADDR = getServerLocation(configServerAddr, configServerPort, "GeneralRepository");
        SERVER_PORT = getServerPort(configServerAddr, configServerPort, "GeneralRepository");
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

    private String getServerLocation(String configServerAddr, int configServerPort, String svname) {
        ClientCom con = new ClientCom(configServerAddr, configServerPort);
        Message inMessage, outMessage;

        if (!con.open()) {
            return "";
        }

        outMessage = new Message(CONFIGURATION_REQUEST_LOCATION, svname);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnStr();
    }

    private int getServerPort(String configServerAddr, int configServerPort, String svname) {
        ClientCom con = new ClientCom(configServerAddr, configServerPort);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }

        outMessage = new Message(CONFIGURATION_REQUEST_PORT, svname);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }
}
