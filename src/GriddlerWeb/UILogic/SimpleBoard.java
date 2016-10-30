package GriddlerWeb.UILogic;

import GriddlerWeb.GameLogic.BlockValues;
import GriddlerWeb.GameLogic.BoardInfo;

import java.util.ArrayList;

public class SimpleBoard {

    private final BoardInfo.BoardOptions[][] board;
    private final ArrayList<BlockValues>[] rowsBlocks;
    private final ArrayList<BlockValues>[] columnsBlocks;

    public SimpleBoard(BoardInfo.BoardOptions[][] board, ArrayList<BlockValues>[] rowsBlocks, ArrayList<BlockValues>[] columnsBlocks) {
        this.board = board;
        this.rowsBlocks = rowsBlocks;
        this.columnsBlocks = columnsBlocks;
    }
}