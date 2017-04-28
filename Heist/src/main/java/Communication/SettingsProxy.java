/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Communication;

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

    public SettingsProxy(String SERVER_ADDR, int SERVER_PORT) {
        this.SERVER_ADDR = SERVER_ADDR;
        this.SERVER_PORT = SERVER_PORT;
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

    public int getN_ROOMS() {
        return N_ROOMS;
    }

    public int getN_ORD_THIEVES() {
        return N_ORD_THIEVES;
    }

    public int getMAX_PAITING_PER_ROOM() {
        return MAX_PAITING_PER_ROOM;
    }

    public int getMIN_PAITING_PER_ROOM() {
        return MIN_PAITING_PER_ROOM;
    }

    public int getMAX_ROOM_DISTANCE() {
        return MAX_ROOM_DISTANCE;
    }

    public int getMIN_ROOM_DISTANCE() {
        return MIN_ROOM_DISTANCE;
    }

    public int getASSAULT_PARTY_SIZE() {
        return ASSAULT_PARTY_SIZE;
    }

    public int getMAX_DISPLACEMENT() {
        return MAX_DISPLACEMENT;
    }

    public int getMAX_THIEF_SPEED() {
        return MAX_THIEF_SPEED;
    }

    public int getMIN_THIEF_SPEED() {
        return MIN_THIEF_SPEED;
    }

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
