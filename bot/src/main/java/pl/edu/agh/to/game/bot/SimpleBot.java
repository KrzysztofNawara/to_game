package pl.edu.agh.to.game.bot;

import pl.edu.agh.to.game.common.Controller;
import pl.edu.agh.to.game.common.state.GameState;
import pl.edu.agh.to.game.common.state.Vector;

import java.util.List;

/**
 * Created by damian on 02.12.15.
 */
public class SimpleBot implements Controller {
    @Override
    public int makeMove(GameState gameState, int currentCarId, List<Vector> allowedPositions) {
        return 0;
    }
}
