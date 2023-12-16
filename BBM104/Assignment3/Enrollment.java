import java.util.ArrayList;
import java.util.List;

public class Enrollment {
    public String enrollmentID;
    public String studentID;

    public List<Assessment> assessments = new ArrayList<>();

    public Enrollment(String enrollmentID, String studentID) {
        this.enrollmentID = enrollmentID;
        this.studentID = studentID;
    }

    public void addAssessment(Assessment assessment) {
        assessments.add(assessment);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for(Assessment assessment : assessments) {
            stringBuilder.append(assessment.toString()).append(System.lineSeparator());
        }

        String result = stringBuilder.toString();

        return enrollmentID + "\t" + studentID + "\n" + result;
    }
}
