package AI;

import Game.Dice;
import Moves.Move;
import Player.Player;
import Table.Table;
import Game.Game;

/**
 * Created by dz on 21.1.17..
 */
public class AI extends Player{
    protected enum LEVEL {
        MYROLL, MYMOVE, OPPROLL, OPPMOVE;

        public LEVEL nextLevel() {
            switch (this) {
                case MYMOVE:
                    return OPPROLL;
                case MYROLL:
                    return MYMOVE;
                case OPPMOVE:
                    return MYROLL;
                case OPPROLL:
                    return OPPMOVE;
            }
            return null;
        }
    }
    public AI(Game game, int col){super(game, col);}


    public Table copyTable(Table table){
        Table newTable = new Table(table);
        return newTable;
    }
    public Dice copyDices(Dice dice){
        Dice newDice = new Dice(dice);
        return newDice;
    }

    boolean tryMove(Move move, Table table, Dice dice){
        if(!table.checkCenter() && move.getFromPoint()!=25 ) return false;
        try{
            table.move(move,dice);
            return true;
        }catch(Exception e){return false;}
    }

    public int choseMove(LEVEL currentTreeLevel, int currentDepth, int totalDepth, Table table, Dice dices) {
        //TODO fix this return it should return current point counts and empty best moves
        if(currentDepth==totalDepth) return 167;

        if (currentTreeLevel == LEVEL.MYMOVE) {
            int maxValue = 0;

            Move[] bestMove = new Move[2];  //bestMoves should be return through the function


            for (int f1 = 25; f1 >= 1; f1--)
                for (int t1 = 24; t1 >= ((f1 - 12 > 0) ? f1 - 12 : 0); t1--)   //from and to for first move chosen
                    for (int f2 = 25; f2 >= 1; f2--)
                        for (int t2 = 24; t2 >= ((f2 - 12 > 0) ? f2 - 12 : 0); t2--)   //from and to for second move chosen
                        {
                            boolean can = true;
                            Table newTable = copyTable(table);
                            Dice newDice = copyDices(dices);

                            Move move1 = new Move(f1, t1);
                            can = tryMove(move1, newTable, newDice);
                            //first move try
                            Move move2 = null;
                            if (can) {
                                move2 = new Move(f2, t2);
                                //second move try
                                tryMove(move2, newTable, newDice);
                            }

                            int bestSubtree = choseMove(currentTreeLevel.nextLevel(), currentDepth + 1, totalDepth, newTable, newDice);

                            //TODO condition
                            if (bestSubtree > maxValue) {
                                maxValue = bestSubtree;
                                bestMove[0] = move1;
                                bestMove[1] = move2;
                            }

                        }

            //TODO fix this, needs to return myValue, oppValue for condition and needs to return combination of best two moves
            return maxValue;
        }

        if (currentTreeLevel == LEVEL.OPPMOVE) {
            int minValue = -1;

            Move[] bestMove = new Move[2];  //bestMoves should be return through the function


            for (int f1 = 25; f1 >= 1; f1--)
                for (int t1 = 24; t1 >= ((f1 - 12 > 0) ? f1 - 12 : 0); t1--)   //from and to for first move chosen
                    for (int f2 = 25; f2 >= 1; f2--)
                        for (int t2 = 24; t2 >= ((f2 - 12 > 0) ? f2 - 12 : 0); t2--)   //from and to for second move chosen
                        {
                            boolean can = true;
                            Table newTable = copyTable(table);
                            Dice newDice = copyDices(dices);

                            Move move1 = new Move(f1, t1);
                            can = tryMove(move1, newTable, newDice);
                            //first move try
                            Move move2 = null;
                            if (can) {
                                move2 = new Move(f2, t2);
                                //second move try
                                tryMove(move2, newTable, newDice);
                            }

                            int bestSubtree = choseMove(currentTreeLevel.nextLevel(), currentDepth + 1, totalDepth, newTable, newDice);

                            //TODO condition
                            if (bestSubtree < minValue) {
                                minValue = bestSubtree;
                                bestMove[0] = move1;
                                bestMove[1] = move2;
                            }

                        }

            //TODO fix this, needs to return myValue, oppValue for condition and needs to return combination of best two moves
            return minValue;
        }

        //TODO dices can't go more than twice the same fix that

        if (currentTreeLevel == LEVEL.MYROLL) {
            int avgValue = 0;
            for (int i = 1; i <= 6; i++) {
                for (int j = i; j <= 6; j++) {
                    Dice newDice = new Dice(i, j);
                    avgValue += choseMove(currentTreeLevel.nextLevel(), currentDepth + 1, totalDepth, table, newDice);
                }
            }
            return avgValue / 21;
        }
        if (currentTreeLevel == LEVEL.OPPROLL) {
            int avgValue = 0;
            for (int i = 1; i <= 6; i++) {
                for (int j = i; j <= 6; j++) {
                    Dice newDice = new Dice(i, j);
                    avgValue += choseMove(currentTreeLevel.nextLevel(), currentDepth + 1, totalDepth, table, newDice);
                }
            }
            return avgValue / 21;
        }

        return 0;
    }
}
