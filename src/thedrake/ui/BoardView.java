package thedrake.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import thedrake.*;

import java.util.List;

public class BoardView extends GridPane implements TileViewContext {

    private GameState gameState;

    private ValidMoves validMoves;

    private TileView selected;

    public BoardView(GameState gameState) {
        this.gameState = gameState;
        this.validMoves = new ValidMoves(gameState);

        PositionFactory positionFactory = gameState.board().positionFactory();
        for (int y = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++) {
                int i = x;
                int j = 3 - y;
                BoardPos boardPos = positionFactory.pos(i, j);
                add(new TileView(boardPos, gameState.tileAt(boardPos), this), x+1, y+1);
            }
        }

        setHgap(5);
        setVgap(5);
        setPadding(new Insets(15));
        setAlignment(Pos.CENTER);


        add(new Text("BLUE"), 0, 3);
        add(new Text("ORANGE"), 5, 3);

        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().DRAKE, PlayingSide.BLUE, TroopFace.AVERS), this, true), 0, 5);
        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().CLUBMAN, PlayingSide.BLUE, TroopFace.AVERS ), this, true), 1, 5);
        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().CLUBMAN, PlayingSide.BLUE, TroopFace.AVERS ), this, true), 2, 5);
        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().MONK, PlayingSide.BLUE, TroopFace.AVERS ), this, true), 3, 5);
        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().SPEARMAN, PlayingSide.BLUE, TroopFace.AVERS ), this, true), 4, 5);
        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().SWORDSMAN, PlayingSide.BLUE, TroopFace.AVERS ), this, true), 5, 5);
        add(new TileView(positionFactory.pos(0,6), new TroopTile(new StandardDrakeSetup().ARCHER, PlayingSide.BLUE, TroopFace.AVERS ), this, true), 6, 5);

        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().DRAKE, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 0, 0);
        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().CLUBMAN, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 1, 0);
        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().CLUBMAN, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 2, 0);
        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().MONK, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 3, 0);
        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().SPEARMAN, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 4, 0);
        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().SWORDSMAN, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 5, 0);
        add(new TileView(positionFactory.pos(0,7), new TroopTile(new StandardDrakeSetup().ARCHER, PlayingSide.ORANGE, TroopFace.AVERS ), this, true), 6, 0);



    }

    @Override
    public void tileViewSelected(TileView tileView) {
        if (selected != null && selected != tileView) {
            selected.unselect();
        }


        selected = tileView;

        clearMoves();
        if(!tileView.getInStack())
            showMoves(validMoves.boardMoves(tileView.position()));
        else
            showMoves(validMoves.movesFromStack());
    }

    @Override
    public void executeMove(Move move) {
        if(selected != null) {
            if(selected.getInStack())
                selected.setVisible(false);
            selected.setInStack(false);
            selected.unselect();
            selected = null;
            clearMoves();
            gameState = move.execute(gameState);
            validMoves = new ValidMoves(gameState);
            updateTiles();
        }
    }

    private void updateTiles() {
        if(gameState.result() == GameResult.VICTORY){
            add(new Text(gameState.armyNotOnTurn().side() + " won"), 6, 2);
        }

        for (Node node : getChildren()) {
            if(node.getClass() == TileView.class) {
                TileView tileView = (TileView) node;
                if(tileView.getInStack() && !gameState.army(PlayingSide.BLUE).stack().contains(tileView.getTile())){
                    tileView.setVisible(false);
                    //tileView.update();
                    //continue;
                }
                if(tileView.getInStack() && !gameState.army(PlayingSide.ORANGE).stack().contains(tileView.getTile())){
                    tileView.setVisible(false);
                    //tileView.update();
                    //continue;
                }
                tileView.setTile(gameState.tileAt(tileView.position()));
                tileView.update();
            }
            if(node.getClass() == Text.class){
                Text text = (Text) node;
                if(text.getText().contains("BLUE")){
                    text.setText("BLUE: " + gameState.army(PlayingSide.BLUE).captured().size());
                }
                if(text.getText().contains("ORANGE")){
                    text.setText("ORANGE: " + gameState.army(PlayingSide.ORANGE).captured().size());
                }
            }
        }



    }

    private void clearMoves() {
        for (Node node : getChildren()) {
            if(node.getClass() == TileView.class) {
                TileView tileView = (TileView) node;
                tileView.clearMove();
            }
        }
    }

    private void showMoves(List<Move> moveList) {
        for (Move move : moveList) {
            tileViewAt(move.target()).setMove(move);
        }
    }

    private TileView tileViewAt(BoardPos target) {
        int index = (3 - target.j()) * 4 + target.i();
        return (TileView) getChildren().get(index);

    }

}
