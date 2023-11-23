public class Officer extends Personnel{
    public Officer(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
        ssBenefits = BASE_SALARY * 0.65;
        maxExtraWorkingHours = 10;
        hourlyExtraWorkingFee = 20;
    }

    @Override
    protected void calculateTotalSalary() {
        calculateExtraWorkSalary();
        totalSalary = BASE_SALARY + ssBenefits + severancePay + overWorkSalary;
    }
}
