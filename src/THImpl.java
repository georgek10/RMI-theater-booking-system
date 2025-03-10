import java.util.ArrayList;
import java.util.Iterator;

public class THImpl extends java.rmi.server.UnicastRemoteObject implements THInterface {

    seats s = new seats();
    bookings b = new bookings();
    ArrayList<THClientInterface> subsa = new ArrayList<>();
    ArrayList<THClientInterface> subsb = new ArrayList<>();
    ArrayList<THClientInterface> subsc = new ArrayList<>();
    ArrayList<THClientInterface> submb = new ArrayList<>();
    ArrayList<THClientInterface> subbb = new ArrayList<>();

    public THImpl() throws java.rmi.RemoteException {
        super();
    }

    // return method of the list of available seats in the theatre for each section and their cost
    @Override
    public String list() throws java.rmi.RemoteException {
        String info = "";
        info += seats.sa + " seats available in Stalls - Zone A (code: SA) - price: 45€\n";
        info += seats.sb + " seats available in Stalls - Zone B (code: SB) - price: 35€\n";
        info += seats.sc + " seats available in Stalls - Zone C (code: SC) - price: 25€\n";
        info += seats.mb + " seats available in Main Balcony (code: MB) - price: 30€\n";
        info += seats.bb + " seats available in Balcony Boxes (code: BB) - price: 20€\n";
        return info;
    }

    // booking method of <number> seat(s) of code <type> in name <name>
    @Override
    public String book(String type, int number, String name) throws java.rmi.RemoteException {
        String result;
        int check, price;
        // convert to uppercase for case-insensitivity
        String normalizedType = type.toUpperCase();
        
        check = s.bookSeats(normalizedType, number);
        // successful booking
        if (check == 1) {
            b.book(normalizedType, number, name);
            price = number * s.seatPrice(normalizedType);
            if (number == 1) {
                result = "A reservation has been made for " + number + " seat of type " + normalizedType + " under the name " + name + " with a total cost of " + price + "€";
            } else {
                result = "A reservation has been made for " + number + " seats of type " + normalizedType + " under the name " + name + " with a total cost of " + price + "€";
            }
        // unsuccessful booking
        } else {
            // case of less available seats than amount requested and inform customer
            if (s.availableSeats(normalizedType) > 0) {
                if (s.availableSeats(normalizedType) == 1) {
                    result = "The requested seats are not available, but there is still 1 seat available for reservation";
                } else {
                    result = "The requested seats are not available, but there are still " + s.availableSeats(normalizedType) + " seats available for reservation";
                }
            // case of lack of available seats
            } else {
                result = "There are no seats available for this type";
            }
        }
        return result;
    }

    // method that returns the list of guests who have made a reservation and information about their bookings
    @Override
    public String guests() throws java.rmi.RemoteException {
        String result;
        result = b.printGuests();
        return result;
    }

    // method to cancel a reservation
    @Override
    public String cancel(String type, int number, String name) throws java.rmi.RemoteException {
        String result;
        // convert to uppercase for case-insensitivity
        String normalizedType = type.toUpperCase();
        
        result = b.cancel(normalizedType, number, name);
        notifySubscribers(normalizedType);
        return result;
    }

    // notification method to handle all seat types dynamically
    @Override
    public void notifySubscribers(String type) throws java.rmi.RemoteException {
        // retrieve list based on type
        ArrayList<THClientInterface> subscribersList = getSubscribersList(type);
        for (THClientInterface subscriber : subscribersList) {
            new Thread(() -> {
                try {
                    // notify the subscriber about the available seats and remove him from the list
                    subscriber.seatsChanged(type, s.availableSeats(type));
                    removeSubscriber(subscriber, type);
                } catch (java.rmi.RemoteException e) {
                    // remove the subscriber if notification fails
                    try {
                        removeSubscriber(subscriber, type); 
                    } catch (java.rmi.RemoteException e2) {
                        System.out.println("Error removing subscriber: " + e2.getMessage());
                    }
                }
            }).start();
        }
    }

    // method to retrieve the correct list of subscribers based on seat type
    public ArrayList<THClientInterface> getSubscribersList(String type) {
        switch (type) {
            case "SA": return subsa;
            case "SB": return subsb;
            case "SC": return subsc;
            case "MB": return submb;
            case "BB": return subbb;
            default: return new ArrayList<>();
        }
    }
    
    // subscriber add method
    @Override
    public void addSubscriber(THClientInterface subscriber, String type) throws java.rmi.RemoteException {
        ArrayList<THClientInterface> list = getSubscribersList(type);
        list.add(subscriber); // add the subscriber from the list
    }

    // subscriber removal method
    @Override
    public void removeSubscriber(THClientInterface subscriber, String type) throws java.rmi.RemoteException {
        ArrayList<THClientInterface> list = getSubscribersList(type);
        list.remove(subscriber); // remove the subscriber from the list
    }
}

class seats {
    static int sa = 100, sb = 200, sc = 400, mb = 225, bb = 75;
    static int sap = 45, sbp = 35, scp = 25, mbp = 30, bbp = 20;

    // method for seat reservation if there are available seats, depending on how many the user requested for booking
    public int bookSeats(String type, int number) {
        if (number <= availableSeats(type) && availableSeats(type)-number >= 0) {
            updateSeats(type, number);
            return 1;
        } else {
            return 0;
        }
    }

    // return method of the current available seats in the theatre 
    public int availableSeats(String type) {
        if (type.equals("SA")) {
            return sa;
        } else if (type.equals("SB")) {
            return sb;
        } else if (type.equals("SC")) {
            return sc;
        } else if (type.equals("MB")) {
            return mb;
        } else if (type.equals("BB")) {
            return bb;
        }
        
        return 0;
    }

    // method to decrease the number of available seats in case of a reservation
    public void updateSeats(String type, int number) {
        if (type.equals("SA")) {
            sa = availableSeats(type) - number;
        } else if (type.equals("SB")) {
            sb = availableSeats(type) - number;
        } else if (type.equals("SC")) {
            sc = availableSeats(type) - number;
        } else if (type.equals("MB")) {
            mb = availableSeats(type) - number;
        } else if (type.equals("BB")) {
            bb = availableSeats(type) - number;
        }
    }

    // method to increase the number of available seats in case of a cancellation
    public void canceledSeats(String type, int number) {
        if (type.equals("SA")) {
            sa = availableSeats(type) + number;
        } else if (type.equals("SB")) {
            sb = availableSeats(type) + number;
        } else if (type.equals("SC")) {
            sc = availableSeats(type) + number;
        } else if (type.equals("MB")) {
            mb = availableSeats(type) + number;
        } else if (type.equals("BB")) {
            bb = availableSeats(type) + number;
        }
    }

    // return method of a price of a seat based on its type 
    public int seatPrice(String type) {
        if (type.equals("SA")) {
            return sap;
        } else if (type.equals("SB")) {
            return sbp;
        } else if (type.equals("SC")) {
            return scp;
        } else if (type.equals("MB")) {
            return mbp;
        } else if (type.equals("BB")) {
            return bbp;
        }
        
        return 0;
    }
}

class bookings {
    
    String type, name;
    int number, price;
    seats s = new seats();
    ArrayList<bookings> bookingList = new ArrayList<>();
	
    // save method of a customer's booking info
    public void book(String type, int number, String name) {
        bookings newBooking = new bookings();
        newBooking.type = type;
        newBooking.number = number;
        newBooking.name = name;
        newBooking.price = number * s.seatPrice(type);

        // check if the customer already has a booking of the same type
        for (bookings booking : bookingList) {
            if (booking.name.equals(newBooking.name) && booking.type.equals(newBooking.type)) {
                booking.number += newBooking.number;
                booking.price += newBooking.price;
                return;
            }
        }
        
        // no existing booking found, add a new entry
        bookingList.add(newBooking);
    }
	
    // return method of the list of customer's with a reservation and their booking info 
    public String printGuests() {
        String result = "", guestresults;
        // case of empty booking list
        if (bookingList.isEmpty()) {
            result = "There are no registered reservations";
        } else {
            ArrayList<String> guests = new ArrayList<>();
            ArrayList<String> results = new ArrayList<>();

            for (bookings booking : bookingList) {
                String guest = booking.name;
                int number = booking.number;
                String type = booking.type;
                int price = booking.price;
                int guestIndex = guests.indexOf(guest);

                // add customers and their booking info to the list
                if (guestIndex == -1) {
                    guests.add(guest);
                    if (number == 1) {
                        guestresults = "Customer " + guest + " totally has:\n" + number + " reservation of type " + type + " with a total cost of " + price + "€\n";
                    } else {
                        guestresults = "Customer " + guest + " totally has:\n" + number + " reservations of type " + type + " with a total cost of " + price + "€\n";
                    }
                    results.add(guestresults);
                    
                // add other booking info of customers to the list
                } else {
                    if (number == 1) {
                        guestresults = results.get(guestIndex) + number + " reservation of type " + type + " with a total cost of " + price + "€\n";
                    } else {
                        guestresults = results.get(guestIndex) + number + " reservations of type " + type + " with a total cost of " + price + "€\n";
                    }
                    results.set(guestIndex, guestresults);
                }
            }

            // save the list to return it
            if (guests.size() == 1) {
                guestresults = results.get(0);
                result += guestresults;
            } else {
                for (int i = 0; i < guests.size(); i++) {
                    guestresults = results.get(i) + "\n";
                    result += guestresults;
                }
            }
        }
        return result;
    }
    
    // method to cancel a customer's booking
    public String cancel(String type, int number, String name) throws java.rmi.RemoteException {
        seats s = new seats();
        String result, guest = "";
        boolean foundb = false, foundbs = false;
        Iterator<bookings> iterator = bookingList.iterator();

        // remove the booking from the list if found and update available number of seats
        while (iterator.hasNext()) {
            bookings booking = iterator.next();
            if (type.equals(booking.type) && name.equals(booking.name)) {
                // partial cancellation, deduct number of seats
                if (number < booking.number) {
                     booking.number -= number;
                    booking.price -= (number * s.seatPrice(type));
                    foundb = true;
                    s.canceledSeats(type, number);
                    guest = booking.name;
                    break;
                }
                // full cancelation of booking
                else if (number == booking.number) {
                    iterator.remove();
                    foundb = true;
                    s.canceledSeats(type, number);
                    guest = booking.name;
                    break;
                }
                else
                    result = "You have " + booking.number + " seat(s) reserved";
                    return result;
            }
        }

        result = "Cancellation of the reservation was successful and customer " + guest + " still has:\n";
        for (bookings booking : bookingList) {
            if (guest.equals(booking.name)) {
                foundbs = true;
                if (booking.number == 1) {
                    result += booking.number + " reservation of type " + booking.type + " with a total cost of " + booking.price + "€\n";
                } else {
                    result += booking.number + " reservations of type " + booking.type + " with a total cost of " + booking.price + "€\n";
                }
            }
        }

        if (foundb && foundbs) {
            return result;
        } else if (foundb) {
            result = "Cancellation of the reservation was successful and no other reservations have been found for this particular customer";
            return result;
        } else {
            result = "The particular reservation was not found";
            return result;
        }
    }
}
