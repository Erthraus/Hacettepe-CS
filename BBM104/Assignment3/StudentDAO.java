import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class StudentDAO implements DAO<Student> {
    private ArrayList<Student> students = new ArrayList<>();

    public StudentDAO() {
        try (BufferedReader reader = new BufferedReader(new FileReader(studentPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                Student student = createStudent(line);
                students.add(student);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Student getByID(int ID) {
        try (BufferedReader reader = new BufferedReader(new FileReader(studentPath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");

                if(Integer.parseInt(parts[0]) == ID)
                {
                    return createStudent(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Student deleteByID(int ID) {
        for(Student student: students) {
            if(Integer.parseInt(student.studentID) == ID) {
                students.remove(student);
                writeToFile();

                return student;
            }
        }

        return null;
    }

    @Override
    public void add(Student student) {
        for(Student student1 : students) {
            if(student1.studentID.equals(student.studentID)) {
                students.set(students.indexOf(student1), student);
                writeToFile();
                return;
            }
        }

        students.add(student);
        writeToFile();
    }

    @Override
    public ArrayList<Student> getAll() {
        return students;
    }

    private Student createStudent(String line) {
        String[] parts = line.split("\t");
        String studentID = parts[0];
        String[] fullName = parts[1].split(" ");
        String name = fullName[0];
        String surname = fullName[1];
        String phoneNumber = parts[2];
        String address = parts[3];

        return new Student(studentID, name, surname, address, phoneNumber);
    }

    private void writeToFile() {
        Collections.sort(students);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(studentPath))) {
            for(Student student : students) {
                writer.write(student.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}