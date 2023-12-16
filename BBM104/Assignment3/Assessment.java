public abstract class Assessment {
    protected Task task;

    public void addTask(Task task)  {
        this.task = task;
    }

    public abstract int fee();
}
