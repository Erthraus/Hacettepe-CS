import java.util.*;
import java.io.*;

public class Quiz1 {
    public static void main(String[] args) throws IOException {
        Locale.setDefault(Locale.ENGLISH);
        String delimiter = "...";
        String[] lines = readFile(args[0]);
        List<String> wordsToIgnore = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        List<String> keywordList = new ArrayList<>();

        boolean delimiterFound = false;
        for(String line : lines) {
            if(line.equalsIgnoreCase(delimiter)) {
                delimiterFound = true;
                continue;
            }

            if(delimiterFound) titles.add(line.toLowerCase());
            else wordsToIgnore.add(line.toLowerCase());
        }

        for(String title : titles) {
            String[] parts = title.split("\\s+");
            for (String part : parts) {
                if(!wordsToIgnore.contains(part) && !keywordList.contains(part)) keywordList.add(part);
            }
        }


        Collections.sort(keywordList);

        for (String keyword : keywordList) {
            for (String title : titles) {
                int ctr = 0;
                String[] parts = title.split("\\s+");
                for (String part : parts) {
                    if(part.equalsIgnoreCase(keyword)) output(parts, keyword, ++ctr);
                }
            }
        }
    }

    public static String[] readFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return lines.toArray(new String[0]);
    }

    public static void output(String[] str, String keyword, int ctr) {
        for (int i = 0; i < str.length; i++) {
            if(str[i].equalsIgnoreCase(keyword)) {
                if(--ctr == 0) System.out.print(str[i].toUpperCase());
                else System.out.print(str[i]);
            } else System.out.print(str[i]);

            if(i+1 < str.length) System.out.print(" ");
        }

        System.out.print("\n");
        
}
}