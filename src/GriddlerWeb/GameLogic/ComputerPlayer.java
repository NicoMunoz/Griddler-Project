package GriddlerWeb.GameLogic;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class ComputerPlayer implements IComputerMoves
{
    private static final int CHANCE_FOR_UNDO = 20, MAKE_UNDO_UPTION = 0 , AMOUNT_OF_CELLS_TO_RANDOM = 20;

        public Pair<GameMove, Integer> calculateComputerMove(int rows, int cols) {
            Random helper = new Random();
            int amountMoves = helper.nextInt(rows * cols);
            GameMove movesToReturn = new GameMove(null);
            int amountOfUndos = 0, reciveChance = 0;
            Integer iterationNumber = 0, ranRow = 0, ranCol = 0;
            BoardInfo.BoardOptions squareOptionChoosed = BoardInfo.BoardOptions.Parse(helper.nextInt(3));
            while (iterationNumber <= amountMoves)
            {
//                reciveChance = helper.nextInt(CHANCE_FOR_UNDO);
//                if (reciveChance == MAKE_UNDO_UPTION)
//                {
//                    amountOfUndos++;
//                } else {
                    LinkedList<Cell> cellsForMove = new LinkedList<>();
                    for (int squarePicked = 0; squarePicked < AMOUNT_OF_CELLS_TO_RANDOM; squarePicked++) {
                        ranRow = helper.nextInt(rows);
                        ranCol = helper.nextInt(cols);
                        cellsForMove.add(new Cell(ranRow, ranCol, squareOptionChoosed));
                   // }
                    movesToReturn = new GameMove(cellsForMove);
                    iterationNumber++;
                }
            }
            Pair<GameMove, Integer> retVal = new Pair<>(movesToReturn, amountOfUndos);
            return retVal;
        }
}
