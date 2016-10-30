package GriddlerWeb.GameLogic;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Stack;

public class Player
{
    private static final float FULL_SCORE = 100;

    private Integer m_ID;
    private String m_Name;
    private boolean m_isHuman;
    private BoardInfo m_Board;
    private LinkedList<GameMove> m_AllMoves;
    private Stack<GameMove> m_UndoMoves;
    private GameMove m_RedoMove;
    private GameMove m_CurrentMove;
    private float m_Score;

    // cstor
    public Player(String i_Name, BoardInfo i_Board, boolean i_isHuman)
    {
        m_Name = i_Name;
        m_isHuman = i_isHuman;
        m_Board = new BoardInfo(i_Board);

        m_AllMoves = new LinkedList<>();
        m_UndoMoves = new Stack<>();
        m_RedoMove = null;
        m_CurrentMove = null;
        m_Score = 0;
    }

    // Public Function
    public void AddMoveUpdateBoard(GameMove i_MovesToEnter)
    {
        InsertNewMove(i_MovesToEnter);
        m_CurrentMove = i_MovesToEnter;
    }

    private void InsertNewMove(GameMove i_MovesToEnter)
    {
        // save cells before (to be able to do "Undo")
        LinkedList<Cell> cellBeforeNewMove = m_Board.backUpCellsForUndo(i_MovesToEnter);
        m_UndoMoves.add(new GameMove(cellBeforeNewMove));

        // save & aplly new GameMove
        m_AllMoves.addLast((i_MovesToEnter));
        m_Board.EnterNewMoveToBoard(i_MovesToEnter.getCells());

        m_RedoMove = null;   // only after Undo Can do Redo !!
    }

    public GameMove UndoMove()
    {
        GameMove returnGameMove = null;
        if (!m_UndoMoves.empty())
        {
            m_RedoMove = m_AllMoves.removeLast();
            GameMove lastGameMove = m_UndoMoves.pop();
            m_Board.EnterNewMoveToBoard(lastGameMove.getCells());
            handleCurrendGameMove();
            returnGameMove = lastGameMove;
        }
        return returnGameMove;
    }

    private void handleCurrendGameMove() {
        if(!m_AllMoves.isEmpty()){
            m_CurrentMove = m_AllMoves.peekLast();
        }
        else {
            m_CurrentMove = new GameMove(null);
        }
    }

    public GameMove RedoMove()
    {
        GameMove returnGameMove = null;
        if (m_RedoMove != null)
        {
            returnGameMove = m_RedoMove;
            AddMoveUpdateBoard(m_RedoMove);
            m_RedoMove = null;
        }
        return returnGameMove;
    }

    // GET / SET
    public BoardInfo getBoard() {
        return m_Board;
    }
    public LinkedList<GameMove> getAllMoves() {
        return m_AllMoves;
    }
    public float getScore() { return m_Score; }
    public boolean IsPlayerWon() { return m_Score >= FULL_SCORE; }
    public String getIDasString() {
        return m_ID.toString();
    }
    public Integer getID() { return m_ID; }
    public String getName() {
        return m_Name;
    }
    public GameMove getCurrentGameMove() {
        return m_CurrentMove;
    }
    public void setScore(float i_Score){m_Score = i_Score;}
    public boolean isHuman(){return m_isHuman;}
}