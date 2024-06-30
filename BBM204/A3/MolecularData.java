import java.util.*;

// Class representing molecular data
public class MolecularData {

    // Private fields
    private final List<Molecule> molecules; // List of molecules

    // Constructor
    public MolecularData(List<Molecule> molecules) {
        this.molecules = molecules;
    }

    // Getter for molecules
    public List<Molecule> getMolecules() {
        return molecules;
    }

    // Method to identify molecular structures
    // Return the list of different molecular structures identified from the input data
    public List<MolecularStructure> identifyMolecularStructures() {
        List<MolecularStructure> structures = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        for (Molecule molecule : molecules) {
            if (!visited.contains(molecule.getId())) {
                boolean found = false;
                for (String id : molecule.getBonds()) {
                    if(visited.contains(id)) {
                        for (MolecularStructure structure : structures) {
                            if(structure.hasMolecule(id)) {
                                structure.addMolecule(molecule);
                                break;
                            }
                        }
                        
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    MolecularStructure structure = new MolecularStructure();
                    dfs(molecule, structure, visited);
                    structures.add(structure);
                }
            }
        }

        return structures;
    }

    // DFS traversal to identify molecular structures
    private void dfs(Molecule molecule, MolecularStructure structure, Set<String> visited) {
        visited.add(molecule.getId());
        structure.addMolecule(molecule);

        for (String id : molecule.getBonds()) {
            for (Molecule bondedMolecule : molecules) {
                if (bondedMolecule.getId().equals(id) && !visited.contains(id)) {
                    dfs(bondedMolecule, structure, visited);
                }
            }
        }
    }             

    // Method to print given molecular structures
    public void printMolecularStructures(List<MolecularStructure> molecularStructures, String species) {
        
        /* YOUR CODE HERE */
        System.out.println(molecularStructures.size() + " molecular structures have been discovered in " + species + ".");
        for (int i = 0; i < molecularStructures.size(); i++) {
            System.out.println("Molecules in Molecular Structure " + (i+1) + ": " + molecularStructures.get(i));
        } 
    }

    // Method to identify anomalies given a source and target molecular structure
    // Returns a list of molecular structures unique to the targetStructure only
    public static ArrayList<MolecularStructure> getVitalesAnomaly(List<MolecularStructure> sourceStructures, List<MolecularStructure> targeStructures) {
        ArrayList<MolecularStructure> anomalyList = new ArrayList<>(targeStructures);
        
        /* YOUR CODE HERE */ 
        anomalyList.removeAll(sourceStructures);

        return anomalyList;
    }

    // Method to print Vitales anomalies
    public void printVitalesAnomaly(List<MolecularStructure> molecularStructures) {

        /* YOUR CODE HERE */
        System.out.println("Molecular structures unique to Vitales individuals:");
        for (MolecularStructure structure : molecularStructures) {
            System.out.println(structure);
        }
    }

    // toString method to represent the MolecularData object as a string
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MolecularData:\n");
        for (Molecule molecule : molecules) {
            sb.append(molecule.toString()).append("\n");
        }
        return sb.toString();
    }
}
