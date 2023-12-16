public class Student implements Comparable<Student>{
    public String studentID;
    public String name;
    public String surname;
    public String address;
    public String phoneNumber;

    public Student(String studentID, String name, String surname, String address, String phoneNumber) {
        this.studentID = studentID;
        this.name = name;
        this.surname = surname;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return studentID + "\t" + name + " " + surname + "\t" + phoneNumber + "\t" + address;
    }

    @Override
    public int compareTo(Student otherStudent) {
        return Integer.compare(Integer.parseInt(this.studentID), Integer.parseInt(otherStudent.studentID));
    }
}
