import java.io.FileWriter;
import java.io.IOException;

public class IceHockey extends Sports{
    @Override
    public void win(Team team) {
        team.updateScore(3);
        team.updateNumberOfWin();
    }

    @Override
    public void tie(Team team) {
        team.updateScore(1);
        team.updateNumberOfTie();
    }

    @Override
    protected void readData(String path) {
        String[] lines = Main.readFile(path);

        for(String line : lines) {
            String[] parts = line.split("\t");
            String sportType = parts[0];
            if(sportType.equalsIgnoreCase("I")) {
                String[] clubNames = {parts[1], parts[2]};
                String[] result = parts[3].split(":");
                int[] clubPoints = {Integer.parseInt(result[0]), Integer.parseInt(result[1])};
                Team[] matchTeams = new Team[2];

                for (int i = 0; i < 2; i++) {
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
    }

    @Override
    protected void output() {
        try {
            FileWriter writer = new FileWriter("icehockey.txt");

            for(Team team : teams) {
                writer.write(team.output());
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
