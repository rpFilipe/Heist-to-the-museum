/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.AssaultParty.IotAssaultParty;

/**
 *
 * @author Ricardo Filipe
 */
class AssaultPartyProxy implements IotAssaultParty, ImtAssaultParty{
    
    String SERVER_ADDR;
    int SERVER_PORT;

    AssaultPartyProxy(int i, String configServerAddr, int configServerPort) {
        SERVER_ADDR = getServerLocation(configServerAddr, configServerPort, "AssaultParty" +i);
        SERVER_PORT = getServerPort(configServerAddr, configServerPort, "AssaultParty" +i);
    }

    @Override
    public void crawlIn(int id) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        int[] args = new int[] {id};
        outMessage = new Message(CI, args);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void crawlOut(int id) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        int[] args = new int[] {id};
        outMessage = new Message(CO, args);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void joinParty(int id, int speed) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(JP, new int[] {id, speed});
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void reverseDirection() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(RD);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public int getTargetRoom() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(GTR);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        return inMessage.getReturnValue();
    }

    @Override
    public void setRoom(int id, int distance) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(SR, new int[] {id, distance});
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void sendAssaultParty() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(SAP);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
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
