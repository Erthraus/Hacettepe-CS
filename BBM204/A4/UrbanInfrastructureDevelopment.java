import java.io.File;
import java.io.Serializable;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UrbanInfrastructureDevelopment implements Serializable {
    static final long serialVersionUID = 88L;

    /**
     * Given a list of Project objects, prints the schedule of each of them.
     * Uses getEarliestSchedule() and printSchedule() methods of the current project to print its schedule.
     * @param projectList a list of Project objects
     */
    public void printSchedule(List<Project> projectList) {
        // TODO: YOUR CODE 
        for (Project project : projectList) {
            project.printSchedule(project.getEarliestSchedule());
        }
    }

    /**
     * TODO: Parse the input XML file and return a list of Project objects
     *
     * @param filename the input XML file
     * @return a list of Project objects
     */
    public List<Project> readXML(String filename) {
        List<Project> projectList = new ArrayList<>();
        // TODO: YOUR CODE HERE

        try {
            File inputFile = new File(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList projectNodes = doc.getElementsByTagName("Project");

            for (int i = 0; i < projectNodes.getLength(); i++) {
                Node projectNode = projectNodes.item(i);
                if (projectNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element projectElement = (Element) projectNode;
                    String projectName = projectElement.getElementsByTagName("Name").item(0).getTextContent();

                    NodeList taskNodes = projectElement.getElementsByTagName("Task");
                    List<Task> tasks = new ArrayList<>();

                    for (int j = 0; j < taskNodes.getLength(); j++) {
                        Node taskNode = taskNodes.item(j);
                        if (taskNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element taskElement = (Element) taskNode;
                            int taskID = Integer.parseInt(taskElement.getElementsByTagName("TaskID").item(0).getTextContent());
                            String description = taskElement.getElementsByTagName("Description").item(0).getTextContent();
                            int duration = Integer.parseInt(taskElement.getElementsByTagName("Duration").item(0).getTextContent());

                            NodeList dependencyNodes = taskElement.getElementsByTagName("Dependencies");
                            List<Integer> dependencies = new ArrayList<>();

                            Node dependencyNode = dependencyNodes.item(0);
                            if (dependencyNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element dependencyElement = (Element) dependencyNode;
                                NodeList dependsOnTaskIDNodes = dependencyElement.getElementsByTagName("DependsOnTaskID");
                                
                                for (int k = 0; k < dependsOnTaskIDNodes.getLength(); k++) {
                                    Node dependsOnTaskIDNode = dependsOnTaskIDNodes.item(k);
                                    if (dependsOnTaskIDNode.getNodeType() == Node.ELEMENT_NODE) {
                                        int dependencyID = Integer.parseInt(dependsOnTaskIDNode.getTextContent());
                                        dependencies.add(dependencyID);
                                    }
                                }
                            }
                            
                            Task task = new Task(taskID, description, duration, dependencies);
                            tasks.add(task);
                        }
                    }
                    
                    Project project = new Project(projectName, tasks);
                    projectList.add(project);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return projectList;
    }
}
