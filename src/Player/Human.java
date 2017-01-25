package Player;

import Game.Game;
import Moves.Move;

/**
 * Created by dz on 25.1.17..
 */
public class Human extends Player {
    public Human(Game g, int col){super(g,col);}

    public void playMoves(){
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

                --moves;

                if(game.checkPat()) return;
            }catch(Exception e){System.out.println(e);}
        }
    }
}
