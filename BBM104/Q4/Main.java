import java.io.FileWriter;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        List<Contact> contacts = new ArrayList<>();
        Comparator<Contact> lastNameComparatorcomparator= new LastNameComparator();
        Set<Contact> contactHashSet = new HashSet<>();
        Set<Contact> contactTreeSet = new TreeSet<>();
        Set<Contact> contactTreeSetLastName = new TreeSet<>(new LastNameComparator());
        Map<String, Contact> contactMap = new HashMap<>();
        readContacts(contacts, args[0]);
        readContacts(contactHashSet, args[0]);
        readContacts(contactTreeSet, args[0]);
        readContacts(contactTreeSetLastName, args[0]);
        readContacts(contactMap, args[0]);

        fileOutput(contacts, "contactsArrayList.txt");
        fileOutput(contactHashSet, "contactsHashSet.txt");
        fileOutput(contactTreeSet, "contactsTreeSet.txt");
        fileOutput(contactTreeSetLastName, "contactsTreeSetOrderByLastName.txt");
        fileOutput(contactMap, "contactsHashMap.txt");

        Collections.sort(contacts, lastNameComparatorcomparator);
        fileOutput(contacts, "contactsArrayListOrderByLastName.txt");
    }

    public static void readContacts(List<Contact> contacts, String path) {
        List<Contact> contactList = readData(path);
        contacts.addAll(contactList);
    }

    public static void readContacts(Set<Contact> contacts, String path) {
        List<Contact> contactList = readData(path);
        contacts.addAll(contactList);
    }

    public static void readContacts(Map<String, Contact> contacts, String path) {
        String[] lines = Main.readFile(path);
        List<Contact> contactList = readData(path);

        for(Contact contact : contactList) {
            contacts.put(contact.getPhoneNumber(), contact);
        }
    }

    public static List<Contact> readData(String path) {
        String[] lines = Main.readFile(path);
        List<Contact> contacts = new ArrayList<>();

        for(String line : lines) {
            String[] parts = line.split(" ");
            String phoneNumber = parts[0];
            String name = parts[1];
            String surname = parts[2];
            String socialSecurityNumber = parts[3];

            Contact contact = new Contact(socialSecurityNumber, name, surname, phoneNumber);
            contacts.add(contact);
        }

        return contacts;
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

    public static void fileOutput(List<Contact> contacts, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);

            for(Contact contact : contacts) {
                writer.write(contact.toString());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fileOutput(Set<Contact> contacts, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);

            for(Contact contact : contacts) {
                writer.write(contact.toString());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fileOutput(Map<String, Contact> contacts, String fileName) {
        try {
            FileWriter writer = new FileWriter(fileName);

            for(String phoneNumber : contacts.keySet()) {
                Contact contact = contacts.get(phoneNumber);
                writer.write(contact.toString());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
