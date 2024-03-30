import java.util.*;
import java.io.*;

public class Quiz2 {
    public static void main(String[] args) throws IOException {
        
        // TODO: Use the first command line argument (args[0]) as the file to read the input from
        Scanner scanner = new Scanner(new File(args[0]));

        int M = scanner.nextInt();
        int n = scanner.nextInt();

        int[] resources = new int[n];
        for (int i = 0; i < n; i++) {
            resources[i] = scanner.nextInt();
        }
        scanner.close();

        // TODO: Your code goes here
        boolean[][] dp = new boolean[M + 1][n + 1];

        for (int i = 0; i <= M; i++) {
            for (int j = 0; j <= n; j++) {
                dp[i][j] = L(i, j, resources, dp);
            }
        }

        int maxMass = 0;
        for (int i = M; i >= 0; i--) {
            if (dp[i][n]) {
                maxMass = i;
                break;
            }
        }

        // TODO: Print the solution to STDOUT
        System.out.println(maxMass);
        
        for (int i = 0; i <= M; i++) {
            for (int j = 0; j <= n; j++) {
                System.out.print(dp[i][j] ? 1 : 0);
            }
            System.out.println();
        }
    }
    
    public static boolean L(int m, int i, int[] resources, boolean[][] dp) {
        if (i == 0 && m == 0) {
            return true;
        } else if (i == 0 && m > 0) {
            return false;
        } else if (i > 0 && resources[i - 1] > m) {
            return dp[m][i - 1];
        } else {
            return dp[m][i - 1] || dp[m - resources[i - 1]][i - 1];
        }
    }
}
