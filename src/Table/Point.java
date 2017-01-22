package Table;

import Tokens.Token;

/**
 * Created by dz on 20.1.17..
 */
public class Point {
    private Token[] tokens = new Token[15];
    private int top=-1;
    private Table table;

    public Point(Table t){ table = t; }

    public boolean checkMove(Token token){
        //putting on empty point
        if (top == -1 || tokens[top].sameType(token) || top == 0) return true;
        else return false;
    }

    public void put(Token token){
        //initialization only
        tokens[++top]=token;
    }

    public void move(Token token){
        if(top == -1 || tokens[top].sameType(token)){
            tokens[++top]=token;
        }
        else {
            table.putOnCenter(tokens[top]);
            tokens[top] = token;
        }
    }

    public Token get(){
        Token token = null;
        if(top != -1) token = tokens[top--];
        return token;
    }

    public Token top(){
        if(top == -1) return null;
        return tokens[top];
    }
}
