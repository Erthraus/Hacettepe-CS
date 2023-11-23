public class FullTimeEmployee extends Employee{
    public static int DAY_OF_WORK = 5;
    protected double dailyFee;

    public FullTimeEmployee(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
    }

    @Override
    protected void calculateTotalSalary() {
        calculateExtraWorkSalary();
        totalSalary = DAY_OF_WORK * 4 * dailyFee + severancePay + overWorkSalary;
    }
}
