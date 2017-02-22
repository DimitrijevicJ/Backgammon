package Tokens;

/**
 * Created by dz on 20.1.17..
 */
public class WhiteToken extends Token {
    @Override
    public boolean sameType(Token token) {
        return (token instanceof WhiteToken)?true:false;
    }
    public Token clone(){return new WhiteToken();}
}
