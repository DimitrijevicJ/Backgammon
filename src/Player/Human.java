package Player;

import Game.Game;
import Moves.Move;

/**
 * Created by dz on 25.1.17..
 */
public class Human extends Player {
    public Human(Game g, int col){super(g,col);}

    public void playMoves(){
        System.out.println("Player"+color+"starting the turn");
        int moves=2;

        while(moves > 0){
            try {
                int from = -1;
                while (from == -1) {
                    try {
                        System.out.println("Player"+color+"inputing first token");
                        from = choseToken();
                    } catch (Exception e) {
                        System.out.print(e);
                    }
                }
                System.out.println("Player"+color+"chose the token");
                //token successfully chosen

                System.out.println("Player"+color+"chose where to move the token");
                int to = chosePosition(from);
                System.out.println("Player"+color+"chosen where to move the token");
                //position chosen and token moved

                Move move = new Move(from, to);

                System.out.println("Player"+color+"attempting move");
                game.table.move(move, game.dices);
                System.out.println("Player"+color+"move successful");

                --moves;

                if(game.checkPat()) return;
            }catch(Exception e){System.out.println(e);}
        }
    }
}
