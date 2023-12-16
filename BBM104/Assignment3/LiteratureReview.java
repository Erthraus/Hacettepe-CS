public class LiteratureReview extends TaskDecorator{
    public LiteratureReview(Task task) {
        super(task);
    }

    @Override
    public int fee() {
        return 15 + super.fee();
    }

    @Override
    public String toString() {
        return super.toString() + " LiteratureReview";
    }
}
