import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Integer> decimalNumbers = new ArrayList<>();
        String inputPath = args[0];
        String[] lines = IOManager.readInputFile(inputPath);
        IOManager.clearFile(IOManager.outputPath);

        for(String line : lines) {
            decimalNumbers.add(Integer.parseInt(line));
        }

        for(int decimalNum : decimalNumbers) {
            int num = decimalToOctal(decimalNum);
            IOManager.writeToFile(String.valueOf(num), IOManager.outputPath);
        }
    }

    public static int decimalToOctal(int number) {
        Stack stack = new Stack();

        do{
            stack.push(number % 8);
            number /= 8;
        }while(number > 0);

        int octalResult = 0;
        int size = stack.size();
        for(int i = 0; i < size; i++) {
            octalResult += (int) (stack.pop() * Math.pow(10, size - (i + 1)));
        }

        return octalResult;
    }
}
