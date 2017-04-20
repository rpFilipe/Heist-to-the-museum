/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ConcentrationSite.IotConcentrationSite;

/**
 *
 * @author Ricardo Filipe
 */
class ConcentrationSiteProxy implements ImtConcentrationSite, IotConcentrationSite {

    String SERVER_ADDR;
    int SERVER_PORT;

    public ConcentrationSiteProxy(String configServerAddr, int configServerPort) {
        SERVER_ADDR = getServerLocation(configServerAddr, configServerPort, "ConcentrarionSite");
        SERVER_PORT = getServerPort(configServerAddr, configServerPort, "ConcentrarionSite");
    }

    @Override
    public void startOperations() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(SO);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void prepareAssaultParty(int partyId) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(PAP, partyId);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void sumUpResults() {
                ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(SUR);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public int appraiseSit(boolean isHeistCompleted, boolean isWaitingNedded) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        int args[] = new int[] {(isHeistCompleted) ? 1:0, (isWaitingNedded) ? 1:0};
        outMessage = new Message(AS, args);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        return inMessage.getReturnValue();
    }

    @Override
    public boolean amINeeded(int id) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(AIN, id);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        boolean value = inMessage.getReturnValue() != 0;
        
        return value;
    }

    @Override
    public int getPartyId(int thiefId) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(AS, thiefId);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        return inMessage.getReturnValue();
    }

    @Override
    public void prepareExcursion(int id) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(AS, id);
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
