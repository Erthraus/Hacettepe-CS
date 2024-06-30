import java.util.*;

// Class representing the Mission Synthesis
public class MissionSynthesis {

    // Private fields
    private final List<MolecularStructure> humanStructures; // Molecular structures for humans
    private final ArrayList<MolecularStructure> diffStructures; // Anomalies in Vitales structures compared to humans

    // Constructor
    public MissionSynthesis(List<MolecularStructure> humanStructures, ArrayList<MolecularStructure> diffStructures) {
        this.humanStructures = humanStructures;
        this.diffStructures = diffStructures;
    }

    // Method to synthesize bonds for the serum
    public List<Bond> synthesizeSerum() {
        List<Bond> serum = new ArrayList<>();

        /* YOUR CODE HERE */
        List<Molecule> weakestMolecules = new ArrayList<>();
        for (MolecularStructure humanStructure : humanStructures) {
            weakestMolecules.add(humanStructure.getMoleculeWithWeakestBondStrength());
        }
        System.out.println("Typical human molecules selected for synthesis: " + weakestMolecules);
        
        for (int i = 0; i < diffStructures.size(); i++) {
            weakestMolecules.add(diffStructures.get(i).getMoleculeWithWeakestBondStrength());
        }
        System.out.println("Vitales molecules selected for synthesis: " + weakestMolecules.subList(weakestMolecules.size() - diffStructures.size(), weakestMolecules.size()));
        
        //Prim's Algorithm
        Set<Molecule> visited = new HashSet<>();
        PriorityQueue<Bond> availableBonds = new PriorityQueue<>(Comparator.comparingDouble(Bond::getWeight));
        List<Molecule> remainingMolecules = new ArrayList<>(weakestMolecules);
        Molecule initialMolecule = weakestMolecules.get(0);
        Molecule molecule = initialMolecule;

        while(visited.size() < weakestMolecules.size() - 1) {
            visited.add(molecule);
            remainingMolecules.remove(molecule);
            
            for(Molecule currentMolecule : remainingMolecules) {
                availableBonds.add(new Bond(currentMolecule, molecule, calculateBondWeight(molecule, currentMolecule)));
            }
            
            Bond bond;
            do {
                bond = availableBonds.poll();
            } while (bond != null && visited.contains(bond.getTo()));

            if (bond != null) serum.add(bond);
            molecule = bond.getTo();
        }

        return serum;
    }

    // Method to print the synthesized bonds
    
    public void printSynthesis(List<Bond> serum) {
        System.out.println("Synthesizing the serum...");

        for (Bond bond : serum) {
            String[] moleculdeIDs = {bond.getFrom().getId(), bond.getTo().getId()};
            Arrays.sort(moleculdeIDs);
            System.out.println("Forming a bond between " + moleculdeIDs[0] + " - " + moleculdeIDs[1] +
                    " with strength " + String.format("%.2f", bond.getWeight()));
        }

        double totalStrength = serum.stream().mapToDouble(Bond::getWeight).sum();
        System.out.println("The total serum bond strength is " + String.format("%.2f", totalStrength));
    }


    public Double calculateBondWeight(Molecule a, Molecule b) {
        return (a.getBondStrength() + b.getBondStrength()) / 2.0;
    }
}
