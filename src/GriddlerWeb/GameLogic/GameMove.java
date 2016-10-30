package GriddlerWeb.GameLogic;

import java.awt.*;
import java.util.LinkedList;

public class GameMove
{
    private LinkedList<Cell> m_Cells;


    // cstor
    public GameMove(LinkedList<Cell> i_Cells)
    {
        m_Cells = i_Cells;
    }

    @Override
    public String toString()
    {
        StringBuilder GameMoveAsString = new  StringBuilder();

        for (Cell cell : m_Cells) {
            GameMoveAsString.append(cell.toString());
            GameMoveAsString.append(" ");

        }

        BoardInfo.BoardOptions status = m_Cells.getFirst().getStatus();
        GameMoveAsString.append(System.lineSeparator() + status.toString());

        return GameMoveAsString.toString();
    }

    // GET / SET
    public LinkedList<Cell> getCells() {
        return m_Cells;
    }

}