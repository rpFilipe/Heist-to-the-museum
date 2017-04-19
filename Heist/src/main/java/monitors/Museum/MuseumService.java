/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.Museum;

import Communication.Message;
import Communication.MessageException;
import Communication.ProxyInterface;
import static Communication.MessageType.*;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author ricardo
 */
public class MuseumService extends Museum implements ProxyInterface{

    public MuseumService(GeneralRepository genRepo) {
        super(genRepo);
    }

    @Override
    public Message processRequest(Message inMessage) throws MessageException{
        Message serverResponse = null;
        int[] messageArgs = inMessage.getArgs();
        
        try{
            switch(inMessage.getType()){
                case RAC:
                    boolean hasCanvas = super.rollACanvas(messageArgs[0]);
                    serverResponse = new Message(SERVER_RESPONSE, (hasCanvas) ? 1 : 0);
                    break;
                case GRD:
                    int roomId = super.getRoomDistance(messageArgs[0]);
                    serverResponse = new Message(SERVER_RESPONSE, roomId);
                    break;
                default:
                    throw new MessageException ("Invalid request for Museum Server", inMessage);

            }
        }
        catch(IndexOutOfBoundsException e){
            throw new MessageException ("Invalid number of arguments", inMessage);
        }
        return serverResponse;
    }
    
}
