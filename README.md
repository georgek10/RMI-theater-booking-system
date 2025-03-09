# RMI-theater-booking-system
A Java-based application that allows clients to reserve and cancel seats at different sections of a theater.
It utilizes Remote Method Invocation (RMI) for communication between clients and the server.
- Clients send commands to the server
- Server executes the commands and stores the data

# Setup
Make sure to have JDK 8+ installed. You can run this project through an IDE or two separate terminals.
- IDE: Run the project with the THServer file set as the main class. Afterwards run the THClient file one or many times in different terminals.
- Terminals: You need two active terminals. Keep all the source code files in the same path location.
  1. In any terminal compile all files with 'javac *.java'
  2. Compile THImpl.java with 'rmic THImpl' to generate the stub file (might not be needed in newer Java versions)
  3. In the first terminal run THServer with 'java THServer'
  4. In the second terminal run THClient with 'java THClient'

# Features
The theater has the following 5 sections which are linked to a unique code, ticket price and initial available seat number:
- Stalls/Zone A - code: SA - price: 45€ - 100 seats
- Stalls/Zone B - code: SB - price: 35€ - 200 seats
- Stalls/Zone C - code: SC - price: 25€ - 400 seats
- Main Balcony  - code: MB - price: 30€ - 225 seats
- Balcony Boxes - code: BB - price: 20€ - 75 seats

Clients can:
- Get a list of available seats remaining for each section with other general information
- Make different reservations of a number of seats in a section of the theater of their choosing
- Get a list of guests and their reservation info
- Cancel their reservations or a number of reserved seats
- Subscribe to a list to get notified when unavailable requested seats become available

# Usage
After running the THClient file the following commands can be entered:
1. list `<hostname>`, to display the list of available seats
2. book `<hostname>` `<type>` `<number>` `<name>`, where `<type>` is for the code of the section, `<number>` for the amount of seats to book, `<name>` for the name of the customer
3. guests `<hostname>`, to display the list of guests with their reservations
4. cancel `<hostname>` `<type>` `<number>` `<name>`, where `<type>` is for the code of the section, `<number>` for the amount of seats to cancel, `<name>` for the name of the customer
- Type **Yes** when asked to subscribe to the notification list  
**Note**: Replace <hostname> with **localhost**
