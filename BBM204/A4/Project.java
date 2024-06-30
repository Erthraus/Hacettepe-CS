import java.io.Serializable;
import java.util.*;

public class Project implements Serializable {
    static final long serialVersionUID = 33L;
    private final String name;
    private final List<Task> tasks;

    public Project(String name, List<Task> tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    /**
     * @return the total duration of the project in days
     */
    public int getProjectDuration() {
        int projectDuration = 0;

        for (Task task : tasks) {
            projectDuration = Math.max(projectDuration, task.getDuration());
        }

        return projectDuration;
    }

    /**
     * Schedule all tasks within this project such that they will be completed as early as possible.
     *
     * @return An integer array consisting of the earliest start days for each task.
     */
    public int[] getEarliestSchedule() {
        // TODO: YOUR CODE HERE
        int[] startDays = new int[tasks.size()];
        Arrays.fill(startDays, -1);

        boolean allTasksScheduled = false;
        while (!allTasksScheduled) {
            allTasksScheduled = true;
            for (int i = 0; i < tasks.size(); i++) {
                if (startDays[i] == -1) {
                    boolean dependenciesCompleted = true;
                    for (int dependency : tasks.get(i).getDependencies()) {
                        if (startDays[dependency] == -1) {
                            dependenciesCompleted = false;
                            break;
                        }
                    }
                    if (dependenciesCompleted) {
                        startDays[i] = getTaskStartTime(tasks.get(i), startDays);
                    } else {
                        allTasksScheduled = false;
                    }
                }
            }
        }

        return startDays;
    }

    private int getTaskStartTime(Task task, int[] startDays) {
        int maxDependencyEndTime = 0;
        for (int dependency : task.getDependencies()) {
            int dependencyEndTime = startDays[dependency] + tasks.get(dependency).getDuration();
            if (dependencyEndTime > maxDependencyEndTime) {
                maxDependencyEndTime = dependencyEndTime;
            }
        }
        return maxDependencyEndTime;
    }

    public static void printlnDash(int limit, char symbol) {
        for (int i = 0; i < limit; i++) System.out.print(symbol);
        System.out.println();
    }

    /**
     * Some free code here. YAAAY! 
     */
    public void printSchedule(int[] schedule) {
        int limit = 65;
        char symbol = '-';
        printlnDash(limit, symbol);
        System.out.println(String.format("Project name: %s", name));
        printlnDash(limit, symbol);

        // Print header
        System.out.println(String.format("%-10s%-45s%-7s%-5s","Task ID","Description","Start","End"));
        printlnDash(limit, symbol);
        for (int i = 0; i < schedule.length; i++) {
            Task t = tasks.get(i);
            System.out.println(String.format("%-10d%-45s%-7d%-5d", i, t.getDescription(), schedule[i], schedule[i]+t.getDuration()));
        }
        printlnDash(limit, symbol);
        System.out.println(String.format("Project will be completed in %d days.", tasks.get(schedule.length-1).getDuration() + schedule[schedule.length-1]));
        printlnDash(limit, symbol);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;

        int equal = 0;

        for (Task otherTask : ((Project) o).tasks) {
            if (tasks.stream().anyMatch(t -> t.equals(otherTask))) {
                equal++;
            }
        }

        return name.equals(project.name) && equal == tasks.size();
    }

}