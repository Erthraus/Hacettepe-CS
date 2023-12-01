import java.util.Comparator;

public class LastNameComparator implements Comparator<Contact> {
    @Override
    public int compare(Contact c1, Contact c2) {
        int i = 0;

        while (i < c1.getLastName().length() && i < c2.getLastName().length()) {
            char c1Letter = Character.toLowerCase(c1.getLastName().charAt(i));
            char c2Letter = Character.toLowerCase(c2.getLastName().charAt(i));

            int lastNameCompare = Character.compare(c1Letter, c2Letter);

            if (lastNameCompare != 0) {
                return lastNameCompare;
            }

            i++;
        }

        return Integer.compare(c1.getLastName().length(), c2.getLastName().length());
    }
}
