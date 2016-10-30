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

    public ArrayList<Player> getPlayers(){ return m_Players;}

    public ArrayList<BlockValues>[] getColumnList()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getColBlocks();
    }

    public ArrayList<BlockValues>[] getRowList()
    {
        return m_Players.get(m_CurrPlayer).getBoard().getRowBlocks();
    }

    public boolean getFinishAllRound() { return m_finishAllRound; }

    public int getCurrGameRound()
    {
        return  m_CurrRound;
    }



    public boolean canMakeAnotherMove() {return (m_currMoveOfSamePlayer < m_MaxMovesPerTurn);}

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
            if(ply.getName().equals(playerNameToRemove))
            {
                m_Players.remove(plyIndex);
                checkTechnicalVictory();

                if(m_Players.size() == 0){
                    initGame();
                }
                break;
            }
            plyIndex++;
        }
    }

    private void initGame() {
        m_CurrPlayer = FIRST_PLAYER;
        m_currMoveOfSamePlayer = TURN_START_VALUE;
        m_CurrRound = ROUND_START_VALUE;
        m_ActiveGame = true;
        m_WinnerName = null;
        m_TechnicalVictory = false;
        m_finishAllRound = false;
        m_computerPlayed = new Pair<>(0,0);
        m_OrginalBoard.initBoard();
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