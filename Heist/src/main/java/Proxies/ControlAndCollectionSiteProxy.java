/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proxies;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.ControlAndCollectionSite.IotControlAndCollectionSite;

/**
 *
 * @author Ricardo Filipe
 */
public class ControlAndCollectionSiteProxy implements ImtControlAndCollectionSite, IotControlAndCollectionSite{

    String SERVER_ADDR;
    int SERVER_PORT;
    
    /**
     * Create a Proxy to Communicate with the Control and Collection Site Server
     * @param configServerAddr Configuration Server address.
     * @param configServerPort Configuration Server port.
     */
    public ControlAndCollectionSiteProxy(String configServerAddr, int configServerPort) {
        SERVER_ADDR = getServerLocation(configServerAddr, configServerPort, "ControlAndCollectionSite");
        SERVER_PORT = getServerPort(configServerAddr, configServerPort, "ControlAndCollectionSite");
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
    public int getPartyToDeploy() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(GPTD);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        return inMessage.getReturnValue();
    }

    @Override
    public void takeARest() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(TAR);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public void collectCanvas() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(CC);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != ACK)
            System.exit(1);
    }

    @Override
    public boolean isHeistCompleted() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(IHC);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        boolean value = inMessage.getReturnValue() != 0;
        return value;
    }

    @Override
    public boolean waitingNedded() {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        outMessage = new Message(WN);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        boolean value = inMessage.getReturnValue() != 0;
        return value;
    }

    @Override
    public void handACanvas(int id, boolean canvas, int roomId, int partyId) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        
        int[] args = new int[] {id, (canvas) ? 1:0, roomId, partyId};
        
        outMessage = new Message(HAC, args);
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
