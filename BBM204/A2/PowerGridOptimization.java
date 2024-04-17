import java.util.ArrayList;

/**
 * This class accomplishes Mission POWER GRID OPTIMIZATION
 */
public class PowerGridOptimization {
    private ArrayList<Integer> amountOfEnergyDemandsArrivingPerHour;

    public PowerGridOptimization(ArrayList<Integer> amountOfEnergyDemandsArrivingPerHour){
        this.amountOfEnergyDemandsArrivingPerHour = amountOfEnergyDemandsArrivingPerHour;
    }

    public ArrayList<Integer> getAmountOfEnergyDemandsArrivingPerHour() {
        return amountOfEnergyDemandsArrivingPerHour;
    }
    /**
     *     Function to implement the given dynamic programming algorithm
     *     SOL(0) <- 0
     *     HOURS(0) <- [ ]
     *     For{j <- 1...N}
     *         SOL(j) <- max_{0<=i<j} [ (SOL(i) + min[ E(j), P(j − i) ] ]
     *         HOURS(j) <- [HOURS(i), j]
     *     EndFor
     *
     * @return OptimalPowerGridSolution
     */
    public OptimalPowerGridSolution getOptimalPowerGridSolutionDP(){
        int n = amountOfEnergyDemandsArrivingPerHour.size();
        int[] SOL = new int[n + 1];
        SOL[0] = 0;
        ArrayList<ArrayList<Integer>> HOURS = new ArrayList<>();
        HOURS.add(new ArrayList<>());

        for (int j = 1; j <= n; j++) {
            int maxSOL = Integer.MIN_VALUE;
            int optimalHour = -1;
            for (int i = 0; i < j; i++) {
                int candidateSOL = SOL[i] + Math.min(amountOfEnergyDemandsArrivingPerHour.get(j - 1), (j-i)*(j-i));
                if (candidateSOL > maxSOL) {
                    maxSOL = candidateSOL;
                    optimalHour = i;
                }
            }
            SOL[j] = maxSOL;
            ArrayList<Integer> optimalHours = new ArrayList<>(HOURS.get(optimalHour));
            optimalHours.add(j);
            HOURS.add(optimalHours);
        }

        return new OptimalPowerGridSolution(SOL[n], HOURS.get(n));
    }
}
