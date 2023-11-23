public class Security extends Personnel{
    public static final double TRANS_MONEY = 5;
    public static final double FOOD_MONEY = 10;
    public static final int MAX_WORK_HOUR = 9;
    public static final int MIN_WORK_HOUR = 5;
    public static final double HOURLY_FEE = 10;
    public static final int DAY_OF_WORK = 6;

    public Security(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
    }

    @Override
    protected void calculateTotalSalary() {
        double tempSalary = 0;
        for(int i = 0; i < 4; i++) {
            if(workingHours[i] > MAX_WORK_HOUR * DAY_OF_WORK) tempSalary += MAX_WORK_HOUR * DAY_OF_WORK * HOURLY_FEE;
            else if(workingHours[i] >= MIN_WORK_HOUR * DAY_OF_WORK) tempSalary += workingHours[i] * HOURLY_FEE;
        }

        totalSalary = severancePay + tempSalary + DAY_OF_WORK * 4 * (FOOD_MONEY + TRANS_MONEY);
    }
}
