import java.io.*;
import java.util.ArrayList;

public class EnrollmentDAO implements DAO<Enrollment>{
    private ArrayList<Enrollment> enrollments = new ArrayList<>();

    public EnrollmentDAO() {
        try (BufferedReader reader = new BufferedReader(new FileReader(enrollmentPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if(isNumeric(parts[0])) {
                    Enrollment enrollment = new Enrollment(parts[0], parts[1]);
                    enrollments.add(enrollment);
                } else {
                    Assessment assessment;
                    if(parts[0].equals("MultipleChoice")) {
                        assessment = new MultipleChoiceAssessment();
                    } else if (parts[0].equals("Essaybased")) {
                        assessment = new EssayBasedAssessment();
                    } else {
                        continue;
                    }
                    String[] parts2 = parts[1].split(" ");
                    Task task = createTask(parts2);

                    assessment.addTask(task);
                    enrollments.get(enrollments.size() - 1).addAssessment(assessment);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static Task createTask(String[] parts) {
        Task task = new SimpleTask();
        for(String part : parts) {
            switch (part) {
                case "Analysis":
                    task = new Analysis(task);
                    break;

                case "AdditionalTasks":
                    task = new AdditionalTasks(task);
                    break;

                case "QuestionSet":
                    task = new QuestionSet(task);
                    break;

                case "LiteratureReview":
                    task = new LiteratureReview(task);
                    break;
            }
        }
        return task;
    }

    @Override
    public Enrollment getByID(int ID) {
        for(Enrollment enrollment : enrollments) {
            if(Integer.parseInt(enrollment.enrollmentID) == ID) return enrollment;
        }
        return null;
    }

    @Override
    public Enrollment deleteByID(int ID) {
        for(Enrollment enrollment: enrollments) {
            if(Integer.parseInt(enrollment.enrollmentID) == ID) {
                enrollments.remove(enrollment);
                writeToFile();

                return enrollment;
            }
        }

        return null;
    }

    @Override
    public void add(Enrollment enrollment) {
        for(Enrollment enrollment1 : enrollments) {
            if(enrollment1.enrollmentID.equals(enrollment.enrollmentID)) {
                enrollments.set(enrollments.indexOf(enrollment1), enrollment);
                writeToFile();
                return;
            }
        }

        enrollments.add(enrollment);
        writeToFile();
    }

    @Override
    public ArrayList<Enrollment> getAll() {
        return enrollments;
    }

    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void writeToFile() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(enrollmentPath))) {
            for(Enrollment enrollment : enrollments) {
                writer.write(enrollment.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
