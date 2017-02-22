package Player;

import Exceptions.InvalidMoveException;
import Exceptions.InvalidTokenException;
import Exceptions.NotAllTokensHomeException;
import Exceptions.TokenOnCenterException;
import Game.EndGames.EndGame;
import Game.EndGames.Victory;
import Game.Turn;
import Game.Game;
import Moves.Move;
import Tokens.RedToken;
import Tokens.Token;
import Tokens.WhiteToken;

import java.io.IOException;

/**
 * Created by dz on 20.1.17..
 */
public abstract class Player extends Thread {
    private Turn turn = new Turn();
    protected Game game;
    protected int color;

    public Player(Game g, int col){game = g; color=col;}

    public Turn getTurn(){return turn;}
    public int getColor(){return color;}


    public void run(){
        try{
            while(true){
                //wait for your turn
                synchronized (turn){
                    if(color == 0) game.waitPlayerReady=1;
                    System.out.println("Player"+color+"waiting for it's turn");
                    turn.wait();
                }
                System.out.println("Player"+color+"'s turn");

                playMoves();

                //turn played, continue game
                synchronized (game.getGameTurn()) {
                    System.out.println("Player"+color+"done");
                    game.getGameTurn().notify();
                }
            }
        }catch (InterruptedException e){
            //let player know if he won or lost
            inform(game.endGame);
        }
    }

    protected abstract void playMoves();

    int choseToken() throws TokenOnCenterException, InvalidTokenException {
        int token=inputChoseToken();

        //check if there is a token on the center and we haven't chosen it
        if(!checkCenter() && token!=25 ) throw new TokenOnCenterException();
        //check if the token we have chosen is ours
        else if((checkCenter() && token==25) || (checkCenter() && token!=25 && !checkToken(token))) throw new InvalidTokenException();
        return token;
    }

    int chosePosition(int from){
        int to = inputMovePoint();

        return to;
    }

    protected boolean checkCenter()   //true if there is nothing on the center for the current player
    {
        return game.table.checkCenter();
    }

    protected boolean checkToken(int point)   //true if the token on the point we have chosen is ours
    {
        Token token = game.table.chosenToken(point);
        if ((token instanceof RedToken && color == 1 )||(token instanceof WhiteToken && color == 0 )) return true;
        return false;
    }

    //TODO choose token : inputing the number of the point on which is the token(0 for the center); +GUI - clicked token
    private int inputChoseToken() {
        int token = -1;
        while (true) {
            System.out.println("Input the position of a token");
            try {
                token = System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(token<=25 && token>=1) return token;
        }
    }
    //TODO choose token : inputing the number of the point on which is the token(0 for the center); +GUI - clicked token
    private int inputMovePoint() {
        int to = -1;
        while (true) {
            System.out.println("Input the position of a point");
            try {
                to = System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(to<=24 && to>=0) return to;
        }
    }

    private void inform(EndGame endGame){
        if (game.getWinner() == this) System.out.println("You won " + endGame + "!");
        else {
            if(endGame instanceof Victory) System.out.println("You lost!");
            else System.out.println("You have been "+endGame+"d!");
        }
    }

    public int getPoints(){
        if(color == 0) return game.table.whitePoints();
        else return game.table.redPoints();
    }

    /*public static void main(String[] args){
        Player player = new Human(Game.game, 0);
        Game.game.joinPlayer(player);
        player.start();
    }*/
}
