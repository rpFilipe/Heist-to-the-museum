/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import interfaces.GeneralRepositoryInterface;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ricardo Filipe
 */
public class MasterThiefStart {
     
    public static void main(String args[])
   {
    Scanner sc = new Scanner(System.in);   
     /* get location of the generic registry service */
     String rmiRegHostName;
     int rmiRegPortNumb;

     System.out.println ("Nome do nó de processamento onde está localizado o serviço de registo? ");
     rmiRegHostName = sc.nextLine();
     System.out.println ("Número do port de escuta do serviço de registo? ");
     rmiRegPortNumb = sc.nextInt();

     /* look for the remote object by name in the remote host registry */
     String nameEntry = "GeneralRepository";
     GeneralRepositoryInterface genRepInterface = null;
     Registry registry = null;

     try
     { registry = LocateRegistry.getRegistry (rmiRegHostName, rmiRegPortNumb);
     }
     catch (RemoteException e)
     { System.out.println ("RMI registry creation exception: " + e.getMessage ());
       e.printStackTrace ();
       System.exit (1);
     }
     
     /*
     Fazer com os outras interfaces dos monitores onde atua o MasterThief
     */

    System.out.println("Alert Logger that I have finished!");
        
    try {
        genRepInterface.signalShutdown();
    } catch (RemoteException ex) {
        Logger.getLogger(MasterThiefStart.class.getName()).log(Level.SEVERE, null, ex);
    }

     /* print the result */
     System.out.println("Done!");
  }   
}
