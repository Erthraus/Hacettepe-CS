import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Main class
 */
// FREE CODE HERE
public class Main {
    public static void main(String[] args) throws IOException {

       /** MISSION POWER GRID OPTIMIZATION BELOW **/

        System.out.println("##MISSION POWER GRID OPTIMIZATION##");
        // TODO: Your code goes here
        // You are expected to read the file given as the first command-line argument to read 
        // the energy demands arriving per hour. Then, use this data to instantiate a 
        // PowerGridOptimization object. You need to call getOptimalPowerGridSolutionDP() method
        // of your PowerGridOptimization object to get the solution, and finally print it to STDOUT.
        ArrayList<Integer> demands = new ArrayList<>(readFile(args[0]));
        PowerGridOptimization powerGridOptimization = new PowerGridOptimization(demands);
        OptimalPowerGridSolution solution = powerGridOptimization.getOptimalPowerGridSolutionDP();
        Integer totalDemand = 0;
        for(Integer demand : demands) totalDemand += demand;
        System.out.println("The total number of demanded gigawatts: " + totalDemand);
        System.out.println("Maximum number of satisfied gigawatts: " + solution.getmaxNumberOfSatisfiedDemands());
        System.out.print("Hours at which the battery bank should be discharged: ");
        int size = solution.getHoursToDischargeBatteriesForMaxEfficiency().size();
        for (int i = 0; i < size; i++) {
            System.out.print(solution.getHoursToDischargeBatteriesForMaxEfficiency().get(i));
            if (i < size - 1)  System.out.print(", ");
        }
        System.out.println();
        System.out.println("The number of unsatisfied gigawatts: " + (totalDemand - solution.getmaxNumberOfSatisfiedDemands()));
        System.out.println("##MISSION POWER GRID OPTIMIZATION COMPLETED##");

        /** MISSION ECO-MAINTENANCE BELOW **/

        System.out.println("##MISSION ECO-MAINTENANCE##");
        // TODO: Your code goes here
        // You are expected to read the file given as the second command-line argument to read
        // the number of available ESVs, the capacity of each available ESV, and the energy requirements 
        // of the maintenance tasks. Then, use this data to instantiate an OptimalESVDeploymentGP object.
        // You need to call getMinNumESVsToDeploy(int maxNumberOfAvailableESVs, int maxESVCapacity) method
        // of your OptimalESVDeploymentGP object to get the solution, and finally print it to STDOUT.
        ArrayList<Integer> nums = new ArrayList<>(readFile(args[1]));
        Integer numOfAvailableESVs = nums.get(0);
        Integer capacity = nums.get(1);
        List<Integer> demandsESV = nums.subList(2, nums.size());
        OptimalESVDeploymentGP optimalESVDeploymentGP = new OptimalESVDeploymentGP(new ArrayList<>(demandsESV));
        Integer minNumOfESVs = optimalESVDeploymentGP.getMinNumESVsToDeploy(numOfAvailableESVs, capacity);
        
        if(minNumOfESVs < 0) {
            System.out.println("Warning: Mission Eco-Maintenance Failed.");
        } else {
            System.out.println("The minimum number of ESVs to deploy: " + minNumOfESVs);
            for(int i = 0; i < minNumOfESVs; i++) {
                System.out.println("ESV " + (i+1) + " tasks: " + optimalESVDeploymentGP.getMaintenanceTasksAssignedToESVs().get(i));
            }
        }
        
        System.out.println("##MISSION ECO-MAINTENANCE COMPLETED##");
    }

    public static ArrayList<Integer> readFile(String path) {
        ArrayList<Integer> numbers = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                for (String part : parts) {
                    try {;
                        numbers.add(Integer.parseInt(part));
                    } catch (NumberFormatException e) {
                        System.err.println("Ignoring non-integer input: " + part);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return numbers;
    }
}
