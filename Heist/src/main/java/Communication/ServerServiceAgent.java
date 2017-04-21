package Communication;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *   Este tipo de dados define o thread agente prestador de serviço dos clientes para uma solução do Problema dos
 *   Barbeiros Sonolentos que implementa o modelo cliente-servidor de tipo 2 (replicação do servidor) com lançamento
 *   dinâmico dos threads barbeiro.
 *   A comunicação baseia-se em passagem de mensagens sobre sockets usando o protocolo TCP.
 */

public class ServerServiceAgent extends Thread
{
   private static int nProxy;
   private ServerCom sconi;
   private ProxyInterface proxyInterface;

   public ServerServiceAgent (ServerCom sconi, ProxyInterface proxyInterface)
   {
      //super ("Proxy_" + getProxyId ());
       System.out.println("Message Arrinves");
      this.sconi = sconi;
      this.proxyInterface = proxyInterface;
   }

  /**
   *  Ciclo de vida do thread agente prestador de serviço.
   */

   @Override
   public void run()
   {
       try {
           Message inMessage = null,                            // mensagem de entrada
                   outMessage = null;                           // mensagem de saída
           inMessage = (Message) sconi.readObject ();                     // ler pedido do cliente
           outMessage = proxyInterface.processRequest(inMessage);         // processá-lo
           sconi.writeObject (outMessage);                                // enviar resposta ao cliente
           sconi.close ();                                                // fechar canal de comunicação
       } catch (MessageException ex) {
           Logger.getLogger(ServerServiceAgent.class.getName()).log(Level.SEVERE, null, ex);
       }
   }

  /**
   *  Geração do identificador da instanciação.
   *
   *    @return identificador da instanciação
   */

   private static int getProxyId ()
   {
      Class<Communication.ServerServiceAgent> ssa = null;           // representação do tipo de dados CustomerProxy na máquina
                                                           //   virtual de Java
      int proxyId;                                         // identificador da instanciação

      try
      { ssa = (Class<Communication.ServerServiceAgent>) Class.forName ("serverSide.CustomerProxy");
      }
      catch (ClassNotFoundException e)
      { System.out.println("O tipo de dados ClientProxy não foi encontrado!");
        e.printStackTrace ();
        System.exit (1);
      }

      synchronized (ssa)
      { proxyId = nProxy;
        nProxy += 1;
      }

      return proxyId;
   }
}
