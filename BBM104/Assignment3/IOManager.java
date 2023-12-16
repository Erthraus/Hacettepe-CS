import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class IOManager {
    StudentDAO studentDAO;
    EnrollmentDAO enrollmentDAO;
    String inputFilePath;
    String outputFilePath = "output.txt";
    List<String> completedOperations = new ArrayList<>();

    public IOManager(StudentDAO studentDAO, EnrollmentDAO enrollmentDAO, String inputFilePath) {
        this.studentDAO = studentDAO;
        this.enrollmentDAO = enrollmentDAO;
        this.inputFilePath = inputFilePath;
    }

    public void run() {
        String[] lines = readInputFile(inputFilePath);
        for(String line : lines) executeCommand(line);
        writeToFile();
    }

    private String[] readInputFile(String path) {
        try{
            int i = 0;
            Path path1 = Paths.get(path);
            int length = Files.readAllLines(path1).size();
            String[] results = new String[length];
            for (String line : Files.readAllLines(path1)) {
                results[i++] = line;
            }
            return results;
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void executeCommand(String line) {
        String[] parts = line.split(" ");
        switch(parts[0]) {
            case "AddStudent":
                String[] addressParts = new String[parts.length - 4];
                addressParts[0] = "Address:";
                System.arraycopy(parts, 5, addressParts, 1, addressParts.length - 1);
                addStudent(parts[1], parts[2], parts[3], parts[4], String.join(" ", addressParts));
                break;
            case "RemoveStudent":
                removeStudent(parts[1]);
                break;
            case "CreateEnrollment":
                createEnrollment(parts[1], parts[2]);
                break;
            case "AddAssessment":
                String[] tasks = new String[parts.length - 3];
                System.arraycopy(parts, 3, tasks, 0, parts.length - 3);
                addAssessment(parts[1], parts[2], tasks);
                break;
            case "TotalFee":
                totalFee(parts[1]);
                break;
            case "ListStudents":
                listStudents();
                break;
        }
    }

    private void addStudent(String studentID, String name, String surname, String phoneNumber, String address) {
        Student student = new Student(studentID, name, surname, address, phoneNumber);
        studentDAO.add(student);
        completedOperations.add("Student " + studentID + " " + name + " added");
    }

    private void removeStudent(String ID) {
        Student student = studentDAO.deleteByID(Integer.parseInt(ID));
        completedOperations.add("Student " + student.studentID + " " + student.name + " removed");
    }

    private void createEnrollment(String enrollmentID, String studentID) {
        Enrollment enrollment = new Enrollment(enrollmentID, studentID);
        enrollmentDAO.add(enrollment);
        completedOperations.add("CourseEnrollment " + enrollmentID + " created");
    }

    private void addAssessment(String enrollmentID, String assessmentType, String[] tasks) {
        Task task = EnrollmentDAO.createTask(tasks);
        Assessment assessment;
        if(assessmentType.equals("Essaybased")) assessment = new EssayBasedAssessment();
        else if(assessmentType.equals("MultipleChoice")) assessment = new MultipleChoiceAssessment();
        else return;
        assessment.addTask(task);
        Enrollment enrollment = enrollmentDAO.getByID(Integer.parseInt(enrollmentID));
        enrollment.addAssessment(assessment);
        enrollmentDAO.add(enrollment);
        completedOperations.add(assessmentType + " assessment added to enrollment " + enrollmentID);
    }

    private void totalFee(String enrollmentID) {
        int total = 0;
        Enrollment enrollment = enrollmentDAO.getByID(Integer.parseInt(enrollmentID));
        int[] fees = new int[enrollment.assessments.size()];
        int i = 0;
        completedOperations.add("TotalFee for enrollment " + enrollmentID);
        for(Assessment assessment : enrollment.assessments) {
            fees[i] = assessment.fee();
            total += fees[i];
            completedOperations.add("\t" + assessment + " " + fees[i] + "$");
        }
        completedOperations.add("\tTotal: " + total + "$");
    }

    private void listStudents() {
        ArrayList<Student> students = studentDAO.getAll();
        sortStudentsByName(students);
        completedOperations.add("Student List:");
        for(Student student : students) {
            completedOperations.add(student.toString());
        }
    }

    private void writeToFile() {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath))) {
            for(String line : completedOperations) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sortStudentsByName(ArrayList<Student> students) {
        students.sort(new Comparator<Student>() {
            @Override
            public int compare(Student student1, Student student2) {
                return student1.name.compareTo(student2.name);
            }
        });
    }
}
