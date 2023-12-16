import java.util.ArrayList;

public interface DAO<T> {
    String studentPath = "student.txt";
    String enrollmentPath = "courseEnrollment.txt";

    T getByID(int ID);

    T deleteByID(int ID);

    void add(T object);

    ArrayList<T> getAll();
}
