package rmi.project;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class THServer {
    public THServer() {
        try {
            // create the registry
            try {
                LocateRegistry.createRegistry(1099);
            } catch (RemoteException e) {
                System.out.println("Registry port already exists.");
            }
            
            // create remote object
            THInterface th = new THImpl();
            // bind remote object in the registry
            Naming.rebind("rmi://localhost/THInterfaceService", th);

            System.out.println("Server has established connection.");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

    public static void main(String[] args) {
        new THServer();
    }
}
