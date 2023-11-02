public class Member extends Person{
    private double weight;
    private double height;

    public Member(int id, String name, String surname, double weight, double height) {
        super(id, name, surname);
        this.weight = weight;
        this.height = height;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    private double bmi() {
        return weight / Math.pow(height, 2);
    }

    public String weightStatus() {
        double bmi = bmi();
        if(bmi < 18.5) return "Thin";
        else if(bmi < 25) return "Normal";
        else if(bmi < 30) return "Fat";
        else return "Obese";
    }
}
