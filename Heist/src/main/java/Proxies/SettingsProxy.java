/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Proxies;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;

/**
 *
 * @author ricardo
 */
public class SettingsProxy {

    private String SERVER_ADDR;
    private int SERVER_PORT;
    private int N_ROOMS = 5;
    private int N_ORD_THIEVES = 6;
    private int MAX_PAITING_PER_ROOM = 16;
    private int MIN_PAITING_PER_ROOM = 8;
    private int MAX_ROOM_DISTANCE = 30;
    private int MIN_ROOM_DISTANCE = 15;
    private int ASSAULT_PARTY_SIZE = 3;
    private int N_ASSAULT_PARTIES;
    private int MAX_DISPLACEMENT = 2;
    private int MAX_THIEF_SPEED = 6;
    private int MIN_THIEF_SPEED = 2;
    private int MAX_DISTANCE_BETWEEN_THIVES = 5;

    /**
     * Create a Proxy to Communicate with the Settings Server
     * @param configServerAddr Configuration Server address.
     * @param configServerPort Configuration Server port.
     */
    public SettingsProxy(String configServerAddr, int configServerPort) {
        this.SERVER_ADDR = configServerAddr;
        this.SERVER_PORT = configServerPort;
        N_ROOMS = getNR();
        N_ORD_THIEVES = getNOT();
        MAX_PAITING_PER_ROOM = getMAPPR();
        MIN_PAITING_PER_ROOM = getMIPPR();
        MAX_ROOM_DISTANCE = getMARD();
        MIN_ROOM_DISTANCE = getMIRD();
        ASSAULT_PARTY_SIZE = getAPS();
        N_ASSAULT_PARTIES = N_ORD_THIEVES / ASSAULT_PARTY_SIZE;
        MAX_DISPLACEMENT = getMD();
        MAX_THIEF_SPEED = getMATS();
        MIN_THIEF_SPEED = getMITS();
        MAX_DISTANCE_BETWEEN_THIVES = getMDBT();
    }

    /**
     * Get the number of Rooms in the simulation. 
     * @return Number of Rooms in the simulation.
     */
    public int getN_ROOMS() {
        return N_ROOMS;
    }

    /**
     * Get the number of Ordinary Thieves in the simulation. 
     * @return Number of Ordinary Thieves in the simulation.
     */
    public int getN_ORD_THIEVES() {
        return N_ORD_THIEVES;
    }

    /**
     * Get the number of maximum paintings per Room. 
     * @return Number of maximum paintings per Room.
     */
    public int getMAX_PAITING_PER_ROOM() {
        return MAX_PAITING_PER_ROOM;
    }

    /**
     * Get the number of minimum paintings per Room. 
     * @return Number of minimum paintings per Room.
     */
    public int getMIN_PAITING_PER_ROOM() {
        return MIN_PAITING_PER_ROOM;
    }

    /**
     * Get the maximum allowed distance of a Room. 
     * @return Maximum allowed distance of a Room.
     */
    public int getMAX_ROOM_DISTANCE() {
        return MAX_ROOM_DISTANCE;
    }

    /**
     * Get the minimum allowed distance of a Room. 
     * @return Minimum allowed distance of a Room.
     */
    public int getMIN_ROOM_DISTANCE() {
        return MIN_ROOM_DISTANCE;
    }

    /**
     * Get the size of an Assault Party. 
     * @return Assault Party size.
     */
    public int getASSAULT_PARTY_SIZE() {
        return ASSAULT_PARTY_SIZE;
    }

    /**
     * Get the maximum displacement.
     * @return Maximum displacement.
     */
    public int getMAX_DISPLACEMENT() {
        return MAX_DISPLACEMENT;
    }

    /**
     * Get the maximum allowed speed of a Thief. 
     * @return Maximum allowed speed of a Thief. 
     */
    public int getMAX_THIEF_SPEED() {
        return MAX_THIEF_SPEED;
    }

    /**
     * Get the minimum allowed speed of a Thief. 
     * @return Minimum allowed speed of a Thief. 
     */
    public int getMIN_THIEF_SPEED() {
        return MIN_THIEF_SPEED;
    }

      /**
     * Get the maximum allowed distance between thieves.
     * @return Maximum allowed distance between thieves. 
     */
    public int getMAX_DISTANCE_BETWEEN_THIVES() {
        return MAX_DISTANCE_BETWEEN_THIVES;
    }

    private int getNR() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(NR);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getNOT() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(NOT);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMAPPR() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MAPPR);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMIPPR() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MIPPR);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMARD() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MARD);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMIRD() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MIRD);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getAPS() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(APS);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getNAP() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(NAP);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMD() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MD);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMATS() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MATS);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMITS() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MITS);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private int getMDBT() {
        ClientCom con = new ClientCom(SERVER_ADDR, SERVER_PORT);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }
        outMessage = new Message(MDBT);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

}
