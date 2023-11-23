public class Chief extends FullTimeEmployee{
    public Chief(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
        dailyFee = 125;
        maxExtraWorkingHours = 8;
        hourlyExtraWorkingFee = 15;
    }
}
