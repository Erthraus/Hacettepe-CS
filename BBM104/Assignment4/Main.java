public class Main {
    public static void main(String[] args) {
        String commandsPath = args[0];
        Stack<Integer> stack = new Stack<>();
        Queue<Integer> queue = new Queue<>();
        Operations[] operatables = new Operations[2];

        IOManager.clearFile(IOManager.stackOutputPath);
        IOManager.clearFile(IOManager.queueOutputPath);

        String[] lines = IOManager.readInputFile(IOManager.stackInputPath);
        String[] parts = lines[0].split(" ");
        int[] values = new int[parts.length];
        for(int i = 0; i < parts.length; i++) {
            values[i] = Integer.parseInt(parts[i]);
        }
        for(int i = 0; i < parts.length; i++) {
            stack.push(values[values.length - i - 1]);
        }

        lines = IOManager.readInputFile(IOManager.queueInputPath);
        parts = lines[0].split(" ");
        for(String part : parts) {
            queue.enqueue(Integer.parseInt(part));
        }

        operatables[0] = stack;
        operatables[1] = queue;

        String[] commands = IOManager.readInputFile(commandsPath);
        for(String command : commands) {
            executeCommand(command, operatables);
        }
    }

    public static void executeCommand(String command, Operations[] operatables)  {
       String[] parts = command.split(" ");
       String path;
       int selector;
       String value = "";
       if(parts.length >= 3) value = " " + parts[2];

       if(parts[0].equals("S")) {
           selector = 0;
           path = IOManager.stackOutputPath;
           IOManager.writeToFile("After " + parts[1] + value + ":", path);
       } else if(parts[0].equals("Q")) {
           selector = 1;
           path = IOManager.queueOutputPath;
           IOManager.writeToFile("After " + parts[1] + value + ":", path);
       } else return;

       switch (parts[1]) {
            case "calculateDistance":
                int distance = operatables[selector].calculateDistance();
                IOManager.writeToFile("Total distance=" + distance, path);
                break;
            case "addOrRemove":
                operatables[selector].addOrRemoveElements(Integer.parseInt(parts[2]));
                IOManager.writeToFile(operatables[selector].toString(), path);
                break;
            case "distinctElements":
                int count = operatables[selector].distinctElements();
                IOManager.writeToFile("Total distinct element=" + count, path);
                break;
            case "reverse":
                operatables[selector].reverseElements(Integer.parseInt(parts[2]));
                IOManager.writeToFile(operatables[selector].toString(), path);
                break;
            case "removeGreater":
                operatables[selector].removeGreater(Integer.parseInt(parts[2]));
                IOManager.writeToFile(operatables[selector].toString(), path);
                break;
            case "sortElements":
                operatables[selector].sort();
                break;
       }
    }
}
