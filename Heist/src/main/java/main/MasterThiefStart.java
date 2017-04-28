/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import Communication.ClientCom;
import Communication.Message;
import static Communication.MessageType.*;
import Communication.SettingsProxy;
import java.util.logging.Level;
import java.util.logging.Logger;
import monitors.AssaultParty.ImtAssaultParty;
import monitors.ConcentrationSite.ImtConcentrationSite;
import monitors.ControlAndCollectionSite.ImtControlAndCollectionSite;
import monitors.GeneralRepository.ImtGeneralRepository;
import monitors.Museum.ImtMuseum;

/**
 *
 * @author Ricardo Filipe
 */
public class MasterThiefStart {

    private static String configServerAddr;
    private static int configServerPort;
    private static int N_ASSAULT_PARTIES;

    public static void main(String[] args) {

        configServerAddr = args[0];
        configServerPort = Integer.parseInt(args[1]);

        SettingsProxy sp = new SettingsProxy(configServerAddr, configServerPort);

        N_ASSAULT_PARTIES = sp.getN_ORD_THIEVES() / sp.getASSAULT_PARTY_SIZE();

        GeneralRepositoryProxy genRepo = new GeneralRepositoryProxy(configServerAddr, configServerPort);
        MuseumProxy museum = new MuseumProxy(configServerAddr, configServerPort);
        ControlAndCollectionSiteProxy controlCollectionSite = new ControlAndCollectionSiteProxy(configServerAddr, configServerPort);
        ConcentrationSiteProxy concentrationSite = new ConcentrationSiteProxy(configServerAddr, configServerPort);
        AssaultPartyProxy[] assaultParty = new AssaultPartyProxy[N_ASSAULT_PARTIES];

        for (int i = 0; i < N_ASSAULT_PARTIES; i++) {
            assaultParty[i] = new AssaultPartyProxy(i, configServerAddr, configServerPort);
        }

        MasterThief mt = new MasterThief((ImtMuseum) museum,
                (ImtConcentrationSite) concentrationSite,
                (ImtControlAndCollectionSite) controlCollectionSite,
                (ImtAssaultParty[]) assaultParty,
                (ImtGeneralRepository) genRepo);

        mt.start();

        try {
            mt.join();
            terminateServers();
        } catch (InterruptedException ex) {
            Logger.getLogger(MasterThiefStart.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void terminateServers() {
        String[] servers = new String[]{"AssaultParty0",
            "AssaultParty1",
            "Museum",
            "ControlAndCollectionSite",
            "ConcentrationSite",
            "GeneralRepository"
        };

        for (String s : servers) {
            String addr = getServerLocation(configServerAddr, configServerPort, s);
            int port = getServerPort(configServerAddr, configServerPort, s);
            sendTerminateSignal(addr, port);
            System.out.printf("%s server has ended.\n", s);
        }
        sendTerminateSignal(configServerAddr, configServerPort);
        System.out.printf("%s server has ended.\n", "ConfigurationServer");
    }

    private static String getServerLocation(String configServerAddr, int configServerPort, String svname) {
        ClientCom con = new ClientCom(configServerAddr, configServerPort);
        Message inMessage, outMessage;

        if (!con.open()) {
            return "";
        }

        outMessage = new Message(CONFIGURATION_REQUEST_LOCATION, svname);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnStr();
    }

    private static int getServerPort(String configServerAddr, int configServerPort, String svname) {
        ClientCom con = new ClientCom(configServerAddr, configServerPort);
        Message inMessage, outMessage;

        if (!con.open()) {
            return -1;
        }

        outMessage = new Message(CONFIGURATION_REQUEST_PORT, svname);
        con.writeObject(outMessage);

        inMessage = (Message) con.readObject();

        if (inMessage.getType() != SERVER_RESPONSE) {
            System.exit(1);
        }

        return inMessage.getReturnValue();
    }

    private static void sendTerminateSignal(String addr, int port) {
        ClientCom con = new ClientCom(addr, port);
        Message inMessage, outMessage;

        if (!con.open()) {
            System.exit(1);
        }
        outMessage = new Message(TERMINATE);
        con.writeObject(outMessage);
    }
}
