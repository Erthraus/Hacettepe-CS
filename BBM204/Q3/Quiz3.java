import java.util.*;
import java.io.*;

public class Quiz3 {
    public static void main(String[] args) throws IOException {
        
        // TODO: Use the first command line argument (args[0]) as the file to read the input from
        BufferedReader br = new BufferedReader(new FileReader(args[0]));
        int N = Integer.parseInt(br.readLine().trim());

        // TODO: Your code goes here

        for (int t = 0; t < N; t++) {
            String[] parts = br.readLine().trim().split(" ");
            int S = Integer.parseInt(parts[0]);
            int P = Integer.parseInt(parts[1]);
            Station[] stations = new Station[P];

            for (int i = 0; i < P; i++) {
                String[] coords = br.readLine().trim().split(" ");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                stations[i] = new Station(x, y);
            }

            double[][] distances = new double[P][P];
            for (int i = 0; i < P; i++) {
                for (int j = 0; j < P; j++) {
                    distances[i][j] = calculateDistance(stations[i], stations[j]);
                }
            }

            PriorityQueue<Edge> pipelines = new PriorityQueue<>();
            for (int i = 0; i < P; i++) {
                for (int j = i + 1; j < P; j++) {
                    pipelines.offer(new Edge(i, j, distances[i][j]));
                }
            }

            UnionFind graph = new UnionFind(P);
            int droneCount = S;
            double threshold = 0;

            while (!pipelines.isEmpty() && droneCount < P) {
                Edge edge = pipelines.poll();
                int a = edge.a;
                int b = edge.b;
                
                if (graph.find(a) != graph.find(b)) {
                    graph.union(a, b);
                    threshold = edge.weight;
                    droneCount++;
                }
            }

            // TODO: Print the solution to STDOUT
            System.out.printf("%.2f%n", threshold);
        }

        br.close();
    }

    public static double calculateDistance(Station s1, Station s2) {
        return Math.sqrt(Math.pow(s1.x - s2.x, 2) + Math.pow(s1.y - s2.y, 2));
    }
}

class Station {
    int x, y;

    public Station(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Edge implements Comparable<Edge> {
    int a, b;
    double weight;

    public Edge(int a, int b, double weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge other) {
        return Double.compare(this.weight, other.weight);
    }
}

class UnionFind {
    int[] parent, rank;

    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }

    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]);
        }
        return parent[x];
    }

    public void union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
    }
}