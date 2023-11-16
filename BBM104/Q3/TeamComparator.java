import java.util.Comparator;

public class TeamComparator implements Comparator<Team> {
    @Override
    public int compare(Team team1, Team team2) {

        int scoreComparison = Integer.compare(team2.getScore(), team1.getScore());

        if (scoreComparison != 0) {
            return scoreComparison;
        }


        int goalDifferenceComparison = Integer.compare(
                team2.getNumberOfSetsFor() - team2.getNumberOfSetsAgainst(),
                team1.getNumberOfSetsFor() - team1.getNumberOfSetsAgainst());

        return goalDifferenceComparison;
    }
}
