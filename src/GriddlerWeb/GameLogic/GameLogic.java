package GriddlerWeb.GameLogic;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameLogic
{
    public  static  final int   MOVES_TURN_SINGLE = 1 ,ROUND_START_VALUE = 1,
            MOVES_TURN_MULTI = 2, TURN_START_VALUE = 0,
            DIFFERNCE_BETWEEN_SIZE_TO_INDEX =1, MAX_ROUND_SINGLE_PLAYER = -1,
            FIRST_PLAYER = 0;
    private ArrayList<Player> m_Players;
    private int m_CurrPlayer;
    private BoardInfo  m_WinBoard;
    private BoardInfo  m_OrginalBoard;
    private int m_CurrRound;
    private int m_currMoveOfSamePlayer, m_MaxMovesPerTurn;
    private Pair<Integer,Integer> m_computerPlayed ;

    private String m_Title;
    private String m_Organizer;
    private int m_TotalPlayers;
    private int m_TotalRounds;    // "MaxMoves"
    private boolean m_ActiveGame = true;
    private String m_WinnerName = null;
    private boolean m_TechnicalVictory = false;
    private boolean m_finishAllRound = false;

    // cstor
    public GameLogic(String i_Organizer, String i_GameTitle, int i_TotalPlayers, int i_TotalRounds,
                     int i_Rows, int i_Cols, ArrayList<BlockValues>[] i_RowBlocks, ArrayList<BlockValues>[] i_ColBlocks, ArrayList<Cell> i_WinnerCells)
    {
        m_CurrPlayer = FIRST_PLAYER;
        m_currMoveOfSamePlayer = TURN_START_VALUE;
        m_CurrRound = ROUND_START_VALUE;
        m_TotalRounds = i_TotalRounds;
        m_MaxMovesPerTurn = MOVES_TURN_MULTI;

        m_TotalPlayers = i_TotalPlayers;
        m_Players = new ArrayList<>();

        m_WinBoard = new BoardInfo(i_Rows,i_Cols);
        m_WinBoard.enterValuesOfWinnerBoard(i_WinnerCells);

        m_OrginalBoard = new BoardInfo(i_Rows, i_Cols, i_RowBlocks, i_ColBlocks);
        m_Title = i_GameTitle;
        m_Organizer = i_Organizer;

        m_computerPlayed = new Pair<>(0,0);
    }

    public void insertPlayer(String i_PlyName, boolean i_isHuman)
    {
        m_Players.add(new Player(i_PlyName, m_OrginalBoard, i_isHuman));
    }


    //region Private Functions

    private float CalcCurrPlayerScore(int i_currPly)
    {
        float score;
        float correctCellsCounter = 0;

        int boardRows = m_WinBoard.getBoardHeight();
        int boardCols = m_WinBoard.getBoardWidth();

        for(int row = 0; row < boardRows; row++)
        {
            for (int col = 0; col < boardCols; col++)
            {
                if (m_WinBoard.getCellValue(row,col) == m_Players.get(i_currPly).getBoard().getCellValue(row,col)){
                    correctCellsCounter++;
                }
            }
        }
        score = ((correctCellsCounter / (boardRows*boardCols)) * 100);
        return  score;
    }


//endregion

    //region Public Function

    public void DoMoveOfCurrPlayer(GameMove i_MovesToEnter)
    {
        if(m_currMoveOfSamePlayer <= MOVES_TURN_MULTI || m_TotalRounds == MAX_ROUND_SINGLE_PLAYER ) // (MultiP || SingleP)
        {
            AddNewMoveToPlayer(i_MovesToEnter,m_CurrPlayer);
            m_currMoveOfSamePlayer++;
        }
//        if(m_currMoveOfSamePlayer == MOVES_TURN_MULTI && m_TotalRounds != MAX_ROUND_SINGLE_PLAYER){
//            PassTurn();
//        }
        if(m_Players.get(m_CurrPlayer).IsPlayerWon()){
            m_WinnerName = m_Players.get(m_CurrPlayer).getName();
        }
    }

    public ArrayList<GameMove> manageComputerMove() {
        ArrayList<GameMove> returnValues = new ArrayList<>();
        ComputerPlayer compMove = new ComputerPlayer();
        int height = m_Players.get(m_CurrPlayer).getBoard().getBoardHeight();
        int width = m_Players.get(m_CurrPlayer).getBoard().getBoardWidth();
        Pair<GameMove, Integer> currMove = compMove.calculateComputerMove(height, width);
        AddNewMoveToPlayer(currMove.getKey(), m_CurrPlayer);
        returnValues.add(currMove.getKey());
        currMove = compMove.calculateComputerMove(height, width);
        AddNewMoveToPlayer(currMove.getKey(), m_CurrPlayer);
        returnValues.add(currMove.getKey());
        return returnValues;

    }
    //        Integer currPlayer = null, amountOfMoves = 0;
    //        int demoround = m_CurrRound, demoCurrPly = m_CurrPlayer;

//        while(!m_Players.get(demoCurrPly).isHuman() && demoround <= m_TotalRounds)
//        {
//            if(currPlayer == null)
//            {
//                currPlayer = m_CurrPlayer;
//            }
//            for(int currMoveInSameTurn =0 ; currMoveInSameTurn< m_MaxMovesPerTurn ; currMoveInSameTurn++) {
//                ComputerPlayer compMove = new ComputerPlayer();
//                Pair<GameMove, Integer> currMove = compMove.calculateComputerMove(m_Players.get(demoCurrPly).getBoard().getBoardHeight(), m_Players.get(demoCurrPly).getBoard().getBoardWidth());
//                if (currMove.getKey() != null) {
//                    for (int undoNum = 0; undoNum < currMove.getValue(); undoNum++) {
//                            UndoLastMoveFromPlayer();
//                    }
//                    AddNewMoveToPlayer(currMove.getKey(),demoCurrPly);
//                    m_Players.get(demoCurrPly).setScore(CalcCurrPlayerScore(demoCurrPly));
//                }
//            }
//            amountOfMoves++;
//
//            if(demoCurrPly == m_Players.size() -DIFFERNCE_BETWEEN_SIZE_TO_INDEX )
//            {
//                demoround ++;
//            }
//            demoCurrPly = (demoCurrPly + 1)% m_Players.size();
//        }
//        m_computerPlayed=new Pair<>(currPlayer,amountOfMoves);


    private void AddNewMoveToPlayer(GameMove i_MovesToEnter ,int i_CurrPly)
    {
        m_Players.get(i_CurrPly).AddMoveUpdateBoard(i_MovesToEnter);
        m_Players.get(i_CurrPly).setScore(CalcCurrPlayerScore(m_CurrPlayer)); // calc currect player score
    }

    public GameMove UndoLastMoveFromPlayer()
    {
        GameMove returnGameMove = null;
        returnGameMove = m_Players.get(m_CurrPlayer).UndoMove();
        if(returnGameMove != null)
        {
            m_Players.get(m_CurrPlayer).setScore(CalcCurrPlayerScore(m_CurrPlayer));
            if(m_currMoveOfSamePlayer > 0) {
                m_currMoveOfSamePlayer--;
            }
        }
        return returnGameMove;
    }
    public GameMove RedoLastMoveFromPlayer()
    {
        GameMove returnGameMove = null;
        returnGameMove = m_Players.get(m_CurrPlayer).RedoMove();
        if(returnGameMove != null)
        {
            m_currMoveOfSamePlayer++;
            m_Players.get(m_CurrPlayer).setScore(CalcCurrPlayerScore(m_CurrPlayer)); // calc currect player score
        }
        return returnGameMove;
    }
    //endregion

    public void PassTurn()
    {
        boolean isComputer;
        m_CurrPlayer = (m_CurrPlayer + 1 ) % m_Players.size();
        m_currMoveOfSamePlayer = TURN_START_VALUE;
        isComputer = (!m_Players.get(m_CurrPlayer).isHuman());
        if(m_CurrPlayer == 0) //when we come back to ply number 1 we pass round
             {
            m_CurrRound ++;
            if(m_CurrRound == m_TotalRounds){
                m_finishAllRound = true;
            }
        }
        if(!m_finishAllRound && isComputer)
        {
            manageComputerMove();
            if(m_Players.get(m_CurrPlayer).IsPlayerWon()){
                m_WinnerName = m_Players.get(m_CurrPlayer).getName();
            }
            PassTurn();
        }
    }

    //region Get,Set
    public BoardInfo getCurrentBoard()
    {
        return m_Players.get(m_CurrPlayer).getBoard();
    }

    public Player getCurrentPlayer() { return m_Players.get(m_CurrPlayer); }

    public int getCurrPlayerIndex() {return m_CurrPlayer;}

    public ArrayList<Player> getPlayers(){ return m_Players;}

    public float getScore()
    {
        return m_Players.get(m_CurrPlayer).getScore();
    }

    public BoardInfo.BoardOptions getCellValue(int i_Row, int i_Col)
    {
        return m_Players.get(m_CurrPlayer).getBoard().getCellValue(i_Row, i_Col);
    }

    public ArrayList<BlockValues>[] getColumnList()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getColBlocks();
    }

    public ArrayList<BlockValues>[] getRowList()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getRowBlocks();
    }

    public int getGameBoardColumn()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getBoardWidth();
    }

    public int getGameBoardRow()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getBoardHeight();
    }

    public int getMaxBlockNumberInRow()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getMaxBlockNumberInRow();
    }

    public int getMaxBlockNumberInCol()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getMaxBlockNumberInCol();
    }

    public boolean IsPlayerWon() { return m_Players.get(m_CurrPlayer).IsPlayerWon(); }

    public boolean getFinishAllRound() { return m_finishAllRound; }

    public String getID() { return m_Players.get(m_CurrPlayer).getIDasString(); }

    public int getCurrGameRound()
    {
        return  m_CurrRound;
    }

    public String getGameMoves(int i_IndexPlayer)
    {
        StringBuilder AllMovesUntilNow = new  StringBuilder();
        LinkedList<GameMove> allMovesFromPlayer = m_Players.get(i_IndexPlayer).getAllMoves();
        if(allMovesFromPlayer.size() > 0){
            for (GameMove gameMove : allMovesFromPlayer) {
                AllMovesUntilNow.append(gameMove.toString() + System.lineSeparator());
            }
        }
        else{
            AllMovesUntilNow.append("Not have Game Moves.");
        }

        return AllMovesUntilNow.toString();
    }

    public boolean IsEndOfRound() {
        return  m_CurrRound == m_TotalRounds && m_CurrPlayer == m_Players.size() - 1;
    }
    public boolean canMakeAnotherMove() {return (m_currMoveOfSamePlayer < m_MaxMovesPerTurn);}
    public boolean hadComputerPlayed(){return m_computerPlayed.getValue() >0;}

    public Pair<Integer,Integer> getComputerPlayersAndSetCurrPlyToCompFirst(){
        m_CurrPlayer=m_computerPlayed.getKey();
        return m_computerPlayed;
    }

    public boolean isFullPlayers()
    {
        if(m_TotalPlayers == m_Players.size()){
            return true;
        }
        return false;
    }

    public boolean hasRoom() {
        return m_Players.size() < m_TotalPlayers;
    }

    public int getTotalRounds() {
        return m_TotalRounds;
    }

    public int getCurrMoveOfSamePlayer() {
        return m_currMoveOfSamePlayer;
    }

    public String getGameTitle() {
        return m_Title;
    }

    public String getWinnerName() {
        return m_WinnerName;
    }


    public void setActiveGame(boolean gamestatus) {
        m_ActiveGame = gamestatus;
    }

    public boolean isActiveGame() {
        return m_ActiveGame;
    }

    public boolean getM_TechnicalVictory() {
        return m_TechnicalVictory;
    }


    public synchronized void removePlayer(String playerNameToRemove)
    {
        int plyIndex = 0;
        for (Player ply: m_Players)
        {
            if(ply.getName().equals(playerNameToRemove)) {
                m_Players.remove(plyIndex);
                checkTechnicalVictory();
                break;
            }
            plyIndex++;
        }
    }

    private void checkTechnicalVictory()
    {
        if(m_Players.size() == 1 && m_TotalPlayers > 1){
            m_TechnicalVictory = true;
            m_WinnerName = m_Players.get(0).getName();
        }
    }

    public boolean notInGame(String playerName)
    {
        boolean result = true;
        for (Player ply : m_Players) {
            if (ply != null && ply.getName().equals(playerName)) {
                result = false;
                break;
            }
        }
        return result;
    }
    public BoardInfo getOriginalBoard (){return m_OrginalBoard;}
    public String getNameCurrPlayer() {
        return m_Players.get(m_CurrPlayer).getName();
    }

//endregion

}