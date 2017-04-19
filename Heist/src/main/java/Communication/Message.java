/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

import java.io.Serializable;

/**
 *
 * @author Tiago Henriques
 */
/**
 * Este tipo de dados define as mensagens que são trocadas entre os clientes e o
 * servidor numa solução do Heist to the Museum que implementa o modelo
 * cliente-servidor de tipo 2 (replicação do servidor). A comunicação
 * propriamente dita baseia-se na troca de objectos de tipo Message num canal
 * TCP.
 */
public class Message implements Serializable {

    /**
     * Chave de serialização
     *
     * @serialField serialVersionUID
     */
    private static final long serialVersionUID = 1001L;
    private MessageType msgType;
    private int[] args;
    private int returnValue;

    public Message() {
        returnValue = -1;
    }

    /**
     * Logging file name
     *
     * @serialField fName
     */
    private String fName = null;

    /**
     * Thieves lifecyle iterations number
     *
     * @serialField nIter
     */
    private int nIter = -1;

    public Message(MessageType msgType) {
        this.msgType = msgType;
    }

    public Message(MessageType msgType, int returnValue) {
        this.returnValue = returnValue;
    }

    public Message(MessageType msgType, int[] args) {
        this(msgType);
        this.args = args;
    }

    public MessageType getType() {
        return (msgType);
    }

    /**
     * Obtenção do valor do campo nome do ficheiro de logging.
     *
     * @return nome do ficheiro
     */
    public String getFName() {
        return (fName);
    }

    /**
     * Obtenção do valor do campo número de iterações do ciclo de vida dos
     * clientes.
     *
     * @return número de iterações do ciclo de vida dos clientes
     */
    public int getNIter() {
        return (nIter);
    }

    /**
     * Impressão dos campos internos. Usada para fins de debugging.
     *
     * @return string contendo, em linhas separadas, a concatenação da
     * identificação de cada campo e valor respectivo
     */
    @Override
    public String toString() {
        return ("Tipo = " + msgType
                + "\nThief Id = " + ""
                + "\nLogging File = " + fName
                + "\nNumber of iterations = " + nIter);
    }

    public int[] getArgs() {
        return this.args;
    }
}
