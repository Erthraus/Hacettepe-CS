public class SportCenter {
    private String name;
    public PersonalTrainer[] PTs;

    public SportCenter(String name) {
        this.name = name;
        PTs = new PersonalTrainer[0];
    }

    public void addPT(PersonalTrainer pt) {
        PersonalTrainer[] temp = new PersonalTrainer[PTs.length + 1];
        System.arraycopy(PTs, 0, temp, 0, PTs.length);
        temp[PTs.length] = pt;
        PTs = temp;
    }

    public PersonalTrainer searchPT(String name, String surname) {
        for(PersonalTrainer pt : PTs) {
            if(!pt.name.equalsIgnoreCase(name)) continue;
            if(!pt.surname.equalsIgnoreCase(surname)) continue;
            return pt;
        }

        return null;
    }
}
