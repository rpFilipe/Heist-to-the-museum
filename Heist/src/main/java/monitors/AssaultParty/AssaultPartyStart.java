/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.AssaultParty;

import monitors.ConcentrationSite.*;
import monitors.ControlAndCollectionSite.*;
import monitors.Museum.*;
import Communication.ServerCom;
import Communication.ServerServiceAgent;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class AssaultPartyStart {
    
    private static int SERVER_PORT;
    private static int PARTY_ID;
    /**
     * This class will launch one server listening one port and processing
     * the events.
     * @param args
     */
    public static void main(String[] args){
        /* TODO
        NodeSettsProxy proxy = new NodeSettsProxy(); 
        SERVER_PORT = proxy.SERVER_PORTS().get("Playground");
        */
        
        SERVER_PORT = Integer.parseInt(args[0]);
        PARTY_ID = Integer.parseInt(args[1]);
        
        // canais de comunicação
        ServerCom schan, schani;
        
        // thread agente prestador do serviço
        ServerServiceAgent cliProxy;         
        
        GeneralRepository genRep = new GeneralRepository();

        /* estabelecimento do servico */
        
        // criação do canal de escuta e sua associação
        schan = new ServerCom(SERVER_PORT);    
        schan.start();
        
        AssaultPartyService assaultPartyService = new AssaultPartyService(PARTY_ID, genRep);
        System.out.println("AssaultParty service has started!");
        System.out.printf("Server is listening on port: %d ... \n" , SERVER_PORT);

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
