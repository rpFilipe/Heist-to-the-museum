/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;

import Communication.Message;
import Communication.MessageException;
import Communication.ProxyInterface;
import static Communication.MessageType.*;

/**
 *
 * @author ricardo
 */
public class GeneralRepositoryService extends GeneralRepository implements ProxyInterface {

    public GeneralRepositoryService() {
        super();
    }

    @Override
    public Message processRequest(Message inMessage) throws MessageException {
        Message serverResponse = null;
        int[] messageArgs = inMessage.getArgs();

        try {
            switch (inMessage.getType()) {
                case FN:
                    super.FinalizeLog();
                    serverResponse = new Message(ACK);
                    break;
                case AT:
                    super.addThief(messageArgs[0], messageArgs[1]);
                    serverResponse = new Message(ACK);
                    break;
                case CP:
                    super.clearParty(messageArgs[0]);
                    serverResponse = new Message(ACK);
                    break;
                case SCC:
                    super.setCollectedCanvas(messageArgs[0]);
                    serverResponse = new Message(ACK);
                    break;
                case SPE:
                    super.setPartyElement(messageArgs[0], messageArgs[1], messageArgs[2]);
                    serverResponse = new Message(ACK);
                    break;
                case SRA:
                    super.setRoomAtributes(messageArgs[0], messageArgs[1], messageArgs[2]);
                    serverResponse = new Message(ACK);
                    break;
                case SRC:
                    super.setRoomCanvas(messageArgs[0], messageArgs[1]);
                    serverResponse = new Message(ACK);
                    break;
                case SRIAP:
                    super.setRoomIdAP(messageArgs[0], messageArgs[1]);
                    serverResponse = new Message(ACK);
                    break;
                case UMTS:
                    super.updateMThiefState(messageArgs[0]);
                    serverResponse = new Message(ACK);
                    break;
                case UTC:
                    boolean b = messageArgs[1] != 0;
                    super.updateThiefCylinder(messageArgs[0], b);
                    serverResponse = new Message(ACK);
                    break;
                case UTP:
                    super.updateThiefPosition(messageArgs[0], messageArgs[1]);
                    serverResponse = new Message(ACK);
                    break;
                case UTST:
                    char c = (char) messageArgs[1];
                    super.updateThiefSituation(messageArgs[0], c);
                    serverResponse = new Message(ACK);
                    break;
                case UTS:
                    super.updateThiefState(messageArgs[0], messageArgs[1]);
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
