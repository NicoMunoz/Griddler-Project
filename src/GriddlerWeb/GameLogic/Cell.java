package GriddlerWeb.GameLogic;

import java.awt.*;

public class Cell
{
    private Point m_Place;
    private BoardInfo.BoardOptions m_Status;

    // cstor
    public Cell(int i_Row, int i_Col , BoardInfo.BoardOptions i_Status)
    {
        m_Place = new Point(i_Row, i_Col);
        m_Status = i_Status;
    }

    @Override
    public String toString()
    {
        return "(" + m_Place.x + "," + m_Place.y + ")";
    }

    // Get / Set
    public Point getPoint(){ return m_Place; }
    public BoardInfo.BoardOptions getStatus(){
        return m_Status;
    }
}