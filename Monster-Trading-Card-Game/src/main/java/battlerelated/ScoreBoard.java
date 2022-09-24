package battlerelated;
import java.util.*;

public class ScoreBoard {
    private List<String> winners;
    private List<String> losers;

    public List<String> getWinnerList() {
        return this.winners;
    }
    public List<String> getLoserList() {
        return this.losers;
    }
    ScoreBoard(){}

    public void addWinnerAndLoser(String winner, String loser) {
        this.winners.add(winner);
        this.losers.add(loser);
    }
}

