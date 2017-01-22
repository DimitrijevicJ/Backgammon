package Player;

import Game.Game;
import Tokens.Token;
import Tokens.WhiteToken;

/**
 * Created by dz on 20.1.17..
 */
public class WhitePlayer extends Player {
    public WhitePlayer(Game game){super(game);}

    protected boolean checkToken(int point){
        Token token = game.table.chosenToken(point);
        if(token==null || !(token instanceof WhiteToken)) return false;
        return true;
    }
}
