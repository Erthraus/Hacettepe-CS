import java.io.*;
import java.util.*;

class TrieNode {
    Map<Character, TrieNode> children;
    List<long[]> entries;

    public TrieNode() {
        children = new HashMap<>();
        entries = new ArrayList<>();
    }
}

public class Quiz4 {
    private TrieNode root;
    private List<String> allWords;

    public Quiz4() {
        root = new TrieNode();
        allWords = new ArrayList<>();
    }

    public void insert(String word, long weight) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        int index = allWords.size();
        allWords.add(word);
        node.entries.add(new long[]{weight, index});
    }

    public List<long[]> searchEntries(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) {
                return new ArrayList<>();
            }
            node = node.children.get(c);
        }

        List<long[]> combined = new ArrayList<>();
        gatherEntries(node, combined);
        combined.sort((a, b) -> Long.compare(b[0], a[0]));
        return combined;
    }

    private void gatherEntries(TrieNode node, List<long[]> combined) {
        if (node == null) return;
        combined.addAll(node.entries);
        for (TrieNode child : node.children.values()) {
            gatherEntries(child, combined);
        }
    }

    public static void main(String[] args) {
        String databaseFile = args[0];
        String queryFile = args[1];

        Quiz4 quiz = new Quiz4();
        quiz.loadDatabase(databaseFile);
        quiz.processQueries(queryFile);
    }

    public void loadDatabase(String databaseFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(databaseFile))) {
            int numResults = Integer.parseInt(br.readLine().trim());
            for (int i = 0; i < numResults; i++) {
                String[] parts = br.readLine().trim().split("\t");
                long weight = Long.parseLong(parts[0]);
                String result = parts[1].toLowerCase();
                insert(result, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void processQueries(String queryFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(queryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split("\t");
                String query = parts[0].toLowerCase();
                int limit = Integer.parseInt(parts[1]);
                List<long[]> entries = searchEntries(query);

                System.out.println("Query received: \"" + query + "\" with limit " + limit + ". Showing results:");
                if (entries.isEmpty() || limit == 0) {
                    System.out.println("No results.");
                } else {
                    for (int i = 0; i < entries.size() && i < limit; i++) {
                        long[] entry = entries.get(i);
                        System.out.println("- " + entry[0] + " " + allWords.get((int) entry[1]));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
