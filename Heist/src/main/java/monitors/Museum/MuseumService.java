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
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author ricardo
 */
public class MuseumService extends Museum implements ProxyInterface {

    /**
     * Create a new Service to handle the requests for the Museum.
     * @param genRepo General Repository Instance
     * @param N_ROOMS Number of Rooms in the simulation.
     * @param MAX_ROOM_DISTANCE Maximum distance a Room can be in the simulation.
     * @param MIN_ROOM_DISTANCE Minimum distance a Room can be in the simulation.
     * @param MAX_PAITING_PER_ROOM Maximum number of paintings in a given Room.
     * @param MIN_PAITING_PER_ROOM Minimum number of paintings in a given Room.
     */
    public MuseumService(ImonitorsGeneralRepository genRepo,
            int N_ROOMS,
            int MAX_ROOM_DISTANCE,
            int MIN_ROOM_DISTANCE,
            int MAX_PAITING_PER_ROOM,
            int MIN_PAITING_PER_ROOM) {
        super(genRepo, N_ROOMS,
                MAX_ROOM_DISTANCE,
                MIN_ROOM_DISTANCE,
                MAX_PAITING_PER_ROOM,
                MIN_PAITING_PER_ROOM);
    }

    /**
     * Process a Message for this Service
     * @param inMessage Incoming Message
     * @return Outgoing Message 
     * @throws MessageException
     */
    @Override
    public Message processRequest(Message inMessage) throws MessageException {
        Message serverResponse = null;
        int[] messageArgs = inMessage.getArgs();

        try {
            switch (inMessage.getType()) {
                case RAC:
                    boolean hasCanvas = super.rollACanvas(messageArgs[0]);
                    serverResponse = new Message(SERVER_RESPONSE, (hasCanvas) ? 1 : 0);
                    break;
                case GRD:
                    int roomId = super.getRoomDistance(messageArgs[0]);
                    serverResponse = new Message(SERVER_RESPONSE, roomId);
                    break;
                case TERMINATE:
                    System.exit(0);
                default:
                    throw new MessageException("Invalid request for Museum Server", inMessage);

            }
        } catch (IndexOutOfBoundsException e) {
            throw new MessageException("Invalid number of arguments", inMessage);
        }
        return serverResponse;
    }

}
