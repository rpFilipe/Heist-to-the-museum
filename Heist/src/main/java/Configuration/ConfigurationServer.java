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
    private static HashMap<String, Integer> simulationParameters;

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
        simulationParameters = new HashMap<>();
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
            String line;
            while (fl.hasNextLine()) {
                line = fl.nextLine();
                if (line.equals("SimulationParameters:"))
                    break;
                colums = line.split(",");
                ServerLocation sl = new ServerLocation(colums[1], Integer.parseInt(colums[2]));
                serversLocation.put(colums[0], sl);
            }
            // Adding the constants
            while (fl.hasNextLine()) {
                line = fl.nextLine();
                colums = line.split(",");
                simulationParameters.put(colums[0], Integer.parseInt(colums[1]));
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
            int value;
            ServerLocation sl;
            switch (inMessage.getType()) {
                case CONFIGURATION_REQUEST_PORT:
                    svname = inMessage.getReturnStr();
                    sl = serversLocation.get(svname);
                    System.out.printf("%s - %d\n", svname, sl.port);
                    outMessage = new Message(SERVER_RESPONSE, sl.port);
                    break;
                case CONFIGURATION_REQUEST_LOCATION:
                    svname = inMessage.getReturnStr();
                    sl = serversLocation.get(svname);
                    outMessage = new Message(SERVER_RESPONSE, sl.host);
                    break;
                case NR:
                    value = simulationParameters.get("NR");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case NOT:
                    value = simulationParameters.get("NOT");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MAPPR:
                    value = simulationParameters.get("MAPPR");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MIPPR:
                    value = simulationParameters.get("MIPPR");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MARD:
                    value = simulationParameters.get("MARD");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MIRD:
                    value = simulationParameters.get("MIRD");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case APS:
                    value = simulationParameters.get("APS");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case NAP:
                    value = simulationParameters.get("NAP");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MD:
                    value = simulationParameters.get("MD");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MATS:
                    value = simulationParameters.get("MATS");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MITS:
                    value = simulationParameters.get("MITS");
                    outMessage = new Message(SERVER_RESPONSE, value);
                    break;
                case MDBT:
                    value = simulationParameters.get("MDBT");
                    outMessage = new Message(SERVER_RESPONSE, value);
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
