/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.Museum;

import Communication.ServerCom;
import Communication.ServerServiceAgent;
import monitors.GeneralRepository.GeneralRepository;

/**
 *
 * @author Ricardo Filipe
 */
public class MuseumStart {
    
    private static int SERVER_PORT;
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
        
        SERVER_PORT = 5003;
        
        // canais de comunicação
        ServerCom schan, schani;
        
        // thread agente prestador do serviço
        ServerServiceAgent cliProxy;         
        
        GeneralRepository genRep = new GeneralRepository();

        /* estabelecimento do servico */
        
        // criação do canal de escuta e sua associação
        schan = new ServerCom(SERVER_PORT);    
        schan.start();
        
        MuseumService museumService = new MuseumService(genRep);
        System.out.println("Museum service has started!");
        System.out.printf("Server is listening on port: %d ... \n" , SERVER_PORT);

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
