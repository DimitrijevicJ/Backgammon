package Game;

/**
 * Created by dz on 21.1.17..
 */
public class Dice {
    private int values[] = new int[2];
    private boolean doubleDices = false;

    public Dice(){}
    public Dice(int k1, int k2){values[0]=k1; values[1]=k2;}
    public Dice(Dice dice){values[0]=dice.values[0]; values[1]=dice.values[1];}

    public void rollDices(){
        values[0] = rollDice();
        values[1] = rollDice();
        if(values[0]==values[1]) doubleDices = true;
    }

    public boolean checkDice(int dice){
        for(int i = 0; i<2; i++)
            if(values[i]==dice) return true;    //found the dice
        return false;
    }

    public void useDice(int dice){
        for(int i = 0; i<2; i++)
            if(values[i]==dice){
                values[i]=0;
                return;
            }
    }

    private int rollDice(){
        //TODO rolling dice algorithm
        return 0;
    }

    public boolean isDoubleDices(){return doubleDices;}
}
