package Configuration;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import Communication.Message;
import Communication.MessageException;
import static Communication.MessageType.*;
import Communication.ProxyInterface;
import Communication.ServerCom;
import Communication.ServerServiceAgent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

/**
 *
 * @author Ricardo Filipe
 */
public class ConfigurationServer {

    private static int SERVER_PORT;
    private static HashMap<String, ServerLocation> serversLocation;

    /**
     * This class will launch one server listening one port and processing the
     * events.
     *
     * @param args
     */
    public static void main(String[] args) {

        SERVER_PORT = Integer.parseInt(args[0]);
        String filename = args[1];
        System.out.println(filename);
        serversLocation = new HashMap<>();
        loadConfiguration(filename);

        // canais de comunicação
        ServerCom schan, schani;

        // thread agente prestador do serviço
        ServerServiceAgent cliProxy;
        /* estabelecimento do servico */

        // criação do canal de escuta e sua associação
        schan = new ServerCom(SERVER_PORT);
        schan.start();

        ConfigurationService configurationService = new ConfigurationService();
        System.out.println("Configuration service has started!");
        System.out.printf("Server is listening on port: %d ... \n", SERVER_PORT);

        /* processamento de pedidos */
        while (true) {

            // entrada em processo de escuta
            schani = schan.accept();
            // lançamento do agente prestador do serviço
            cliProxy = new ServerServiceAgent(schani, configurationService);
            cliProxy.start();
        }
    }

    private static void loadConfiguration(String filename) {
        try {
            File f = new File(filename);
            Scanner fl = new Scanner(f);
            String[] colums;
            while (fl.hasNextLine()) {
                colums = fl.nextLine().split(",");
                ServerLocation sl = new ServerLocation(colums[1], Integer.parseInt(colums[2]));
                serversLocation.put(colums[0], sl);
            }

        } catch (FileNotFoundException ex) {
            System.out.print(ex);
        }
    }

    private static class ConfigurationService implements ProxyInterface {

        @Override
        public Message processRequest(Message inMessage) throws MessageException {
            Message outMessage = null;
            String svname;
            ServerLocation sl;
            System.out.println(inMessage);
            switch (inMessage.getType()) {
                case CONFIGURATION_REQUEST_PORT:
                    svname = inMessage.getReturnStr();
                    sl = serversLocation.get(svname);
                    outMessage = new Message(SERVER_RESPONSE, sl.port);
                    break;
                case CONFIGURATION_REQUEST_LOCATION:
                    svname = inMessage.getReturnStr();
                    sl = serversLocation.get(svname);
                    outMessage = new Message(SERVER_RESPONSE, sl.host);
                    break;
                case TERMINATE:
                    System.exit(0);
                default:
                    throw new MessageException("Invalid Request", inMessage);
            }

            return outMessage;
        }
    }

    private static class ServerLocation {

        int port;
        String host;

        public ServerLocation(String host, int port) {
            this.port = port;
            this.host = host;
        }
    }

}
