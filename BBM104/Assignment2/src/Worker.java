public class Worker extends FullTimeEmployee{
    public Worker(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
        dailyFee = 105;
        maxExtraWorkingHours = 10;
        hourlyExtraWorkingFee = 11;
    }
}
