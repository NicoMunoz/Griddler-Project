package GriddlerWeb.GameLogic;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by nicom on 10/30/2016.
 */
public class ReplayMove {
    private LinkedList<GameMove> allMoves;
    private Stack<GameMove> undoMoves;
    private BoardInfo board;
    private int currIndex;

    public ReplayMove(LinkedList<GameMove> allMoves, int currIndex,BoardInfo board) {
        this.allMoves = allMoves;
        this.currIndex = currIndex;
        this.board = board;
        undoMoves = new Stack<>();
    }

    public boolean moveNext() {
        boolean canMake = true;
        if(currIndex<allMoves.size() -1) {
            currIndex++;
            LinkedList<Cell> cellBeforeNewMove = board.backUpCellsForUndo(allMoves.get(currIndex));
            undoMoves.add(new GameMove(cellBeforeNewMove));
            board.EnterNewMoveToBoard(allMoves.get(currIndex).getCells());
        }
        else
        {
           canMake = false;
        }
        return canMake;
    }

    public boolean movePrev() {
        boolean canMake = true;
        if (currIndex > 0) {
            currIndex--;
            GameMove lastGameMove = undoMoves.pop();
            board.EnterNewMoveToBoard(lastGameMove.getCells());
        }
        else{
            canMake =false;
        }
        return canMake;
    }

    public BoardInfo getBoard(){return board;}
}
