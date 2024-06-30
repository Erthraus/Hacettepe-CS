// Class representing the mission of Genesis

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MissionGenesis {

    // Private fields
    private MolecularData molecularDataHuman; // Molecular data for humans
    private MolecularData molecularDataVitales; // Molecular data for Vitales

    // Getter for human molecular data
    public MolecularData getMolecularDataHuman() {
        return molecularDataHuman;
    }

    // Getter for Vitales molecular data
    public MolecularData getMolecularDataVitales() {
        return molecularDataVitales;
    }

    // Method to read XML data from the specified filename
    // This method should populate molecularDataHuman and molecularDataVitales fields once called
    public void readXML(String filename) {

        /* YOUR CODE HERE */ 
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(filename);
            document.getDocumentElement().normalize();
            
            NodeList humanMoleculeList = document.getElementsByTagName("HumanMolecularData").item(0).getChildNodes();
            molecularDataHuman = parseMolecularData(humanMoleculeList);

            NodeList vitalesMoleculeList = document.getElementsByTagName("VitalesMolecularData").item(0).getChildNodes();
            molecularDataVitales = parseMolecularData(vitalesMoleculeList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper method to parse MolecularData from NodeList
    private MolecularData parseMolecularData(NodeList moleculeList) {
        List<Molecule> molecules = new ArrayList<>();
        for (int i = 0; i < moleculeList.getLength(); i++) {
            Node moleculeNode = moleculeList.item(i);
            if (moleculeNode.getNodeType() == Node.ELEMENT_NODE) {
                Element moleculeElement = (Element) moleculeNode;
                String id = moleculeElement.getElementsByTagName("ID").item(0).getTextContent();
                int bondStrength = Integer.parseInt(moleculeElement.getElementsByTagName("BondStrength").item(0).getTextContent());
                
                NodeList bondsNodeList = moleculeElement.getElementsByTagName("MoleculeID");
                List<String> bondsList = new ArrayList<>();
                for (int j = 0; j < bondsNodeList.getLength(); j++) {
                    Node bondNode = bondsNodeList.item(j);
                    if (bondNode.getNodeType() == Node.ELEMENT_NODE) {
                        String bondId = bondNode.getTextContent().trim();
                        bondsList.add(bondId);
                    }
                }
                
                Molecule molecule = new Molecule(id, bondStrength, bondsList);            
                molecules.add(molecule);
            }
        }
        
        MolecularData molecularData = new MolecularData(molecules);
        return molecularData;
    }
}
