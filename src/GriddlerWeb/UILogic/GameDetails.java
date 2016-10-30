package GriddlerWeb.UILogic;

public class GameDetails
{
    private String currPlayer;
    private int currRound;
    private int totalRounds;
    private int cuurMove;
    private String gameTitle;
    private String winnerName;
    private boolean technicalVictory;
    private boolean finishAllRound;

    public GameDetails(String currPlayer, int currRound, int totalRounds, int cuurMove, String gameTitle, String winnerName, boolean technicalVictory, boolean finishAllRound) {
        this.currPlayer = currPlayer;
        this.currRound = currRound;
        this.totalRounds = totalRounds;
        this.cuurMove = cuurMove;
        this.gameTitle = gameTitle;
        this.winnerName = winnerName;
        this.technicalVictory = technicalVictory;
        this.finishAllRound = finishAllRound;
    }
}
