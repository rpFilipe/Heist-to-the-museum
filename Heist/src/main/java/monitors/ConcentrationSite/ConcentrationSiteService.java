/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ConcentrationSite;

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
public class ConcentrationSiteService extends ConcentrationSite implements ProxyInterface {

    public ConcentrationSiteService(GeneralRepository genRepo) {
        super(genRepo);
    }

    @Override
    public Message processRequest(Message inMessage) throws MessageException {
        Message serverResponse = null;
        int[] messageArgs = inMessage.getArgs();

        try {
            switch (inMessage.getType()) {
                case SO:
                    super.startOperations();
                    serverResponse = new Message(ACK);
                    break;
                case PAP:
                    super.prepareAssaultParty(messageArgs[0]);
                    serverResponse = new Message(ACK);
                    break;
                case SUR:
                    super.sumUpResults();
                    serverResponse = new Message(ACK);
                    break;
                case AS:
                    boolean heistCompleted = messageArgs[0] != 0;
                    boolean waitingNeeded = messageArgs[1] != 0;
                    int sit = super.appraiseSit(heistCompleted, waitingNeeded);
                    serverResponse = new Message(SERVER_RESPONSE, sit);
                    break;
                case AIN:
                    boolean isNeeded = super.amINeeded(messageArgs[0]);
                    serverResponse = new Message(SERVER_RESPONSE, (isNeeded) ? 1 : 0);
                    break;
                case GPI:
                    int partyId = super.getPartyId(messageArgs[0]);
                    serverResponse = new Message(SERVER_RESPONSE, partyId);
                    break;
                case PE:
                    super.prepareExcursion(messageArgs[0]);
                    serverResponse = new Message(ACK);
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
