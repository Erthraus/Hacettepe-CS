public class Analysis extends TaskDecorator{
    public Analysis(Task task) {
        super(task);
    }

    @Override
    public int fee() {
        return 10 + super.fee();
    }

    @Override
    public String toString() {
        return super.toString() + " Analysis";
    }
}
