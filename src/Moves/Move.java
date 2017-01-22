package Moves;

/**
 * Created by dz on 20.1.17..
 */
public class Move {
    private int fromPoint;
    private int toPoint;

    public Move(int f, int t){ fromPoint = f; toPoint = t; }

    public int getFromPoint(){ return fromPoint; }
    public int getToPoint(){ return toPoint; }
}
