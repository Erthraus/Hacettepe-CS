public class Bus {
    private String plate;
    private int seatCount;
    private Seat[] seats;

    public Bus(String plate, int seatCount) {
        this.plate = plate;
        this.seatCount = seatCount;
        this.seats = new Seat[seatCount];
        for(int i = 0; i < seatCount; i++) {
            seats[i] = new Seat(i + 1, false, null);
        }
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public int getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(int seatCount) {
        this.seatCount = seatCount;
    }

    public Seat[] getSeats() {
        return seats;
    }

    public void setSeats(Seat[] seats) {
        this.seats = seats;
    }

    public Seat getSeat(int seatID) {
        if(seatID > 0 && seatID <= seatCount) {
            return seats[seatID - 1];
        } else {
            return null;
        }
    }

    public void bookSeat(Passenger p, int seatID) {
        Seat seat = getSeat(seatID);
        if(seat != null) {
            if(seat.isAvailable()) {
                seat.bookSeat(p);
                System.out.println("Seat " + seatID + " booked for passenger:");
                p.display();
            } else {
                System.out.println("Seat " + seatID + " is already booked.");
            }
        } else {
            System.out.println("Invalid seat number.");
        }
    }

    public void cancelBooking(int seatID) {
        Seat seat = getSeat(seatID);
        if(seat != null) {
            if(seat.isAvailable()) {
                System.out.println("Seat " + seatID + " is already available.");
            } else {
                seat.cancelBooking();
                System.out.println("Booking for seat " + seatID + " canceled.");
            }
        } else {
            System.out.println("Invalid seat number.");
        }
    }

    public void printAllPassengers() {
        System.out.println("Passengers on bus " + plate + ":");
        for(Seat seat : seats) {
            if(!seat.isAvailable()) {
                System.out.println("Seat: " + seat.getSeatID() + " Status: Reserved");
                seat.getPassenger().display();
                System.out.println("---------");
            }
        }
    }

    public void printAllAvailableSeatIDs() {
        System.out.println("Available seats on bus " + plate + ":");
        for(Seat seat : seats) {
            if(seat.isAvailable()) {
                System.out.println("Seat " + seat.getSeatID());
            }
        }
    }

    public Passenger search(String name, String surname) {
        for(Seat seat : seats) {
            if(!seat.isAvailable()) {
                if(seat.getPassenger().getName().equalsIgnoreCase(name) && seat.getPassenger().getSurname().equalsIgnoreCase(surname)) {
                    System.out.println("Seat: " + seat.getSeatID() + " Status: Reserved");
                    return seat.getPassenger();
                }
            }
        }
        return null;
    }
}