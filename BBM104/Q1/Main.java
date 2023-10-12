import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Bus bus = new Bus("06 HUBM 06", 42);
        Scanner scanner = new Scanner(System.in);

        while(true) {
            System.out.println("Menu");
            System.out.println("1-Book a seat");
            System.out.println("2-Cancel booking");
            System.out.println("3-Print all passengers");
            System.out.println("4-Print available seats");
            System.out.println("5-Print all seats");
            System.out.println("6-Search passenger");
            System.out.println("7-Exit");

            System.out.print("Select an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    int seatID;
                    while(true) {
                        try {
                            System.out.print("Enter seat number: ");
                            seatID = scanner.nextInt();
                            break;
                        } catch(InputMismatchException e) {
                            System.out.println("Invalid input. Please enter a valid integer.");
                        } finally {
                            scanner.nextLine();
                        }
                    }

                    if(bus.getSeat(seatID) == null) {
                        System.out.println("Invalid seat number.");
                    } else if(!bus.getSeat(seatID).isAvailable()) {
                        System.out.println("Seat " + seatID + " is already booked.");
                    } else {
                        System.out.print("Enter passenger name: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter passenger surname: ");
                        String surname = scanner.nextLine();
                        System.out.print("Enter passenger gender: ");
                        String gender = scanner.nextLine();
                        System.out.print("Enter passenger phone country code: ");
                        String countrycode = scanner.nextLine();
                        System.out.print("Enter passenger phone code: ");
                        String code = scanner.nextLine();
                        
                        String number;
                        while(true) {
                            System.out.print("Enter passenger phone number: ");
                            number = scanner.nextLine();
                            if(number.length() == 7) break;
                            else System.out.println("Phone number must be 7 digits.");
                        }
                        
                        String phoneType;
                        while(true) {
                            System.out.print("Enter passenger phone type (home, office, mobile): ");
                            phoneType = scanner.nextLine();
                            if(phoneType.equalsIgnoreCase("home") || phoneType.equalsIgnoreCase("office") || phoneType.equalsIgnoreCase("mobile")) break;
                            else System.out.println("Wrong input. Try again");
                        }
                        Phone phone = new Phone(countrycode, code, number, phoneType);
                        Passenger passenger = new Passenger(name, surname, gender, phone);

                        bus.bookSeat(passenger, seatID);
                    }
                    break;

                case 2:
                    System.out.print("Enter seat number to cancel booking: ");
                    int cancelSeatID = scanner.nextInt();
                    scanner.nextLine();
                    bus.cancelBooking(cancelSeatID);
                    break;

                case 3:
                    bus.printAllPassengers();
                    break;

                case 4:
                    bus.printAllAvailableSeatIDs();
                    break;

                case 5:
                    System.out.println("All seats on bus " + bus.getSeats() + ":");
                    for(Seat seat : bus.getSeats()) {
                        seat.display();
                    }
                    break;

                case 6:
                    System.out.print("Enter passenger name to search: ");
                    String searchName = scanner.nextLine();
                    System.out.print("Enter passenger surname to search: ");
                    String searchSurname = scanner.nextLine();
                    Passenger foundPassenger = bus.search(searchName, searchSurname);
                    if (foundPassenger != null) {
                        foundPassenger.display();
                    } else {
                        System.out.println("Passenger not found.");
                    }
                    break;

                case 7:
                    System.out.println("Exiting the program.");
                    scanner.close();
                    System.exit(0);

                default:
                    System.out.println("Invalid choice. Please select a valid option.");
            }
        }
    }
}