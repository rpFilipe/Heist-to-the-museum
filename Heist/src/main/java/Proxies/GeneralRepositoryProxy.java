/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proxies;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import monitors.GeneralRepository.ImonitorsGeneralRepository;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.GeneralRepository.IotGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepositoryProxy implements ImtGeneralRepository, IotGeneralRepository, ImonitorsGeneralRepository {

    String SERVER_ADDR;
    int SERVER_PORT;

    /**
     * Create a Proxy to Communicate with the General Repository Server
     * @param configServerAddr Configuration Server address.
     * @param configServerPort Configuration Server port.
     */
    public GeneralRepositoryProxy(String configServerAddr, int configServerPort) {
        SERVER_ADDR = getServerLocation(configServerAddr, configServerPort, "GeneralRepository");
        SERVER_PORT = getServerPort(configServerAddr, configServerPort, "GeneralRepository");
    }

    @Override
    public void updateMThiefState(int state) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
        int[] args = new int[] {state};
        outMessage = new Message(UMTS, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void FinalizeLog() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }

        outMessage = new Message(FN);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();
        
        terminateServers(SERVER_ADDR, SERVER_PORT);
        
        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void addThief(int thiefId, int speed) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {thiefId, speed};
        outMessage = new Message(AT, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void updateThiefState(int thiefId, int state) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {thiefId, state};
        outMessage = new Message(UTS, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void updateThiefCylinder(int thiefId, boolean hasCanvas) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {thiefId, (hasCanvas) ? 1:0};
        outMessage = new Message(UTC, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void updateThiefSituation(int thiefId, char situation) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {thiefId, situation};
        outMessage = new Message(UTST, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
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

    @Override
    public void clearParty(int partyId) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {partyId};
        outMessage = new Message(CP, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void setCollectedCanvas(int toalCanvas) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {toalCanvas};
        outMessage = new Message(SCC, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void setPartyElement(int partyId, int thiefId, int elemId) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {partyId, thiefId, elemId};
        outMessage = new Message(SPE, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void setRoomAtributes(int roomId, int distance, int paitings) {
                ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {roomId, distance, paitings};
        outMessage = new Message(SRA, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void updateThiefPosition(int thiefId, int position) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {thiefId, position};
        outMessage = new Message(UTP, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void setRoomIdAP(int partyId, int room) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {partyId, room};
        outMessage = new Message(SRIAP, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }

    @Override
    public void setRoomCanvas(int id, int paitings) {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
         int[] args = new int[] {id, paitings};
        outMessage = new Message(SRC, args);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != ACK) {
            System.exit(1);
        }
    }
    
    private void terminateServers(String configServerAddr, int configServerPort){
    }
}
