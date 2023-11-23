public class FacultyMember extends Academician{
    public FacultyMember(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
        ssBenefits = BASE_SALARY * 1.35;
        hourlyExtraWorkingFee = 20;
        maxExtraWorkingHours = 8;
    }

    @Override
    protected void calculateTotalSalary() {
        calculateExtraWorkSalary();

        totalSalary = BASE_SALARY + ssBenefits + severancePay + overWorkSalary;
    }
}
