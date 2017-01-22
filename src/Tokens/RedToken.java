package Tokens;

/**
 * Created by dz on 20.1.17..
 */
public class RedToken extends Token{
    @Override
    public boolean sameType(Token token){
        return (token instanceof RedToken)?true:false;
    }
}
