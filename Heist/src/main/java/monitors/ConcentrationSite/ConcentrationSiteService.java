/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.ConcentrationSite;

import Communication.Message;
import Communication.MessageException;
import Communication.ProxyInterface;
import static Communication.MessageType.*;
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author ricardo
 */
public class ConcentrationSiteService extends ConcentrationSite implements ProxyInterface {

    public ConcentrationSiteService(ImonitorsGeneralRepository genRepo) {
        super(genRepo);
    }

    @Override
    public Message processRequest(Message inMessage) throws MessageException {
        Message serverResponse = null;
        int[] messageArgs = inMessage.getArgs();
        System.out.println(messageArgs);
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
                case TERMINATE:
                    System.exit(0);
                default:
                    throw new MessageException("Invalid request for Museum Server", inMessage);

            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e);
            System.out.println(inMessage);
            throw new MessageException("Invalid number of arguments", inMessage);
        }
        return serverResponse;
    }

}
