/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.Museum;

import Communication.ServerCom;
import Communication.ServerServiceAgent;
import Proxies.SettingsProxy;
import Proxies.GeneralRepositoryProxy;

/**
 *
 * @author Ricardo Filipe
 */
public class MuseumStart {

    private static int SERVER_PORT;
    private static String CONFIG_SERVER_ADDR;
    private static int CONFIG_SERVER_PORT;

    /**
     * This class will launch one server listening one port and processing the
     * events.
     *
     * @param args
     */
    public static void main(String[] args) {
        /* TODO
        NodeSettsProxy proxy = new NodeSettsProxy(); 
        SERVER_PORT = proxy.SERVER_PORTS().get("Playground");
         */

        SERVER_PORT = Integer.parseInt(args[0]);
        CONFIG_SERVER_ADDR = args[1];
        CONFIG_SERVER_PORT = Integer.parseInt(args[2]);

        // canais de comunicação
        ServerCom schan, schani;

        // thread agente prestador do serviço
        ServerServiceAgent cliProxy;

        GeneralRepositoryProxy genRepo = new GeneralRepositoryProxy(CONFIG_SERVER_ADDR, CONFIG_SERVER_PORT);
        SettingsProxy sp = new SettingsProxy(CONFIG_SERVER_ADDR, CONFIG_SERVER_PORT);
        /* estabelecimento do servico */

        // criação do canal de escuta e sua associação
        schan = new ServerCom(SERVER_PORT);
        schan.start();

        //simulation parameters
        int nrooms = sp.getN_ROOMS();
        int max_room_distance = sp.getMAX_ROOM_DISTANCE();
        int min_room_distance = sp.getMIN_ROOM_DISTANCE();
        int max_paitings_per_room = sp.getMAX_PAITING_PER_ROOM();
        int min_paitings_per_room = sp.getMIN_PAITING_PER_ROOM();

        MuseumService museumService = new MuseumService(genRepo, nrooms, max_room_distance, min_room_distance, max_paitings_per_room, min_paitings_per_room);
        System.out.println("Museum service has started!");
        System.out.printf("Server is listening on port: %d ... \n", SERVER_PORT);

        /* processamento de pedidos */
        while (true) {

            // entrada em processo de escuta
            schani = schan.accept();
            // lançamento do agente prestador do serviço
            cliProxy = new ServerServiceAgent(schani, museumService);
            cliProxy.start();
        }
    }

}
