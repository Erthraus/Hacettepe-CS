public class EssayBasedAssessment extends Assessment{
    @Override
    public int fee() {
        return 10 + task.fee();
    }

    @Override
    public String toString() {
        return  "Essaybased\t" + task.toString();
    }
}
