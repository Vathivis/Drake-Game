package thedrake;

import java.util.List;

public class Troop {

    private final String name;
    private final Offset2D aversPivot, reversPivot;
    private final List<TroopAction> aversActions;
    private final List<TroopAction> reversActions;

    public Troop(String name, Offset2D aversPivot, Offset2D reversPivot, List<TroopAction> aversActions, List<TroopAction> reversActions){
        this.name = name;
        this.aversPivot = aversPivot;
        this.reversPivot = reversPivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, Offset2D pivot, List<TroopAction> aversActions, List<TroopAction> reversActions){
        this.name = name;
        aversPivot = pivot;
        reversPivot = pivot;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
    }

    public Troop(String name, List<TroopAction> aversActions, List<TroopAction> reversActions){
        this.name = name;
        this.aversActions = aversActions;
        this.reversActions = reversActions;
        aversPivot = new Offset2D(1,1);
        reversPivot = new Offset2D(1,1);
    }

    //Vrací seznam akcí pro zadanou stranu jednotky
    public List<TroopAction> actions(TroopFace face){
        if(face == TroopFace.AVERS)
            return aversActions;
        return reversActions;
    }

    public String name(){
        return name;
    }

    public Offset2D pivot(TroopFace face){
        if(face == TroopFace.AVERS)
            return aversPivot;
        else
            return reversPivot;
    }


}
