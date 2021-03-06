package Table;

import Exceptions.InvalidMoveException;
import Exceptions.NotAllTokensHomeException;
import Moves.Move;
import Tokens.RedToken;
import Tokens.Token;
import Tokens.WhiteToken;
import Game.Dice;
import Player.Player;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dz on 20.1.17..
 */
//TABLE SHOULD BE THE WAY WHITE PLAYER SEES IT
public class Table {
    private final int tableSize = 25;
    private Point[] points =  new Point[tableSize];
    private Point[] center = new Point[2];
    private Point[] outer = new Point[2];  //each player should have only its own outter and center
    {
        for(int i=0; i<tableSize; i++) points[i] = new Point(this);
        for(int i=0; i<2; i++) { center[i] = new Point(this); outer[i] = new Point(this); }
    }
    {
        for (int i = 0; i < 2; i++) {
            Token tokenW = new WhiteToken();
            Token tokenR = new RedToken();
            points[24].put(tokenW);
            points[tableSize - 24].put(tokenR);
        }
        for (int i = 0; i < 3; i++) {
            Token tokenW = new WhiteToken();
            Token tokenR = new RedToken();
            points[8].put(tokenW);
            points[tableSize - 8].put(tokenR);
        }
        for (int i = 0; i < 5; i++) {
            Token tokenW = new WhiteToken();
            Token tokenR = new RedToken();
            points[6].put(tokenW);
            points[tableSize - 6].put(tokenR);
        }
        for (int i = 0; i < 5; i++) {
            Token tokenW = new WhiteToken();
            Token tokenR = new RedToken();
            points[13].put(tokenW);
            points[tableSize - 13].put(tokenR);
        }
    }
    private int onTheMove = 0;

    public Table(){}
    public Table(Table table){
        {
            for(int i=0; i<tableSize; i++) points[i] = new Point(this);
            for(int i=0; i<2; i++) { center[i] = new Point(this, table.center[i]); outer[i] = new Point(this, table.outer[i]); }
        }

        onTheMove=table.onTheMove;
    }

    public void move(Move move, Dice dices)throws InvalidMoveException, NotAllTokensHomeException{
        //start token is always valid
        //check if the token is ours
        if(checkToken(points[move.getFromPoint()].top())) throw new InvalidMoveException();
        //check if the move is valid for the player
        if (!checkDirection(move)) throw  new InvalidMoveException();
        //check if the move is valid
        {
            Token token = (move.getFromPoint() == 25)?center[onTheMove].top():points[move.getFromPoint()].top();
            if(!(points[move.getToPoint()].checkMove(token))) throw new InvalidMoveException();
        }
        //move
        //checking outing the token - critical to point 0
        if(move.getToPoint() == 0){
            //we are trying to out the token
            AtomicInteger tokensHome = new AtomicInteger(0), tokensInGame = new AtomicInteger(0);
            countTokens(onTheMove, tokensHome, tokensInGame);
            if(tokensHome == tokensInGame){
                int dice = 0;
                for(int i = move.getFromPoint() - move.getToPoint(); i<=6; i++)
                    if(dices.checkDice(i)) { dice = i; break; }
                if(dice==0) throw new InvalidMoveException();

                dices.useDice(dice);
                removeToken(move.getFromPoint());
            }
            else throw new NotAllTokensHomeException();
        }
        else {
            if (!dices.checkDice(move.getFromPoint() - move.getToPoint())) throw new InvalidMoveException();    //dice with that value exists
            if(move.getFromPoint()==25){
                points[move.getToPoint()].move(takeFromCenter());
            }
            else points[move.getToPoint()].move(points[move.getFromPoint()].get());

            //move is valid, use the dice
            dices.useDice(move.getFromPoint()-move.getToPoint());
        }
        //change player NO NO NO NO NO WHAT IF THE MOVE IS FIRST FOR THE PLAYER
        //onTheMove = 1 - onTheMove;
    }

    //counts total number of tokens in the game and tokens home
    public void countTokens(int onTheMove, AtomicInteger tokensHome, AtomicInteger tokensInGame){
        tokensInGame.addAndGet(center[onTheMove].count());
        for(int i=24; i<=1; i--){
            if (checkToken(points[i].top())) {
                tokensInGame.addAndGet(points[i].count());
                if(i<=6) tokensHome.addAndGet(points[i].count());
            }
        }

    }
    private boolean checkDirection(Move move){
        if((onTheMove == 0 && move.getFromPoint()>move.getToPoint()) || (onTheMove == 1 && move.getFromPoint()<move.getToPoint())) return true;
        return false;
    }

    public void removeToken(int from){
        Token token = points[from].get();
        int tokenColor = token instanceof WhiteToken ? 0 : 1;
        outer[tokenColor].put(token);
    }

    public void putOnCenter(Token token){
        int tokenColor = token instanceof WhiteToken ? 0 : 1;
        center[tokenColor].put(token);
    }

    private Token takeFromCenter(){
        return center[onTheMove].get();
    }

    public boolean checkCenter(){
        int color = onTheMove;
        if(center[color].top()==null) return true;
        else return false;
    }
    public boolean checkCenter(int player){
        if(center[player].top()==null) return true;
        return false;
    }

    private boolean checkToken(Token token){
        if((token instanceof  WhiteToken && onTheMove == 0) || (token instanceof RedToken && onTheMove ==1)) return true;
        return false;

    }
    public boolean checkOpposing(int player){    //if it finds any token in the opposing field, return false, if nothing return true
        if(player == 0) {
            for (int i = 24; i >= 19; i--)
                if (points[i].top() != null) return false;
        }
        else {
            for (int i = 1; i <= 6; i++)
                if (points[i].top() != null) return false;
        }
        return true;
    }

    public void changePlayer(){
        onTheMove=1-onTheMove;
    }

    public Token chosenToken(int point){
        return points[point].top();
    }

    public int whitePoints(){
        int count = 0;
        for(int i=1; i<=24; i++)
            if(points[i].top() instanceof WhiteToken) count += points[i].count()*i;
        count+=center[0].count()*25;
        return count;
    }
    public int redPoints(){
        int count = 0;
        for(int i=1; i<=24; i++)
            if(points[i].top() instanceof RedToken) count += points[i].count()*i;
        count+=center[1].count()*25;
        return count;
    }

    public boolean checkClosed(int player){
        int home = (player==0)?1:19, end = (player==0)?6:24;
        Token token;
        if(player==0) token = new WhiteToken();
        else token = new RedToken();

        for(int i=home; home!=end; home++)
            if(points[i].count() == 0 || !token.sameType(points[i].top()) || points[i].count()==1) return false;
        return true;
    }

    public void tokenPrint(Token token){
        if(token instanceof WhiteToken) System.out.print('w');
        else System.out.print('r');

    }
    public void print(){
        System.out.println("Table");
        for(int i=1; i<=24; i++){
            System.out.print(i+":");
            for(int j=0; j<points[i].count(); j++) tokenPrint(points[i].top());
            System.out.print("\n\n");
        }
        System.out.println("Centers");
        for(int i=0; i<2; i++)
        for(int j=0; j<center[i].count(); j++) tokenPrint(center[i].top());

    }
}