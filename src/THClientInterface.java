package rmi.project;

public interface THClientInterface extends java.rmi.Remote {
    void seatsChanged(String type, int number) throws java.rmi.RemoteException;
}