package GriddlerWeb.GameLogic;

import java.awt.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.LinkedList;

public class BoardInfo
{
    private static final int FIRST_INDEX_ARRAY = 0;
    private int m_Rows, m_Cols;
    private int m_MaxBlockNumberInCol, m_MaxBlockNumberInRow;
    private BoardOptions[][] m_Board;
    private ArrayList<BlockValues>[] m_RowBlocks, m_ColBlocks;
    private  PerfectBlock m_PerfectBlockCalculator;

    public static enum BoardOptions
    {
        UNDEFINED, EMPTY, FILLED;

        @Override
        public String toString() {
            if(this.equals(UNDEFINED)) {
                return "Undefined";
            }
            else if(this.equals(EMPTY)){
                return "Empty";
            }
            else{
                return "Filled";
            }
        }

        public static BoardOptions Parse(int i_IntToCheck) throws InputMismatchException
        {
            BoardOptions retVal;
            if( i_IntToCheck < 0 && i_IntToCheck > values().length ) {
                throw new InputMismatchException();
            }
            else {
                switch (i_IntToCheck){
                    case 0:
                        retVal = BoardOptions.FILLED;
                        break;
                    case 1:
                        retVal= BoardOptions.EMPTY;
                        break;
                    case 2:
                        retVal= BoardOptions.UNDEFINED;
                        break;
                    default:
                        throw new InputMismatchException();
                }
            }
            return retVal;
        }
        public static BoardOptions ParseFromString(String strToParse) throws InputMismatchException
        {
            BoardOptions retVal;
            switch (strToParse)
            {
                case "filled":
                    retVal = BoardOptions.FILLED;
                    break;
                case "empty":
                    retVal= BoardOptions.EMPTY;
                    break;
                case "undefined":
                    retVal= BoardOptions.UNDEFINED;
                    break;
                default:
                    throw new InputMismatchException();
            }
            return retVal;
        }
    }


    // cstors
    public BoardInfo(int i_Rows, int i_Cols, ArrayList<BlockValues>[] i_RowsBlock,ArrayList<BlockValues>[] i_ColsBlock)
    {
        m_Rows = i_Rows;
        m_Cols = i_Cols;
        m_Board = new BoardOptions[m_Rows][m_Cols];
        m_RowBlocks = i_RowsBlock;
        m_ColBlocks = i_ColsBlock;
        m_MaxBlockNumberInCol = findMaxBlockNumber(i_ColsBlock, i_Cols);
        m_MaxBlockNumberInRow = findMaxBlockNumber(i_RowsBlock, i_Rows);
        initBoard();
        m_PerfectBlockCalculator = new PerfectBlock(i_Rows,i_Cols,m_Board,i_ColsBlock,i_RowsBlock);
    }
    // copy cstor
    public BoardInfo(BoardInfo i_BoardToCopy)
    {
        m_Rows = i_BoardToCopy.getBoardHeight();
        m_RowBlocks = i_BoardToCopy.getRowBlocks();
        m_MaxBlockNumberInRow = findMaxBlockNumber(m_RowBlocks, m_Rows);

        m_Cols = i_BoardToCopy.getBoardWidth();
        m_ColBlocks = i_BoardToCopy.getColBlocks();
        m_MaxBlockNumberInCol = findMaxBlockNumber(m_ColBlocks, m_Cols);

        m_Board = new BoardOptions[m_Rows][m_Cols];
        initBoard();
        m_PerfectBlockCalculator = new PerfectBlock(m_Rows,m_Cols,m_Board,i_BoardToCopy.m_ColBlocks,i_BoardToCopy.m_RowBlocks);
    }
    // win board cstor
    public BoardInfo(int i_Rows, int i_Cols) {
        m_Rows = i_Rows;
        m_Cols = i_Cols;
        m_Board = new BoardOptions[m_Rows][m_Cols];
    }

    public void initBoard()
    {
        for (int currRow = FIRST_INDEX_ARRAY; currRow < m_Rows; currRow++) {
            for (int currCol = FIRST_INDEX_ARRAY; currCol < m_Cols; currCol++) {
                m_Board[currRow][currCol] = BoardOptions.UNDEFINED;
            }
        }
    }

    private int findMaxBlockNumber(ArrayList<BlockValues>[] i_Block, int size)
    {
        int maxInBlock = i_Block[0].size();
        for(int i = FIRST_INDEX_ARRAY; i < size; i++){
            if(i_Block[i].size() > maxInBlock){
                maxInBlock = i_Block[i].size();
            }
        }
        return maxInBlock;
    }

    // Public Functions
    // save the previous cells value (for Undo Actions)
    public LinkedList<Cell> backUpCellsForUndo(GameMove i_MovesToEnter)
    {
        LinkedList<Cell> cellsBeforeNewMove = new LinkedList<>();
        int row, col;
        for (Cell cell : i_MovesToEnter.getCells()) {
            row = cell.getPoint().x;
            col = cell.getPoint().y;
            cellsBeforeNewMove.add(new Cell(row, col, m_Board[row][col]));
        }
        return cellsBeforeNewMove;
    }

    // get the list of cells and uptade boad values
    public void EnterNewMoveToBoard(LinkedList<Cell> i_CellsToEnter)
    {
        for (Cell currCel : i_CellsToEnter) {
            Point cellPoint = currCel.getPoint();
            m_Board[cellPoint.x][cellPoint.y] = currCel.getStatus();
        }
        m_PerfectBlockCalculator.updatePerectScore();

    }

    public void enterValuesOfWinnerBoard(ArrayList<Cell> i_WinnerCells)
    {
        int difWithPointToMatrixValue = -1;
        for(int i = FIRST_INDEX_ARRAY; i < m_Rows; i++) {
            for(int j = FIRST_INDEX_ARRAY; j < m_Cols; j++) {
                m_Board[i][j] = BoardInfo.BoardOptions.EMPTY;
            }
        }
        for(Cell winner: i_WinnerCells) {
            m_Board[winner.getPoint().x + difWithPointToMatrixValue][winner.getPoint().y + difWithPointToMatrixValue] = BoardOptions.FILLED;
        }
    }

    // Get / Set
    public int getBoardHeight(){
        return m_Rows;
    }
    public int getBoardWidth (){
        return m_Cols;
    }
    public ArrayList<BlockValues>[] getRowBlocks() {
        return m_RowBlocks;
    }
    public ArrayList<BlockValues>[] getColBlocks() {
        return m_ColBlocks;
    }
    public int getMaxBlockNumberInRow() { return m_MaxBlockNumberInRow; }
    public int getMaxBlockNumberInCol() { return m_MaxBlockNumberInCol; }
    public BoardOptions[][] getBoard() {
        return m_Board;
    }
    public BoardInfo.BoardOptions getCellValue(int i_Row, int i_Col)throws IndexOutOfBoundsException
    {
        BoardInfo.BoardOptions cellValue = null;
        if(i_Row >= FIRST_INDEX_ARRAY && i_Row < m_Rows && i_Col >= FIRST_INDEX_ARRAY && i_Col < m_Cols){
            cellValue = m_Board[i_Row][i_Col];
        }
        if (cellValue != null){
            return m_Board[i_Row][i_Col];
        }
        else throw new IndexOutOfBoundsException("Error: Numbers are out of bounds!");
    }
}
