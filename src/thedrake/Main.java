package thedrake;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import thedrake.ui.BoardView;

public class Main extends Application {
	public static void main(String[] args) {
		launch(args);
	}


	@Override
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("IntroScreen.fxml"));
		primaryStage.setTitle("TheDrake");
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();

		Button Button2Players = (Button) scene.lookup("#Button2Players");
		Button2Players.setOnAction(new EventHandler<ActionEvent>(){

			@Override
			public void handle(ActionEvent event) {
				GameState gameState = createSampleGameState();
				BoardView boardView = new BoardView(gameState);
				primaryStage.setScene(new Scene(boardView));
				primaryStage.setTitle("The Drake");
				primaryStage.show();
			}
		});

	}

	private static GameState createSampleGameState() {
		Board board = new Board(4);
		PositionFactory positionFactory = board.positionFactory();
		board = board.withTiles(new Board.TileAt(positionFactory.pos(1, 1), BoardTile.MOUNTAIN));
		return new StandardDrakeSetup().startState(board)
            /*.placeFromStack(positionFactory.pos(0, 0))
            .placeFromStack(positionFactory.pos(3, 3))
            .placeFromStack(positionFactory.pos(0, 1))
            .placeFromStack(positionFactory.pos(3, 2))
            .placeFromStack(positionFactory.pos(1, 0))
            .placeFromStack(positionFactory.pos(2, 3))*/;
	}


}
