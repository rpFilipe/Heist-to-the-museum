/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ControlAndCollectionSite;

import Communication.Message;
import Communication.MessageException;
import Communication.ProxyInterface;
import static Communication.MessageType.*;
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author ricardo
 */
public class ControlAndCollectionSiteService extends ControlAndCollectionSite implements ProxyInterface {

    /**
     * Create a new Service to handle the requests for the Control and Collection Site.
     * @param genRepo General Repository instance
     * @param nrooms Number of Rooms in the Simulation
     * @param n_assault_parties Number of Assault Parties in the Simulation
     * @param assault_party_size Number of Ordinary Thieves in each Assault Party.
     */
    public ControlAndCollectionSiteService(ImonitorsGeneralRepository genRepo, int nrooms, int n_assault_parties, int assault_party_size) {
        super(genRepo, nrooms, n_assault_parties, assault_party_size);
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
                case GTR:
                    int targetRoom = super.getTargetRoom();
                    serverResponse = new Message(SERVER_RESPONSE, targetRoom);
                    break;
                case GPTD:
                    int partyId = super.getPartyToDeploy();
                    serverResponse = new Message(SERVER_RESPONSE, partyId);
                    break;
                case TAR:
                    super.takeARest();
                    serverResponse = new Message(ACK);
                    break;
                case CC:
                    super.collectCanvas();
                    serverResponse = new Message(ACK);
                    break;
                case IHC:
                    boolean completed = super.isHeistCompleted();
                    serverResponse = new Message(SERVER_RESPONSE, (completed) ? 1 : 0);
                    break;
                case WN:
                    boolean waiting_needed = super.waitingNedded();
                    serverResponse = new Message(SERVER_RESPONSE, (waiting_needed) ? 1 : 0);
                    break;
                case HAC:
                    boolean hasCanvas = (messageArgs[1] != 0);
                    super.handACanvas(messageArgs[0], hasCanvas, messageArgs[2], messageArgs[3]);
                    serverResponse = new Message(ACK);
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
