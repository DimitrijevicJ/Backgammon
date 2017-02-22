package Tokens;

/**
 * Created by dz on 20.1.17..
 */
public abstract class Token {
    public abstract boolean sameType(Token token);
    public abstract Token clone();
}
