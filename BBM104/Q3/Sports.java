import java.util.List;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;

public class Sports {
    protected List<Team> teams = new ArrayList<>();

    protected void match(Team[] matchTeams, int[] clubPoints) {
        for(int i = 0; i < 2; i++) {
            matchTeams[i].updateNumberOfPlayedMatches();
            matchTeams[i].updateNumberOfSetsFor(clubPoints[i]);
            matchTeams[i].updateNumberOfSetsAgainst(clubPoints[1-i]);
        }

        if(clubPoints[0] > clubPoints[1]) {
            win(matchTeams[0]);
            lose(matchTeams[1]);
        } else if(clubPoints[0] < clubPoints[1]) {
            win(matchTeams[1]);
            lose(matchTeams[0]);
        } else {
            for(Team team : matchTeams) tie(team);
        }
    }

    protected void readData(String path) {
        String[] lines = Main.readFile(path);

        for(String line : lines) {
            String[] parts = line.split("\t");
            String sportType = parts[0];
            String[] clubNames = {parts[1], parts[2]};
            String[] result = parts[3].split(":");
            int[] clubPoints = {Integer.parseInt(result[0]), Integer.parseInt(result[1])};
            Team[] matchTeams = new Team[2];

            for(int i = 0; i < 2; i++) {
                boolean found = false;
                for (Team team : teams) {
                    if (team.getName().equalsIgnoreCase(clubNames[i])) {
                        matchTeams[i] = team;
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    Team team = new Team(clubNames[i]);
                    teams.add(team);
                    matchTeams[i] = team;
                }
            }

            match(matchTeams, clubPoints);
        }
    }

    protected void win(Team team) {
        team.updateScore(1);
        team.updateNumberOfWin();
    }

    protected void tie(Team team) {
        team.updateNumberOfTie();
    }

    protected void lose(Team team) {
        team.updateNumberOfLose();
    }

    protected void rankTeams() {
        teams.sort(new TeamComparator());
        int i = 0;

        for (Team team : teams) {
            team.setRank(++i);
            /*System.out.println("Rank: " + team.getRank() + "Name : " + team.getName() +
                    ", Score: " + team.getScore() +
                    ", Points For: " + team.getNumberOfSetsFor() +
                    ", Points Against: " + team.getNumberOfSetsAgainst());*/
        }
    }

    protected void output() {
        try {
            FileWriter writer = new FileWriter("output.txt");

            for(Team team : teams) {
                writer.write(team.output());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
