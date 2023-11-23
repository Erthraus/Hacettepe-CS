public abstract class Academician extends Personnel{
    public Academician(String name, String surname, String registerNumber, String position, int yearOfStart) {
        super(name, surname, registerNumber, position, yearOfStart);
    }

    protected abstract void calculateTotalSalary();
}
