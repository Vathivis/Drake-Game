package thedrake;

import java.util.ArrayList;
import java.util.List;

public final class TroopTile implements Tile {

    private Troop troop;
    private PlayingSide side;
    private TroopFace face;


    public TroopTile(Troop troop, PlayingSide side, TroopFace face){
        this.troop = troop;
        this.side = side;
        this.face = face;
    }

    // Vrací barvu, za kterou hraje jednotka na této dlaždici
    public PlayingSide side(){
        return side;
    }

    // Vrací stranu, na kterou je jednotka otočena
    public TroopFace face(){
        return face;
    }

    // Jednotka, která stojí na této dlaždici
    public Troop troop(){
        return troop;
    }

    // Vrací False, protože na dlaždici s jednotkou se nedá vstoupit
    @Override
    public boolean canStepOn(){
        return false;
    }

    // Vrací True
    @Override
    public boolean hasTroop(){
        return true;
    }

    @Override
    public List<Move> movesFrom(BoardPos pos, GameState state) {

        List<TroopAction> list = new ArrayList<>(troop.actions(face));

        List<Move> moves = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            moves.addAll(list.get(i).movesFrom(pos, side, state));
        }

        return moves;

    }

    // Vytvoří novou dlaždici, s jednotkou otočenou na opačnou stranu
    // (z rubu na líc nebo z líce na rub)
    public TroopTile flipped(){
        if(face == TroopFace.AVERS){
            return new TroopTile(troop, side, TroopFace.REVERS);
        }
        else {
            return new TroopTile(troop, side, TroopFace.AVERS);
        }
    }
}
