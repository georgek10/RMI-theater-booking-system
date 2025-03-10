import java.rmi.Naming;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.util.Scanner;

public class THClient extends java.rmi.server.UnicastRemoteObject implements THClientInterface {
    
    public THClient() throws java.rmi.RemoteException {
        super();
    }
    
    public static void main(String[] args) {
        try {
            // lookup the RMI registry to get the remote object
            THInterface th = (THInterface) Naming.lookup("rmi://localhost/THInterfaceService");

            System.out.println("Client has successfully connected to the Server.\n");

            // initialize scanner to read user inputs
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Enter the following commands:\n"
                + "list <hostname>\n"
                + "book <hostname> <type> <number> <name>\n"
                + "guests <hostname>\n"
                + "cancel <hostname> <type> <number> <name>\n"
                + "exit\n");
            
            // loop to handle user commands
            while (true) {
                // scan user input
                String[] command = scanner.nextLine().split(" ");

                // case: list <hostname>
                if (command[0].equals("list") && command[1].equals("localhost")) {
                    // call method to print list of the theatre's available seats
                    String info = th.list();
                    System.out.println("\n" + info);
                    
                // case: book <hostname> <type> <number> <name>
                } else if (command[0].equals("book") && command[1].equals("localhost") && command.length == 5) {
                    String type = command[2];
                    int number = Integer.parseInt(command[3]);
                    String name = command[4];
                    
                    // parse parameters to call seat booking method
                    String result = th.book(type, number, name);
                    System.out.println("\n" + result);
                    
                    // ask user to subscribe to notification list in case of no available seats
                    if (result.equals("There are no seats available for this type")) {
                        System.out.println("Do you want to be added to the notification list in case your requested seats become available?\n(Yes/No)");
                        String answer = scanner.nextLine();
                        
                        // add user to notification list in case they accept
                        if (answer.equals("Yes")) {
                            THClient client = new THClient();
                            th.addSubscriber(client, type);
                            System.out.println("\nSuccessfully subscribed to the notification list");
                        }
                    }
                    
                // case: guests <hostname>
                } else if (command[0].equals("guests") && command[1].equals("localhost")) {
                    // call method to print the theatre's customers and their booking information
                    String result = th.guests();
                    System.out.println("\n" + result);
                    
                // case: cancel <hostname> <type> <number> <name>
                } else if (command[0].equals("cancel") && command[1].equals("localhost") && command.length == 5) {
                    String type = command[2];
                    int number = Integer.parseInt(command[3]);
                    String name = command[4];
                    // call method to cancel a booking
                    String result = th.cancel(type, number, name);
                    System.out.println("\n" + result);
                    
                // case: exit
                } else if (command[0].equalsIgnoreCase("exit")) {
                    System.out.println("Client exiting...");
                    break;
                    
                // do nothing on empty commands
                } else if (command.length == 1) {
                    
                // any other command case
                } else {
                    System.out.println("Unknown command. Enter the following commands:\n"
                        + "list <hostname>\n"
                        + "book <hostname> <type> <number> <name>\n"
                        + "guests <hostname>\n"
                        + "cancel <hostname> <type> <number> <name>\n"
                        + "exit\n");
                    continue;
		}
                
                System.out.println("\nEnter the following commands:\n"
                    + "list <hostname>\n"
                    + "book <hostname> <type> <number> <name>\n"
                    + "guests <hostname>\n"
                    + "cancel <hostname> <type> <number> <name>\n"
                    + "exit\n");
            }
            
            // close scanner
            scanner.close();
            
        } catch (MalformedURLException murle) {
            System.out.println("MalformedURLException: " + murle);
        } catch (RemoteException re) {
            System.out.println("RemoteException: " + re);
        } catch (NotBoundException nbe) {
            System.out.println("NotBoundException: " + nbe);
        } catch (java.lang.ArithmeticException ae) {
            System.out.println("java.lang.ArithmeticException: " + ae);
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }
	
    @Override
    // client notification message in case of cancellation of a reservation for the type of seats requested and not available
    public void seatsChanged(String type, int number) throws java.rmi.RemoteException {
        System.out.println("Notification: Seats for " + type + " have changed. Available: " + number + "\n");
    }
}
