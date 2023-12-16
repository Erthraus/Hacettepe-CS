public class MultipleChoiceAssessment extends Assessment{
    @Override
    public int fee() {
        return 15 + task.fee();
    }

    @Override
    public String toString() {
        return  "MultipleChoice\t" + task.toString();
    }
}
