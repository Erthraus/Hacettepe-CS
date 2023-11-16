public class Team {
    private String name;
    private int score;
    private int rank;
    private int numberOfPlayedMatches;
    private int numberOfWin;
    private int numberOfTie;
    private int numberOfLose;
    private int numberOfSetsFor;
    private int numberOfSetsAgainst;

    public Team() {
        score = 0;
        rank = 0;
        numberOfPlayedMatches = 0;
        numberOfWin = 0;
        numberOfLose = 0;
        numberOfSetsFor = 0;
        numberOfSetsAgainst = 0;
    }

    public Team(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }

    public void updateScore(int points) {
        score += points;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getNumberOfPlayedMatches() {
        return numberOfPlayedMatches;
    }

    public void updateNumberOfPlayedMatches() {
        numberOfPlayedMatches++;
    }

    public int getNumberOfWin() {
        return numberOfWin;
    }

    public void updateNumberOfWin() {
        numberOfWin++;
    }

    public int getNumberOfTie() {
        return numberOfTie;
    }

    public void updateNumberOfTie() {
        numberOfTie++;
    }

    public int getNumberOfLose() {
        return numberOfLose;
    }

    public void updateNumberOfLose() {
        numberOfLose++;
    }

    public int getNumberOfSetsFor() {
        return numberOfSetsFor;
    }

    public void updateNumberOfSetsFor(int points) {
        numberOfSetsFor += points;
    }

    public int getNumberOfSetsAgainst() {
        return numberOfSetsAgainst;
    }

    public void updateNumberOfSetsAgainst(int points) {
        numberOfSetsAgainst += points;
    }

    public String output() {
        return rank + ".\t" + name + "\t" + numberOfPlayedMatches + "\t" + numberOfWin + "\t" +
                numberOfTie + "\t" + numberOfLose + "\t" + numberOfSetsFor + ":" + numberOfSetsAgainst + "\t" +
                score + "\n";
    }
}
