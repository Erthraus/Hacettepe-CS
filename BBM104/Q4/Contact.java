public class Contact implements Comparable<Contact> {
    private String socialSecurityNumber;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public Contact(String socialSecurityNumber, String firstName, String lastName, String phoneNumber) {
        this.socialSecurityNumber = socialSecurityNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void output() {
        System.out.println(phoneNumber + " " + firstName + " " + lastName + " " + socialSecurityNumber);
    }

    @Override
    public String toString() {
        return phoneNumber + " " + firstName + " " + lastName + " " + socialSecurityNumber + "\n";
    }


    @Override
    public int compareTo(Contact c) {
        return this.getPhoneNumber().compareTo(c.getPhoneNumber());
    }
}