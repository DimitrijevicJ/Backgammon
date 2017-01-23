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
public class Player extends Thread {
    private int points = 167;
    private int tokensHome = 5;
    private int tokensInGame = 15;
    private Turn turn = new Turn();
    protected Game game;
    private int color;

    public Player(Game g, int col){game = g; color=col;}

    public Turn getTurn(){return turn;}
    public int getPoints(){return points;}
    public int getTokensHome(){return tokensHome;}
    public int getTokensInGame(){return tokensInGame;}
    public int getColor(){return color;}


    public void run(){
        try{
            while(true){
                //wait for your turn
                synchronized (turn){
                    turn.wait();
                }

                int moves=2;

                while(moves > 0){
                    try {
                        int from = -1;
                        while (from == -1) {
                            try {
                                from = choseToken();
                            } catch (Exception e) {
                                System.out.print(e);
                            }
                        }
                        //token successfully chosen

                        int to = chosePosition(from);
                        //position chosen and token moved

                        Move move = new Move(from, to);

                        game.table.move(move, game.dices);

                        points -= from - to;

                        --moves;
                    }catch(Exception e){System.out.println(e);}
                }

                //turn played, continue game
                game.getGameTurn().notify();
            }
        }catch (InterruptedException e){
            //let player know if he won or lost
            inform(game.endGame);
        }
    }

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
}
