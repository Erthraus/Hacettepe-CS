public class Seat {
    private int seatID;
    private boolean status;
    private Passenger passenger;

    public Seat(int seatID, boolean status, Passenger passenger) {
        this.seatID = seatID;
        this.status = status;
        this.passenger = passenger;
    }

    public int getSeatID() {
        return seatID;
    }

    public void setSeatID(int seatID) {
        this.seatID = seatID;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return !status;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public void bookSeat(Passenger passenger) {
        this.status = true;
        this.passenger = passenger;
    }

    public void cancelBooking() {
        this.status = false;
        this.passenger = null;
    }

    public void display() {
        System.out.println("Seat ID: " + seatID);
        if(status) {
            System.out.println("Status: Booked");
            passenger.display();
        } else {
            System.out.println("Status: Available");
        }
    }
}