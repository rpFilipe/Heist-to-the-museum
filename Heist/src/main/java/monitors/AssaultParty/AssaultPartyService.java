/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import monitors.ConcentrationSite.*;
import monitors.ControlAndCollectionSite.*;
import Communication.Message;
import Communication.MessageException;
import Communication.ProxyInterface;
import static Communication.MessageType.*;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author ricardo
 */
public class AssaultPartyService extends AssaultParty implements ProxyInterface {

    public AssaultPartyService(int partyId, GeneralRepository genRepo) {
        super(partyId, genRepo);
    }

    @Override
    public Message processRequest(Message inMessage) throws MessageException {
        Message serverResponse = null;
        int[] messageArgs = inMessage.getArgs();

        try {
            switch (inMessage.getType()) {
                case SR:
                    super.setRoom(messageArgs[0], messageArgs[1]);
                    serverResponse = new Message(ACK);
                    break;
                case SAP:
                    super.sendAssaultParty();
                    serverResponse = new Message(ACK);
                    break;
                case CI:
                    super.crawlIn(messageArgs[0]);
                    serverResponse = new Message(ACK);
                    break;
                case CO:
                    super.crawlOut(messageArgs[0]);
                    serverResponse = new Message(ACK);
                    break;
                case JP:
                    super.joinParty(messageArgs[0], messageArgs[1]);
                    serverResponse = new Message(ACK);
                    break;
                case RD:
                    super.reverseDirection();
                    serverResponse = new Message(ACK);
                    break;
                case GTR:
                    int roomId = super.getTargetRoom();
                    serverResponse = new Message(SERVER_RESPONSE, roomId);
                    break;
                default:
                    throw new MessageException("Invalid request for Museum Server", inMessage);

            }
        } catch (IndexOutOfBoundsException e) {
            throw new MessageException("Invalid number of arguments", inMessage);
        }
        return serverResponse;
    }

}
