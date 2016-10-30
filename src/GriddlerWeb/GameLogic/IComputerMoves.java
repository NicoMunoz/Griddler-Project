package GriddlerWeb.GameLogic;

import javafx.util.Pair;

/**
 * Created by nicom on 10/25/2016.
 */
public interface IComputerMoves {
    Pair<GameMove, Integer> calculateComputerMove(int rows, int cols);

    }
