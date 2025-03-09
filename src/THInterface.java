package rmi.project;

public interface THInterface extends java.rmi.Remote { 
    public String list() throws java.rmi.RemoteException;
	
    public String book(String type, int number, String name) throws java.rmi.RemoteException;
	
    public String guests() throws java.rmi.RemoteException;
	
    public String cancel(String type, int number, String name) throws java.rmi.RemoteException;
	
    public void notifySubscribers(String type) throws java.rmi.RemoteException;
    
    public void addSubscriber(THClientInterface subscriber, String type) throws java.rmi.RemoteException;
    
    public void removeSubscriber(THClientInterface subscriber, String type) throws java.rmi.RemoteException;
}