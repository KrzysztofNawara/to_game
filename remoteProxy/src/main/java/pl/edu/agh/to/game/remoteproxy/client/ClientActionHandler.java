package pl.edu.agh.to.game.remoteproxy.client;

import java.util.Set;

import pl.edu.agh.to.game.common.state.CarState;
import pl.edu.agh.to.game.common.state.GameState;
import pl.edu.agh.to.game.common.state.Vector;
import pl.edu.agh.to.game.common.state.VectorFuture;

public interface ClientActionHandler {
	VectorFuture handleNextMove(Set<Vector> availableMoves);
	void handleMovePerformed(int carId, CarState change);
	void handleGameStarted(GameState initialState);
	void handleCarLost(int carId);
	void handleGameOver(int winnerId);
	void receiveCarId(int carId);
}