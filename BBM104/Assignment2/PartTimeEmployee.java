public class PartTimeEmployee extends Employee{
    public static final int MIN_WORK_HOUR = 10;
    public static final int MAX_WORK_HOUR = 20;
    public static final double HOURLY_FEE = 18;

    public PartTimeEmployee(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
    }

    @Override
    public void calculateTotalSalary() {
        double tempSalary = 0;
        for(int i = 0; i < 4; i++) {
            if(workingHours[i] > MAX_WORK_HOUR) tempSalary += MAX_WORK_HOUR * HOURLY_FEE;
            else if(workingHours[i] >= MIN_WORK_HOUR) tempSalary += workingHours[i] * HOURLY_FEE;
        }

        totalSalary = tempSalary + severancePay;
    }
}
