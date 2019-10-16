package thedrake;

import java.util.*;

public class BoardTroops {
	private final PlayingSide playingSide;
	private final Map<BoardPos, TroopTile> troopMap;
	private final TilePos leaderPosition;
	private final int guards;
	
	public BoardTroops(PlayingSide playingSide) { 
		troopMap = Collections.emptyMap();
		guards = 0;
		leaderPosition = TilePos.OFF_BOARD;
		this.playingSide = playingSide;
	}

	public BoardTroops(PlayingSide playingSide, Map<BoardPos, TroopTile> troopMap, TilePos leaderPosition, int guards) {
		this.playingSide = playingSide;
		this.troopMap = troopMap;
		this.leaderPosition = leaderPosition;
		this.guards = guards;
	}

	public Optional<TroopTile> at(TilePos pos) {
	    if(troopMap.containsKey(pos))
	        return Optional.of(troopMap.get(pos));
	    return Optional.empty();
	}
	
	public PlayingSide playingSide() {
		return playingSide;
	}
	
	public TilePos leaderPosition() {
		return leaderPosition;
	}

	public int guards() {
		return guards;
	}
	
	public boolean isLeaderPlaced() {
		if(leaderPosition != TilePos.OFF_BOARD)
			return true;
		else
			return false;
	}
	
	public boolean isPlacingGuards() {
		if(isLeaderPlaced() && guards < 2){
			return true;
		}
		else return false;
	}	
	
	public Set<BoardPos> troopPositions() {
		return troopMap.keySet();
	}

	public BoardTroops placeTroop(Troop troop, BoardPos target) {

		int updatedGuards = guards;
		if(isLeaderPlaced() && isPlacingGuards())
			updatedGuards++;

		TilePos leaderPos;
		if(!isLeaderPlaced())
			leaderPos = target;
		else
			leaderPos = leaderPosition;

        if(troopMap.get(target) != null)
            throw new IllegalArgumentException();


		Map<BoardPos, TroopTile> updatedTroopMap = new HashMap<>(troopMap);
		updatedTroopMap.put(target, new TroopTile(troop, playingSide, TroopFace.AVERS));




		return new BoardTroops(playingSide, updatedTroopMap, leaderPos, updatedGuards);
	}
	
	public BoardTroops troopStep(BoardPos origin, BoardPos target) {

		if(isPlacingGuards() || !isLeaderPlaced())
			throw new IllegalStateException();

		if(troopMap.get(origin) == null || troopMap.get(target) != null)
			throw new IllegalArgumentException();

		TilePos leaderPos = leaderPosition;
		if(troopMap.get(origin) == troopMap.get(leaderPosition))
			leaderPos = target;


		BoardTroops updatedBoardTroops = new BoardTroops(playingSide, troopMap, leaderPos, guards);

		TroopTile x = updatedBoardTroops.troopMap.get(origin);
		updatedBoardTroops.troopMap.remove(origin);
		updatedBoardTroops.troopMap.put(target, x.flipped());

		return updatedBoardTroops;
	}
	
	public BoardTroops troopFlip(BoardPos origin) {
		if(!isLeaderPlaced()) {
			throw new IllegalStateException(
					"Cannot move troops before the leader is placed.");			
		}
		
		if(isPlacingGuards()) {
			throw new IllegalStateException(
					"Cannot move troops before guards are placed.");			
		}
		
		if(!at(origin).isPresent())
			throw new IllegalArgumentException();
		
		Map<BoardPos, TroopTile> newTroops = new HashMap<>(troopMap);
		TroopTile tile = newTroops.remove(origin);
		newTroops.put(origin, tile.flipped());

		return new BoardTroops(playingSide(), newTroops, leaderPosition, guards);
	}
	
	public BoardTroops removeTroop(BoardPos target) {

		if(isPlacingGuards() || !isLeaderPlaced())
			throw new IllegalStateException();

		if(troopMap.get(target) == null)
			throw new IllegalArgumentException();

		TilePos leaderPos;
		if(troopMap.get(target) == troopMap.get(leaderPosition))
			leaderPos = TilePos.OFF_BOARD;
		else
			leaderPos = leaderPosition;

		troopMap.remove(target);

		return new BoardTroops(playingSide, troopMap, leaderPos, guards);
	}	
}
