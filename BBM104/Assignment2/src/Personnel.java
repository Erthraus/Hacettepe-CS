import java.io.FileWriter;
import java.io.IOException;

public abstract class Personnel {
    //Constants
    protected static final int WEEKLY_WORK_HOURS = 40;
    protected static final int CURRENT_YEAR = 2023;
    protected static final double BASE_SALARY = 2600;

    //Variables
    protected String name, surname, registerNumber, position;
    protected int yearOfStart;
    protected double totalSalary, overWorkSalary, severancePay, ssBenefits;
    protected int[] workingHours;

    //Variables to Calculate Extra Working Fee
    protected int maxExtraWorkingHours;
    protected double hourlyExtraWorkingFee;

    public Personnel(String name, String surname, String registerNumber, String position, int yearOfStart) {
        this.name = name;
        this.surname = surname;
        this.registerNumber = registerNumber;
        this.position = position;
        this.yearOfStart = yearOfStart;
        severancePay = (CURRENT_YEAR - yearOfStart) * 20 * 0.8;
        overWorkSalary = 0;
    }

    public String getRegisterNumber() {
        return registerNumber;
    }

    public void setRegisterNumber(String registerNumber) {
        this.registerNumber = registerNumber;
    }

    protected void setWorkingHours(int[] workingHours) {
        this.workingHours = workingHours;
    }

    protected abstract void calculateTotalSalary(); //Method to calculate total salary.

    //Method to calculate extra work salary.
    protected void calculateExtraWorkSalary() {
        for(int i = 0; i < 4; i++) {
            int extraWorkHour = workingHours[i] - WEEKLY_WORK_HOURS;
            if(extraWorkHour > maxExtraWorkingHours) overWorkSalary += maxExtraWorkingHours * hourlyExtraWorkingFee;
            else overWorkSalary += extraWorkHour * hourlyExtraWorkingFee;
        }
    }

    //Method to output personnel data to console.
    protected void output() {
        System.out.println("Name: " + name);
        System.out.println("Surname: " + surname);
        System.out.println("registerNumber: " + registerNumber);
        System.out.println("position: " + position);
        System.out.println("yearOfStart: " + yearOfStart);
        System.out.println("WorkHours: " + workingHours[0] + " " + workingHours[1] + " " + workingHours[2] + " " + workingHours[3]);
        System.out.println("totalSalary: " + totalSalary + "\n");
    }

    //Method to output personnel data to a file.
    protected void fileOutput() {
        try {
            FileWriter writer = new FileWriter(registerNumber + ".txt");

            writer.write("Name : " + name.toUpperCase() + "\n");
            writer.write("Surname : " + surname.toUpperCase() + "\n");
            writer.write("Registration Number : " + registerNumber + "\n");
            writer.write("Position : " + position.toUpperCase() + "\n");
            writer.write("Year of Start : " + yearOfStart + "\n");
            writer.write("Total Salary : " + totalSalary + " TL\n");

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
