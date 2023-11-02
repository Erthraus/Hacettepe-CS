public class PersonalTrainer extends Person{
    public Member[] members;
    public String sportType;

    public PersonalTrainer(int id, String name, String surname, String sportType) {
        super(id, name, surname);
        this.sportType = sportType;
        members = new Member[0];
    }

    public int returnCountofMembers() {
        return members.length;
    }

    public void addMember(Member m) {
        Member[] temp = new Member[members.length + 1];
        for(int i = 0; i < members.length; i++) {
            temp[i] = members[i];
        }
        temp[members.length] = m;
        members = temp;
    }

    public Member returnMember(int memberID) {
        for(Member m : members) {
            if(m.Id != memberID) continue;
            return m;
        }

        return null;
    }

    public Member ReturnFattestMember() {
        Member fattest = members[0];

        for(Member m : members) {
            if(m.getWeight() > fattest.getWeight()) fattest = m;
        }

        return fattest;
    }
}
