public class Main {
    public static void main(String[] args) {
        IOManager ioManager = new IOManager(new StudentDAO(), new EnrollmentDAO(), args[0]);
        ioManager.run();
    }
}
