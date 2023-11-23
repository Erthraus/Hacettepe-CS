public class ResearchAssistant extends Academician{
    public ResearchAssistant(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
        ssBenefits = BASE_SALARY * 1.05;
    }

    @Override
    protected void calculateTotalSalary() {
        totalSalary = BASE_SALARY + ssBenefits + severancePay;
    }
}
