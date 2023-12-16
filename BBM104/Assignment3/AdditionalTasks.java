public class AdditionalTasks extends TaskDecorator{
    public AdditionalTasks(Task task) {
        super(task);
    }

    @Override
    public int fee() {
        return 5 + super.fee();
    }

    @Override
    public String toString() {
        return super.toString() + " AdditionalTasks";
    }
}
