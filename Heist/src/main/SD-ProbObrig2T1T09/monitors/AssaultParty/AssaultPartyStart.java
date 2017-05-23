/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import Communication.ServerCom;
import Communication.ServerServiceAgent;
import Proxies.SettingsProxy;
import Proxies.GeneralRepositoryProxy;
import monitors.GeneralRepository.ImonitorsGeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class AssaultPartyStart {

    private static int SERVER_PORT;
    private static int PARTY_ID;
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
        PARTY_ID = Integer.parseInt(args[1]);
        CONFIG_SERVER_ADDR = args[2];
        CONFIG_SERVER_PORT = Integer.parseInt(args[3]);

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

        int aps = sp.getASSAULT_PARTY_SIZE();
        
        AssaultPartyService assaultPartyService = new AssaultPartyService(PARTY_ID, (ImonitorsGeneralRepository) genRepo, aps);
        System.out.println("AssaultParty service has started!");
        System.out.printf("Server is listening on port: %d ... \n", SERVER_PORT);

        /* processamento de pedidos */
        while (true) {

            // entrada em processo de escuta
            schani = schan.accept();
            // lançamento do agente prestador do serviço
            cliProxy = new ServerServiceAgent(schani, assaultPartyService);
            cliProxy.start();
        }
    }

}