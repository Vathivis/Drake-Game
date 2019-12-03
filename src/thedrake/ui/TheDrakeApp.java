package thedrake.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import thedrake.*;

public class TheDrakeApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        GameState gameState = createSampleGameState();
        BoardView boardView = new BoardView(gameState);
        primaryStage.setScene(new Scene(boardView));
        primaryStage.setTitle("The Drake");
        primaryStage.show();
    }

    private static GameState createSampleGameState() {
        Board board = new Board(4);
        PositionFactory positionFactory = board.positionFactory();
        board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
        return new StandardDrakeSetup().startState(board)
            .placeFromStack(positionFactory.pos(0, 0))
            .placeFromStack(positionFactory.pos(3, 3))
            .placeFromStack(positionFactory.pos(0, 1))
            .placeFromStack(positionFactory.pos(3, 2))
            .placeFromStack(positionFactory.pos(1, 0))
            .placeFromStack(positionFactory.pos(2, 3));
    }

}
