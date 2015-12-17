package pl.edu.agh.to.game.bot;

import org.junit.Test;
import pl.edu.agh.to.game.common.Controller;
import pl.edu.agh.to.game.common.state.CarState;
import pl.edu.agh.to.game.common.state.GameState;
import pl.edu.agh.to.game.common.state.Vector;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.validateMockitoUsage;


public class AStarBotTest extends TestHelpers {
    private Controller underTest = new AStarBot();

    @Test
    public void testWillHeadProperWayOnSimpleEmptySquareBoard() {
        board = SIMPLE_EMPTY_BOARD;
        position = board.getStartingPosition();
        velocity = new Vector(0, 0);
        generateData();
        int bestIndex = allowedPositions.indexOf(new Vector(2, 2));
        assertEquals("should point (2, 2)", bestIndex, underTest.makeMove(gameState, id, allowedPositions));
    }

    @Test
    public void testWillGetToFinishInOptimalMovesOnSimpleEmptySquareBoard() {
        board = SIMPLE_EMPTY_BOARD;
        velocity = new Vector(0, 0);

        moveTowardsFinishMaxOptimalMoves();
    }

    @Test
    public void testWillGetToFinishOnSnakeOneWayBoardBoard() {
        board = SNAKE_ONE_WAY_BOARD;
        velocity = new Vector(0, 0);

        moveTowardsFinishMaxOptimalMoves();
    }
    @Test
    public void testWillGetToFinishOnSJumpBoard() {
        board = STRAIGHT_JUMP_BOARD;
        velocity = new Vector(0, 2);

        moveTowardsFinishMaxOptimalMoves();
    }

    private void moveTowardsFinishMaxOptimalMoves() {
        position = board.getStartingPosition();
        int moves = 0;

        while (!position.equals(board.getFinish()) && moves <= board.getOptimalMoves()) {
            moves++;
            generateData();
            int nextPositionIndex = underTest.makeMove(gameState, id, allowedPositions);
            Vector newPosition = allowedPositions.get(nextPositionIndex);
            velocity = calculateNewVelocity(newPosition);
            position = newPosition;
        }

        assertEquals("Did not get to finish in moves limit: "+board.getOptimalMoves(), board.getFinish(), position);
    }

}
