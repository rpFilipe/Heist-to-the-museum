/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

import java.io.Serializable;

/**
 *
 * @author Ricardo Filipe
 */
/**
 * Este tipo de dados define as mensagens que são trocadas entre os clientes e o
 * servidor numa solução do Heist to the Museum que implementa o modelo
 * cliente-servidor de tipo 2 (replicação do servidor). A comunicação
 * propriamente dita baseia-se na troca de objectos de tipo Message num canal
 * TCP.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = 1001L;
    private MessageType msgType;
    private int[] args;
    private String returnStr;
    private int returnValue;

    /**
     * Create a new Message.
     * @param msgType Type of the Message.
     */
    public Message(MessageType msgType) {
        this.msgType = msgType;
    }

    /**
     * Create a new Message.
     * @param msgType Type of the Message.
     * @param returnValue Integer value to send.
     */
    public Message(MessageType msgType, int returnValue) {
        this.msgType = msgType;
        this.returnValue = returnValue;
    }

    /**
     * Get the String sent in this Message.
     * @return String that has been sent.
     */
    public String getReturnStr() {
        return returnStr;
    }
    
    /**
     * Create a new Message.
     * @param msgType Type of the Message.
     * @param str String value to send.
     */
    public Message(MessageType msgType, String str) {
        this.msgType = msgType;
        this.returnStr = str;
    }

    /**
     * Get the Integer sent in this Message.
     * @return Integer that has been sent.
     */
    public int getReturnValue() {
        return returnValue;
    }

    /**
     * Create a new Message
     * @param msgType Type of the Message.
     * @param args Array of Integers to be sent.
     */
    public Message(MessageType msgType, int[] args) {
        this.msgType = msgType;
        this.args = args;
    }

    /**
     * Get the Message type.
     * @return MessageType.
     */
    public MessageType getType() {
        return (msgType);
    }

    /**
     * Get the arguments of this Message.
     * @return Array of Integers that have been sent.
     */
    public int[] getArgs() {
        return this.args;
    }
}
