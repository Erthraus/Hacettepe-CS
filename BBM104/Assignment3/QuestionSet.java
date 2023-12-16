public class QuestionSet extends TaskDecorator{
    public QuestionSet(Task task) {
        super(task);
    }

    @Override
    public int fee() {
        return 7 + super.fee();
    }

    @Override
    public String toString() {
        return super.toString() + " QuestionSet";
    }
}
