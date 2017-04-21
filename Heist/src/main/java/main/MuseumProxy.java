/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import monitors.Museum.ImtMuseum;
import monitors.Museum.IotMuseum;

/**
 *
 * @author Ricardo Filipe
 */
public class MuseumProxy implements ImtMuseum, IotMuseum{
    String SERVER_ADDR;
    int SERVER_PORT;
    
    public MuseumProxy(String configServerAddr, int configServerPort) {
        SERVER_ADDR = getServerLocation(configServerAddr, configServerPort, "Museum");
        SERVER_PORT = getServerPort(configServerAddr, configServerPort, "Museum");
    }

    @Override
    public int getRoomDistance(int targetRoom){
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) return -1;
        int[] args = new int[] {targetRoom};
        outMessage = new Message(GRD, args);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        return inMessage.getReturnValue();
    }

    @Override
    public boolean rollACanvas(int roomId) {
        ClientCom con = new ClientCom (SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;
        
        if (!con.open ()) System.exit(1);
        int[] args = new int[] {roomId};
        outMessage = new Message(RAC, args);
        con.writeObject(outMessage);
        
        inMessage = (Message) con.readObject();
        
        if(inMessage.getType() != SERVER_RESPONSE)
            System.exit(1);
        
        boolean value = inMessage.getReturnValue() != 0;
        
        return value;
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
