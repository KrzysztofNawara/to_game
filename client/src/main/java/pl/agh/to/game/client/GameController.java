package pl.agh.to.game.client;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pl.edu.agh.to.game.common.state.CarState;
import pl.edu.agh.to.game.common.state.GameState;
import pl.edu.agh.to.game.common.state.Vector;
import pl.edu.agh.to.game.remoteproxy.client.ClientActionHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GameController implements ClientActionHandler {
    private Canvas gameCanvas;
    private Canvas lineLayer;
    private GameState gameState;
    private GameModel gameModel;
    private ClientRemoteProxy clientProxy;

    private double startX;
    private double startY;
    private double endX;
    private double endY;

    private static boolean movePerfGlobal = false;
    private static Vector movePerfVector;

    public GameController(Canvas gameCanvas, Canvas lineLayer, GameState gameState) {
        this.gameCanvas = gameCanvas;
        this.gameState = gameState;
        this.lineLayer = lineLayer;
    }

    public void init() {
        lineLayer.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            GraphicsContext graphicsContext = lineLayer.getGraphicsContext2D();
            graphicsContext.save();
            System.out.println("PRESSED");
            startX = event.getX();
            startY = event.getY();
            endX = startX;
            endY = startY;
        });

        lineLayer.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            System.out.println("DRAGGED");

            GraphicsContext graphicsContext = lineLayer.getGraphicsContext2D();
            graphicsContext.clearRect(0, 0, lineLayer.getWidth(), lineLayer.getHeight());
            graphicsContext.setFill(Color.TRANSPARENT);
            graphicsContext.rect(0, 0, lineLayer.getWidth(), lineLayer.getHeight());
            graphicsContext.setFill(Color.BLACK);


            endX = event.getX();
            endY = event.getY();
            graphicsContext.strokeLine(startX, startY, endX, endY);

        });

        lineLayer.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            System.out.println("RELEASED");
            GraphicsContext graphicsContext = lineLayer.getGraphicsContext2D();
            endX = event.getX();
            endY = event.getY();
            graphicsContext.strokeLine(startX, startY, endX, endY);
            System.out.println(startX + " " + startY);
            System.out.println(endX + " " + endY);
            graphicsContext.setFill(Color.TRANSPARENT);
            graphicsContext.clearRect(0, 0, lineLayer.getWidth(), lineLayer.getHeight());
            graphicsContext.setFill(Color.BLACK);

            int i = (int) (endX / (StartScreen.pointSize));
            int j = (int) (endY / (StartScreen.pointSize));
            boolean movePerformed = false;

            if (!gameModel.getAvailableMoves().isEmpty()) {
                for (Vector v : gameModel.getAvailableMoves()) {
                    if (i == v.getX() && j == v.getY()) {
                        // player will be moved - note: this will be commented since we don't need to update model here
                        CarState changed = new CarState(new Vector(i, j), new Vector(0, 0));
                        gameModel.setCarChange(gameModel.ourCar, changed);
                        movePerformed = true;
                        movePerfVector = v;
                    }
                }
            }

            if (movePerformed) {
                gameModel.emptyAvailableMoves();
                movePerfGlobal = true;
            }

            System.out.println(gameModel.getMapOfCars().toString());

            System.out.println(i + " " + j);
            redraw();

        });
        handleGameStarted(gameState);
        Set<Vector> vectors = new HashSet<>();
        Vector v = new Vector();
        v = v.setX(2);
        v = v.setY(3);
        Vector v2 = new Vector();
        v2 = v2.setY(6);
        v2 = v2.setX(8);
        vectors.add(v);
        vectors.add(v2);
        handleNextMove(vectors);
        CarState change = new CarState(new Vector(0,8), new Vector(0,0));
        handleMovePerformed(1, change);
        change = new CarState(new Vector(4,4), new Vector(0,0));
        handleMovePerformed(1, change);
    }

    public void redraw() {
        this.drawMap(gameCanvas);
    }

    private void drawMap(Canvas gameCanvas) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());


        //drawing only background with possible no go positions
        gameCanvas.setHeight(gameModel.getMaxY() * StartScreen.pointSize);
        gameCanvas.setWidth(gameModel.getMaxX() * StartScreen.pointSize);
        lineLayer.setHeight(gameModel.getMaxY() * StartScreen.pointSize);
        lineLayer.setWidth(gameModel.getMaxX() * StartScreen.pointSize);

        int mapSizeX = gameModel.getMaxX();
        int mapSizeY = gameModel.getMaxY();

        gameCanvas.setWidth(mapSizeX * StartScreen.pointSize);
        gameCanvas.setHeight(mapSizeY * StartScreen.pointSize);

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, mapSizeX, mapSizeY);

        // printing possible and nogo positions
        for (int i = 0; i < mapSizeX; i++) {
            for (int j = 0; j < mapSizeY; j++) {
                if (gameModel.map[i][j]) {
                    gc.setFill(Color.BISQUE);
                } else {
                    gc.setFill(Color.RED);
                }
                gc.fillRect(i * StartScreen.pointSize, j * StartScreen.pointSize, StartScreen.pointSize / 2, StartScreen.pointSize / 2);
            }
        }

        //Drawing positions of cars
        System.out.println(gameModel.getMapOfCars().size());
        for (Map.Entry<Integer, CarState> carPositionEntry : gameModel.getMapOfCars().entrySet()) {
            gc.setFill(Color.BLUE);
            Vector positionOfCar = carPositionEntry.getValue().getPosition();
            gc.fillRect(positionOfCar.getX() * StartScreen.pointSize, positionOfCar.getY() * StartScreen.pointSize, StartScreen.pointSize / 2, StartScreen.pointSize / 2);
            gc.setFill(Color.BLACK);
            gc.fillText(carPositionEntry.getKey().toString(), positionOfCar.getX() * StartScreen.pointSize, positionOfCar.getY() * StartScreen.pointSize + StartScreen.pointSize);
        }

        //Drawing available moves for player
        for (Vector v : gameModel.getAvailableMoves()) {
            gc.setFill(Color.YELLOW);
            gc.fillRect(v.getX() * StartScreen.pointSize, v.getY() * StartScreen.pointSize, StartScreen.pointSize / 2, StartScreen.pointSize / 2);
        }

    }

    private void deleteCarFromMap(int carId) {
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        CarState carToBeDeleted = gameModel.getMapOfCars().get(carId);
        gc.setFill(Color.BISQUE);
        gc.fillRect(StartScreen.pointSize * carToBeDeleted.getPosition().getX(), StartScreen.pointSize * carToBeDeleted.getPosition().getY(), StartScreen.pointSize / 2, StartScreen.pointSize / 2);
        gameModel.getMapOfCars().remove(carId);
    }


    @Override
    public Vector handleNextMove(Set<Vector> availableMoves) {
        gameModel.setAvailableMoves(availableMoves);
        redraw();

        //TODO busy loop - to change
        /*while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (movePerfGlobal) {
                movePerfGlobal = false;
                return movePerfVector;
            }
        }*/
        return null;
    }

    @Override
    public void handleMovePerformed(int carId, CarState change) {
        gameModel.setCarChange(carId, change);
        redraw();
    }

    @Override
    public void handleGameStarted(GameState initialState) {
        gameModel = new GameModel(initialState);
        this.drawMap(gameCanvas);
    }

    @Override
    public void handleCarLost(int carId) {
        gameModel.getMapOfCars().remove(carId);
    }

    @Override
    public void handleGameOver(int winnerId) {
        System.out.println("GAME OVER!");
        GraphicsContext gc = gameCanvas.getGraphicsContext2D();

        Font previousFont = gc.getFont();
        Paint previousFill = gc.getFill();
        TextAlignment previousAlignment = gc.getTextAlign();

        gc.setFill(Color.LIGHTGOLDENRODYELLOW);
        gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        gc.setFont(new Font("Arial", gameCanvas.getWidth() / 25));
        gc.setFill(Color.BLACK);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("GAME OVER! WINNER : " + winnerId, gameCanvas.getWidth() / 2, gameCanvas.getHeight() / 2);

        gc.setTextAlign(previousAlignment);
        gc.setFont(previousFont);
        gc.setFill(previousFill);
    }

    @Override
    public void receiveCarId(int carId) {
        Map<Integer, CarState> mapOfCars = gameModel.getMapOfCars();
    }



}
