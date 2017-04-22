/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitors.GeneralRepository;
import Communication.ServerCom;
import Communication.ServerServiceAgent;

/**
 *
 * @author Ricardo Filipe
 */
public class GeneralRepositoryStart {
    
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
        
        SERVER_PORT = Integer.parseInt(args[0]);
        
        // canais de comunicação
        ServerCom schan, schani;
        
        // thread agente prestador do serviço
        ServerServiceAgent cliProxy;         

        /* estabelecimento do servico */
        
        // criação do canal de escuta e sua associação
        schan = new ServerCom(SERVER_PORT);    
        schan.start();
        
        GeneralRepositoryService generalRepositoryService = new GeneralRepositoryService();
        System.out.println("GeneralRepository service has started!");
        System.out.printf("Server is listening on port: %d ... \n" , SERVER_PORT);

        /* processamento de pedidos */     
        while (true) {
            
            // entrada em processo de escuta
            schani = schan.accept();
            // lançamento do agente prestador do serviço
            cliProxy = new ServerServiceAgent(schani, generalRepositoryService);
            cliProxy.start();
        }
    }
    
}
