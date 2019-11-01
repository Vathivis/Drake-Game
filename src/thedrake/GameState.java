package thedrake;

import java.util.Set;

public class GameState{
	private final Board board;
	private final PlayingSide sideOnTurn;
	private final Army blueArmy;
	private final Army orangeArmy;
	private final GameResult result;
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy) {
		this(board, blueArmy, orangeArmy, PlayingSide.BLUE, GameResult.IN_PLAY);
	}
	
	public GameState(
			Board board, 
			Army blueArmy, 
			Army orangeArmy, 
			PlayingSide sideOnTurn, 
			GameResult result) {
		this.board = board;
		this.sideOnTurn = sideOnTurn;
		this.blueArmy = blueArmy;
		this.orangeArmy = orangeArmy;
		this.result = result;
	}
	
	public Board board() {
		return board;
	}
	
	public PlayingSide sideOnTurn() {
		return sideOnTurn;
	}
	
	public GameResult result() {
		return result;
	}
	
	public Army army(PlayingSide side) {
		if(side == PlayingSide.BLUE) {
			return blueArmy;
		}
		
		return orangeArmy;
	}
	
	public Army armyOnTurn() {
		return army(sideOnTurn);
	}
	
	public Army armyNotOnTurn() {
		if(sideOnTurn == PlayingSide.BLUE)
			return orangeArmy;
		
		return blueArmy;
	}

	// Vrátí dlaždici, která se nachází na hrací desce na pozici pos.
	// Musí tedy zkontrolovat, jestli na této pozici není jednotka z
	// armády nějakého hráče a pokud ne, vrátí dlaždici z objektu board
	public Tile tileAt(BoardPos pos) {
		Tile tile1 = blueArmy.boardTroops().at(pos).get();
		if(tile1.hasTroop())
			return tile1;
		Tile tile2 = orangeArmy.boardTroops().at(pos).get();
		if(tile2.hasTroop())
			return tile2;

		return board.at(pos);
	}

	// Vrátí true, pokud je možné ze zadané pozice začít tah nějakou
	// jednotkou. Vrací false, pokud stav hry není IN_PLAY, pokud
	// na dané pozici nestojí žádná jednotka nebo pokud na pozici
	// stojí jednotka hráče, který zrovna není na tahu.
	// Při implementaci vemte v úvahu zahájení hry. Dokud nejsou
	// postaveny stráže, žádné pohyby jednotek po desce nejsou možné.
	private boolean canStepFrom(TilePos origin) {

		if(result != GameResult.IN_PLAY || armyOnTurn().boardTroops().isPlacingGuards() || !armyOnTurn().boardTroops().isLeaderPlaced())
			return false;

		if(!armyOnTurn().boardTroops().troopPositions().contains(origin))
			return false;

		return true;
	}

	// Vrátí true, pokud je možné na zadanou pozici dokončit tah nějakou
	// jednotkou. Vrací false, pokud stav hry není IN_PLAY nebo pokud
	// na zadanou dlaždici nelze vstoupit (metoda Tile.canStepOn).
	private boolean canStepTo(TilePos target) {

		if(result != GameResult.IN_PLAY || target == TilePos.OFF_BOARD || !board.at(new BoardPos(board.dimension(), target.i(), target.j())).canStepOn()
				|| armyOnTurn().boardTroops().at(target).isPresent() || armyNotOnTurn().boardTroops().at(target).isPresent()){
			return false;
		}

		if(!armyOnTurn().boardTroops().isLeaderPlaced()){
			if(armyOnTurn() == blueArmy && target.row() > 1){
				return false;
			}
			if(armyOnTurn() == orangeArmy && target.row() < board.dimension()){
				return false;
			}
		}



		return true;
	}

	// Vrátí true, pokud je možné na zadané pozici vyhodit soupeřovu jednotku.
	// Vrací false, pokud stav hry není IN_PLAY nebo pokud
	// na zadané pozici nestojí jednotka hráče, který zrovna není na tahu.
	private boolean canCaptureOn(TilePos target) {
		if(result != GameResult.IN_PLAY || !armyNotOnTurn().boardTroops().troopPositions().contains(target))
			return false;


		return true;
	}
	
	public boolean canStep(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canStepTo(target);
	}
	
	public boolean canCapture(TilePos origin, TilePos target)  {
		return canStepFrom(origin) && canCaptureOn(target);
	}

	// Vrátí true, pokud je možné na zadanou pozici položit jednotku ze
	// zásobníku.. Vrací false, pokud stav hry není IN_PLAY, pokud je zásobník
	// armády, která je zrovna na tahu prázdný, pokud není možné na danou
	// dlaždici vstoupit. Při implementaci vemte v úvahu zahájení hry, kdy
	// se vkládání jednotek řídí jinými pravidly než ve střední hře.
	public boolean canPlaceFromStack(TilePos target) {

		if(armyOnTurn().boardTroops().isPlacingGuards()){
			if(armyOnTurn() == blueArmy && target.row() > 2)
				return false;

			if(armyOnTurn() == orangeArmy && target.row() < board.dimension() - 1)
				return false;

			if(!armyOnTurn().boardTroops().leaderPosition().neighbours().contains(target))
				return false;
		}

		if(result != GameResult.IN_PLAY || armyOnTurn().stack().isEmpty() || !canStepTo(target))
			return false;

		if(armyOnTurn().boardTroops().isLeaderPlaced()) {
			Set<BoardPos> troops = armyOnTurn().boardTroops().troopPositions();
			for (BoardPos troop : troops) {
				if (troop.isNextTo(target)) {
					return true;
				}
			}
			return false;
		}

		return true;
	}
	
	public GameState stepOnly(BoardPos origin, BoardPos target) {		
		if(canStep(origin, target))		 
			return createNewGameState(
					armyNotOnTurn(),
					armyOnTurn().troopStep(origin, target), GameResult.IN_PLAY);
		
		throw new IllegalArgumentException();
	}
	
	public GameState stepAndCapture(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target), 
					armyOnTurn().troopStep(origin, target).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState captureOnly(BoardPos origin, BoardPos target) {
		if(canCapture(origin, target)) {
			Troop captured = armyNotOnTurn().boardTroops().at(target).get().troop();
			GameResult newResult = GameResult.IN_PLAY;
			
			if(armyNotOnTurn().boardTroops().leaderPosition().equals(target))
				newResult = GameResult.VICTORY;
			
			return createNewGameState(
					armyNotOnTurn().removeTroop(target),
					armyOnTurn().troopFlip(origin).capture(captured), newResult);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState placeFromStack(BoardPos target) {
		if(canPlaceFromStack(target)) {
			return createNewGameState(
					armyNotOnTurn(), 
					armyOnTurn().placeFromStack(target), 
					GameResult.IN_PLAY);
		}
		
		throw new IllegalArgumentException();
	}
	
	public GameState resign() {
		return createNewGameState(
				armyNotOnTurn(), 
				armyOnTurn(), 
				GameResult.VICTORY);
	}
	
	public GameState draw() {
		return createNewGameState(
				armyOnTurn(), 
				armyNotOnTurn(), 
				GameResult.DRAW);
	}
	
	private GameState createNewGameState(Army armyOnTurn, Army armyNotOnTurn, GameResult result) {
		if(armyOnTurn.side() == PlayingSide.BLUE) {
			return new GameState(board, armyOnTurn, armyNotOnTurn, PlayingSide.BLUE, result);
		}
		
		return new GameState(board, armyNotOnTurn, armyOnTurn, PlayingSide.ORANGE, result); 
	}
}
