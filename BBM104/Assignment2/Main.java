import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Personnel> personnels = readPersonnels(args[0]);
        readWorkHours(personnels, args[1]);
        for(Personnel p : personnels) {
            p.calculateTotalSalary();
            p.fileOutput();
        }
    }

    //Function to read the list of the personnels from the file.
    public static List<Personnel> readPersonnels(String path) {
        String[] lines = Main.readFile(path);
        List<Personnel> personnels = new ArrayList<>();

        for(String line : lines) {
            String[] parts = line.split("\t");
            String[] temp = parts[0].split(" ");
            String name = temp[0];
            String surname = temp[1];
            String registerNumber = parts[1];
            String position = parts[2];
            int yearOfStart = Integer.parseInt(parts[3]);

            if(position.equalsIgnoreCase("FACULTY_MEMBER")) {
                FacultyMember personnel = new FacultyMember(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            } else if(position.equalsIgnoreCase("WORKER")) {
                Worker personnel = new Worker(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            } else if(position.equalsIgnoreCase("SECURITY")) {
                Security personnel = new Security(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            } else if(position.equalsIgnoreCase("OFFICER")) {
                Officer personnel = new Officer(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            } else if(position.equalsIgnoreCase("CHİEF")) {
                Chief personnel = new Chief(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            } else if(position.equalsIgnoreCase("PARTTIME_EMPLOYEE")) {
                PartTimeEmployee personnel = new PartTimeEmployee(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            } else if(position.equalsIgnoreCase("RESEARCH_ASSISTANT")) {
                ResearchAssistant personnel = new ResearchAssistant(name, surname, registerNumber, position, yearOfStart);
                personnels.add(personnel);
            }
        }

        return personnels;
    }

    //Function to read and match weekly working hours of the personnels from the file.
    public static void readWorkHours(List<Personnel> personnels, String path) {
        String[] lines = Main.readFile(path);

        for(String line : lines) {
            String[] parts = line.split("\t");
            String registerNumber = parts[0];
            int[] workHours = {Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4])};

            for(Personnel p : personnels) {
                if(p.getRegisterNumber().equalsIgnoreCase(registerNumber)) {
                    p.setWorkingHours(workHours);
                    break;
                }
            }
        }
    }

    public static String[] readFile(String path) {
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
}
