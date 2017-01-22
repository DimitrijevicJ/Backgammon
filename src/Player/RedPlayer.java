package Player;

import Game.Game;
import Tokens.RedToken;
import Tokens.Token;

/**
 * Created by dz on 20.1.17..
 */
public class RedPlayer extends Player {
    public RedPlayer(Game game){super(game);}

    protected boolean checkToken(int point){
        Token token = game.table.chosenToken(point);
        if(token==null || !(token instanceof RedToken)) return false;
        return true;
    }
}
