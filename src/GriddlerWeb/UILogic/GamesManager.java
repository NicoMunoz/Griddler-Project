package GriddlerWeb.UILogic;

import GriddlerWeb.GameLogic.*;


import GriddlerWeb.GameLogic.GameLogic;
import GriddlerWeb.GameLogic.XmlManager;
import GriddlerWeb.jaxb.schema.generated.Slice;
import GriddlerWeb.jaxb.schema.generated.Square;

import java.io.InputStream;
import java.util.*;

public class GamesManager
{
    private final Map<String, GameLogic> m_GamesMap;

    private XmlManager m_XmlManager;

    public GamesManager()
    {
        m_GamesMap = new HashMap<>();
    }

    // returm error or null if all good
    public String addNewGame(InputStream file, String userNameFromSession)
    {
        m_XmlManager = new XmlManager();
        boolean gameLoaded = m_XmlManager.loadXML(file);
        if(gameLoaded)
        {
            String gameTitle = m_XmlManager.getGameData().getDynamicMultiPlayers().getGametitle();
            if(m_GamesMap.get(gameTitle) == null) // game not exsist
            {
                int rows = m_XmlManager.getGameData().getBoard().getDefinition().getRows().intValue();
                int cols = m_XmlManager.getGameData().getBoard().getDefinition().getColumns().intValue();
                ////////////////
                ArrayList<Cell> solutionCells = new ArrayList();
                ArrayList<BlockValues>[] rowBlocks, colsBlock;
                rowBlocks = new ArrayList[rows];
                colsBlock = new ArrayList[cols];
                getSolutionAndBlocks(solutionCells, rowBlocks, colsBlock);
                ////////////////
                int totalPlayers = Integer.parseInt(m_XmlManager.getGameData().getDynamicMultiPlayers().getTotalPlayers());
                int totalRounds = Integer.parseInt(m_XmlManager.getGameData().getDynamicMultiPlayers().getTotalmoves());

                GameLogic newGame = new GameLogic(userNameFromSession, gameTitle, totalPlayers, totalRounds,
                        rows, cols, rowBlocks, colsBlock, solutionCells);
                m_GamesMap.put(gameTitle, newGame);
            }
            else{
                return "Game With The Same Name Exsist";
            }
        }
        else {
            return m_XmlManager.m_ExceptionOcurred.toString();
        }
        return null;
    }

    public Map<String, GameLogic> getGames() {
        return Collections.unmodifiableMap(m_GamesMap);
    }

    //region Load Game
    public void getSolutionAndBlocks(ArrayList<Cell> i_SolutionCells,ArrayList<BlockValues>[] i_RowBlock, ArrayList<BlockValues>[] i_ColBlocks)
    {
        getBlocks(i_RowBlock, i_ColBlocks);
        List<Square> solutionSquares = m_XmlManager.getGameData().getBoard().getSolution().getSquare();
        for (Square currSquare : solutionSquares) {
            int currR = currSquare.getRow().intValue();
            int currC = currSquare.getColumn().intValue();
            i_SolutionCells.add(new Cell(currR, currC, null));
        }
    }

    private void getBlocks(ArrayList<BlockValues>[]i_RowBlocks,ArrayList<BlockValues>[] i_CollBlocks)
    {
        List<Slice> allSlices = m_XmlManager.getGameData().getBoard().getDefinition().getSlices().getSlice();
        allSlices.sort((a, b) -> a.getId().intValue() - b.getId().intValue());
        boolean incrRow=false;
        int indexRowArray = 0, indexColArray = 0;
        initializeBlocks(i_RowBlocks,i_CollBlocks);
        for (Slice currSlice : allSlices)
        {
            String[] sizeB = currSlice.getBlocks().trim().split(",");
            for (String currBlock : sizeB)
            {
                currBlock=currBlock.trim();
                int currNum=Integer.parseInt(currBlock);
                BlockValues blockToAdd = new BlockValues(currNum);
                if (currSlice.getOrientation().equals("row"))
                {
                    i_RowBlocks[indexRowArray].add(blockToAdd);
                    incrRow=true;
                } else
                {
                    i_CollBlocks[indexColArray].add(blockToAdd);
                    incrRow=false;
                }
            }
            if(incrRow) { indexRowArray++;}
            else    { indexColArray++;}
        }
    }

    private void initializeBlocks(ArrayList<BlockValues>[]i_RowBlocks,ArrayList<BlockValues>[]i_CollBlocks)
    {
        for(int index = 0; index < i_RowBlocks.length; index++)
        {
            i_RowBlocks[index] = new ArrayList<>();
        }
        for(int index=0; index < i_CollBlocks.length; index++)
        {
            i_CollBlocks[index] = new ArrayList<>();
        }
    }

    public GameLogic getSpecificGame(String gameTitleToJoin)
    {
        return m_GamesMap.get(gameTitleToJoin);
    }
    //endregion
}
