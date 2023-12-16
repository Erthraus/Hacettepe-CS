public class TaskDecorator implements Task{
    protected final Task task;

    public TaskDecorator(Task task) {
        this.task = task;
    }

    @Override
    public int fee() {
        return task.fee();
    }

    @Override
    public String toString() {
        return task.toString();
    }
}
