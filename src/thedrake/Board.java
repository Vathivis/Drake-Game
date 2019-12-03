package thedrake;

import java.util.ArrayList;

public class Board {

	private final int dimension;
	private final ArrayList<ArrayList<BoardTile>> tiles = new ArrayList<>();

	// Konstruktor. Vytvoří čtvercovou hrací desku zadaného rozměru, kde všechny dlaždice jsou prázdné, tedy BoardTile.EMPTY
	public Board(int dimension) {
		this.dimension = dimension;
		for(int i = 0; i < dimension; ++i){
			tiles.add(new ArrayList<>());
			for(int j = 0; j < dimension; ++j){
				tiles.get(i).add(BoardTile.EMPTY);
			}
		}
	}

	// Rozměr hrací desky
	public int dimension() {
		return dimension;
	}

	// Vrací dlaždici na zvolené pozici.
	public BoardTile at(BoardPos pos) {
		return tiles.get(pos.i()).get(pos.j());
	}

	// Vytváří novou hrací desku s novými dlaždicemi. Všechny ostatní dlaždice zůstávají stejné
	public Board withTiles(TileAt ...ats) {
		Board updatedBoard = new Board(dimension);


		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				updatedBoard.tiles.get(i).set(j, tiles.get(i).get(j));
			}
		}

		for(TileAt tile: ats){
			updatedBoard.tiles.get(tile.pos.i()).set(tile.pos.j(), tile.tile);
		}

		return updatedBoard;
	}

	// Vytvoří instanci PositionFactory pro výrobu pozic na tomto hracím plánu
	public PositionFactory positionFactory() {
		return new PositionFactory(dimension);
	}
	
	public static class TileAt {
		public final BoardPos pos;
		public final BoardTile tile;

		public TileAt(BoardPos pos, BoardTile tile) {
			this.pos = pos;
			this.tile = tile;
		}
	}
}

