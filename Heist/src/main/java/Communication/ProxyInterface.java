/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

/**
 *
 * @author Ricardo Filipe
 * 221GX xE[0,9]
 * l040101-ws??.clients.ua.pt
 * sd010g
 */
public interface ProxyInterface {

    /**
     * Process and reply to an incoming  Message
     * @param inMessage Incoming Message
     * @return Outgoing Message 
     * @throws MessageException
     */
    public Message processRequest(Message inMessage) throws MessageException;
}
