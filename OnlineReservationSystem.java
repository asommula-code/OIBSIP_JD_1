import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

class Passenger {
    String name;
    int age;
    String trainNo;
    String trainName;
    String from;
    String to;
    String doj;
    String bookingDate;
    String pnr;
    String status;
    double refund;

    Passenger(String name, int age, String trainNo, String trainName,
              String from, String to, String doj,
              String bookingDate, String pnr,
              String status, double refund) {

        this.name = name;
        this.age = age;
        this.trainNo = trainNo;
        this.trainName = trainName;
        this.from = from;
        this.to = to;
        this.doj = doj;
        this.bookingDate = bookingDate;
        this.pnr = pnr;
        this.status = status;
        this.refund = refund;
    }
}

public class OnlineReservationSystem {

    static Scanner sc = new Scanner(System.in);

    static HashMap<String, String> users = new HashMap<>();
    static HashMap<String, Passenger> tickets = new HashMap<>();
    static HashMap<String, Integer> seats = new HashMap<>();
    static HashMap<String, String> trains = new HashMap<>();

    static final String FILE_NAME = "bookings.txt";
    static final double TICKET_PRICE = 500;
    static final double REFUND_PERCENT = 0.8;

    public static void main(String[] args) {

        // USERS
        users.put("admin", "admin123");
        users.put("user1", "pass1");

        // TRAINS (MORE ADDED)
        trains.put("101", "Chennai Express");
        trains.put("102", "Hyderabad Intercity");
        trains.put("103", "Vijayawada Superfast");
        trains.put("104", "Tirupati Express");
        trains.put("105", "Delhi Rajdhani");
        trains.put("106", "Bangalore Mail");

        // SEATS
        for (String t : trains.keySet()) {
            seats.put(t, 5);
        }

        loadFromFile();

        System.out.println("===== ONLINE RESERVATION SYSTEM =====");

        if (login()) {
            menu();
        } else {
            System.out.println("Invalid login!");
        }
    }

    // LOGIN
    static boolean login() {
        System.out.print("User ID: ");
        String id = sc.next();

        System.out.print("Password: ");
        String pass = sc.next();

        return users.containsKey(id) && users.get(id).equals(pass);
    }

    // MENU
    static void menu() {
        while (true) {

            System.out.println("\n===== MENU =====");
            System.out.println("1. Reservation");
            System.out.println("2. Cancellation");
            System.out.println("3. Admin Panel");
            System.out.println("4. Print Ticket (PNR)");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1 -> reservation();
                case 2 -> cancellation();
                case 3 -> adminLogin();
                case 4 -> printTicketByPNR();
                case 5 -> {
                    saveToFile();
                    System.out.println("Exited Successfully!");
                    return;
                }
                default -> System.out.println("Invalid Choice");
            }
        }
    }

    // RESERVATION
    static void reservation() {

        System.out.println("\nAVAILABLE TRAINS:");
        for (String t : trains.keySet()) {
            System.out.println(t + " -> " + trains.get(t) +
                    " | Seats: " + seats.get(t));
        }

        System.out.print("\nEnter Train No: ");
        String trainNo = sc.next();

        if (!trains.containsKey(trainNo)) {
            System.out.println("Invalid Train!");
            return;
        }

        if (seats.get(trainNo) <= 0) {
            System.out.println("No seats available!");
            return;
        }

        System.out.print("Name: ");
        String name = sc.next();

        System.out.print("Age: ");
        int age = sc.nextInt();

        System.out.print("From: ");
        String from = sc.next();

        System.out.print("To: ");
        String to = sc.next();

        System.out.print("Date of Journey: ");
        String doj = sc.next();

        String bookingDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date());

        String pnr = "PNR" + new Random().nextInt(99999);

        Passenger p = new Passenger(
                name, age, trainNo,
                trains.get(trainNo),
                from, to, doj,
                bookingDate, pnr,
                "BOOKED",
                0
        );

        tickets.put(pnr, p);
        seats.put(trainNo, seats.get(trainNo) - 1);

        saveToFile();

        System.out.println("\nBooking Successful!");
        System.out.println("PNR: " + pnr);
    }

    // CANCELLATION
    static void cancellation() {

        System.out.print("Enter PNR: ");
        String pnr = sc.next();

        if (!tickets.containsKey(pnr)) {
            System.out.println("Invalid PNR!");
            return;
        }

        Passenger p = tickets.get(pnr);

        if (p.status.equals("CANCELLED")) {
            System.out.println("Already Cancelled!");
            return;
        }

        double refundAmount = TICKET_PRICE * REFUND_PERCENT;

        System.out.println("\nTicket Found:");
        printTicket(p);

        System.out.println("\nRefund Eligible: Rs." + refundAmount);

        System.out.print("Confirm cancellation? (yes/no): ");
        String c = sc.next();

        if (c.equalsIgnoreCase("yes")) {

            p.status = "CANCELLED";
            p.refund = refundAmount;

            seats.put(p.trainNo, seats.get(p.trainNo) + 1);

            saveToFile();

            System.out.println("Cancelled Successfully!");
            System.out.println("Refund of Rs." + refundAmount + " will be processed.");
        }
    }

    // ADMIN LOGIN (NEW SECURITY FEATURE)
    static void adminLogin() {

        System.out.print("Admin ID: ");
        String id = sc.next();

        System.out.print("Admin Password: ");
        String pass = sc.next();

        if (id.equals("admin") && pass.equals("admin123")) {
            adminPanel();
        } else {
            System.out.println("Access Denied!");
        }
    }

    // ADMIN PANEL
    static void adminPanel() {

        System.out.println("\n===== ADMIN PANEL =====");

        System.out.println("\n--- ACTIVE BOOKINGS ---");
        for (Passenger p : tickets.values()) {
            if (p.status.equals("BOOKED")) {
                printTicket(p);
            }
        }

        System.out.println("\n--- CANCELLED BOOKINGS ---");
        for (Passenger p : tickets.values()) {
            if (p.status.equals("CANCELLED")) {
                printTicket(p);
                System.out.println("Refund Paid: Rs." + p.refund);
            }
        }
    }

    // TICKET PRINT FORMAT (REALISTIC)
    static void printTicket(Passenger p) {

        System.out.println("\n==============================");
        System.out.println("      INDIAN RAILWAYS TICKET");
        System.out.println("==============================");
        System.out.println("PNR            : " + p.pnr);
        System.out.println("Name           : " + p.name);
        System.out.println("Age            : " + p.age);
        System.out.println("Train          : " + p.trainName + " (" + p.trainNo + ")");
        System.out.println("From           : " + p.from);
        System.out.println("To             : " + p.to);
        System.out.println("Journey Date   : " + p.doj);
        System.out.println("Booked On      : " + p.bookingDate);
        System.out.println("Status         : " + p.status);
        System.out.println("==============================");
    }

    // PRINT TICKET BY PNR
    static void printTicketByPNR() {

        System.out.print("Enter PNR: ");
        String pnr = sc.next();

        if (tickets.containsKey(pnr)) {
            printTicket(tickets.get(pnr));
        } else {
            System.out.println("Ticket not found!");
        }
    }

    // SAVE
    static void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {

            for (Passenger p : tickets.values()) {
                bw.write(p.pnr + "," + p.name + "," + p.age + "," +
                        p.trainNo + "," + p.trainName + "," +
                        p.from + "," + p.to + "," +
                        p.doj + "," + p.bookingDate + "," +
                        p.status + "," + p.refund);
                bw.newLine();
            }

        } catch (Exception e) {
            System.out.println("File Error");
        }
    }

    // LOAD
    static void loadFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {

            String line;

            while ((line = br.readLine()) != null) {

                String[] d = line.split(",");

                Passenger p = new Passenger(
                        d[1], Integer.parseInt(d[2]),
                        d[3], d[4],
                        d[5], d[6],
                        d[7], d[8],
                        d[0],
                        d[9],
                        Double.parseDouble(d[10])
                );

                tickets.put(p.pnr, p);

                if (p.status.equals("BOOKED") && seats.containsKey(p.trainNo)) {
                    seats.put(p.trainNo, seats.get(p.trainNo) - 1);
                }
            }

        } catch (Exception e) {
            // first run safe
        }
    }
}