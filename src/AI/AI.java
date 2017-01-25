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

    class OneMove{
        Move[] moves = new Move[2];
        double ratio;
        OneMove nextBrother=null;
        OneMove firstChild=null, lastChild=null;
        OneMove(Move[] m, int points1, int points2){moves[0]=m[0]; moves[1]=m[1]; ratio = (double)points1/(double)points2;}
        OneMove(Move[] m, double rat){moves[0]=m[0]; moves[1]=m[1]; ratio = rat;}
        void addChild(OneMove child){
            if(firstChild==null)firstChild = lastChild = child;
            else lastChild = lastChild.nextBrother = child;
        }
        void setRatio(double r){ratio = r;}
    }

    public OneMove choseMoves(LEVEL currentTreeLevel, int currentDepth, int totalDepth, OneMove move, Table table, Dice dices) {
        if(currentDepth==totalDepth+1) return move;

        if (currentTreeLevel == LEVEL.MYMOVE) {
            double bestValue = 500;  //for the current player best value is the smallest ratio

            OneMove bestMove = null;  //bestMoves should be return through the function
            Move[] moves = new Move[2];
            moves[0]=moves[1]=null;

            for (int f1 = 25; f1 >= 1; f1--)
                for (int t1 = 24; t1 >= ((f1 - 12 > 0) ? f1 - 12 : 0); t1--)   //from and to for first move chosen
                    for (int f2 = 25; f2 >= 1; f2--)
                        for (int t2 = 24; t2 >= ((f2 - 12 > 0) ? f2 - 12 : 0); t2--)   //from and to for second move chosen
                        {
                            boolean canFirst = true;
                            Table newTable = copyTable(table);
                            Dice newDice = copyDices(dices);

                            moves[1] = new Move(f1, t1);
                            canFirst = tryMove(moves[1], newTable, newDice);
                            //first move try
                            if (canFirst) {
                                moves[2] = new Move(f2, t2);
                                //second move try
                                tryMove(moves[2], newTable, newDice);
                            }

                            OneMove bestSubtree = null;
                            OneMove playedMoves = null;
                            //he played at least something
                            if(canFirst){
                                playedMoves = new OneMove(moves, newTable.whitePoints(), newTable.redPoints());
                                move.addChild(playedMoves);
                                bestSubtree = choseMoves(currentTreeLevel.nextLevel(), currentDepth + 1, totalDepth, playedMoves, newTable, newDice);
                            }


                            if (canFirst && bestSubtree.ratio < bestValue) {
                                bestMove = playedMoves;
                            }

                        }

            //if he made no moves, he had no moves to make, which means he skipped the move and let the opponent play
            if(bestMove == null) {
                bestMove = new OneMove(moves, table.whitePoints(), table.redPoints());
                move.addChild(bestMove);
                bestMove= choseMoves(currentTreeLevel.nextLevel(), currentDepth+1, totalDepth, bestMove, table, dices);
            }
            return bestMove;
        }

        if (currentTreeLevel == LEVEL.OPPMOVE) {
            double worstValue = 500;  //for the current player best value is the smallest ratio

            OneMove worstMove = null;  //bestMoves should be return through the function
            Move[] moves = new Move[2];
            moves[0]=moves[1]=null;


            for (int f1 = 25; f1 >= 1; f1--)
                for (int t1 = 24; t1 >= ((f1 - 12 > 0) ? f1 - 12 : 0); t1--)   //from and to for first move chosen
                    for (int f2 = 25; f2 >= 1; f2--)
                        for (int t2 = 24; t2 >= ((f2 - 12 > 0) ? f2 - 12 : 0); t2--)   //from and to for second move chosen
                        {
                            boolean canFirst = true;
                            Table newTable = copyTable(table);
                            Dice newDice = copyDices(dices);

                            moves[1] = new Move(f1, t1);
                            canFirst = tryMove(moves[1], newTable, newDice);
                            //first move try
                            if (canFirst) {
                                moves[2] = new Move(f2, t2);
                                //second move try
                                tryMove(moves[2], newTable, newDice);
                            }

                            OneMove worstSubtree = null;
                            OneMove playedMoves = null;
                            //he played at least something
                            if(canFirst){
                                playedMoves = new OneMove(moves, newTable.whitePoints(), newTable.redPoints());
                                move.addChild(playedMoves);
                                worstSubtree = choseMoves(currentTreeLevel.nextLevel(), currentDepth + 1, totalDepth, playedMoves, newTable, newDice);
                            }


                            if (canFirst && worstSubtree.ratio > worstValue) {
                                worstMove = playedMoves;
                            }

                        }

            //if he made no moves, he had no moves to make, which means he skipped the move and let the opponent play
            if(worstMove == null) {
                worstMove = new OneMove(moves, table.whitePoints(), table.redPoints());
                move.addChild(worstMove);
                worstMove= choseMoves(currentTreeLevel.nextLevel(), currentDepth+1, totalDepth, worstMove, table, dices);
            }
            return worstMove;
        }


        //I don't need to remember dices, pure luck brought me there
        if (currentTreeLevel == LEVEL.MYROLL || currentTreeLevel == LEVEL.OPPROLL) {
            Move[] moves = new Move[2];
            moves[0]=moves[1]=null;
            OneMove avgValue = new OneMove(moves, table.whitePoints(), table.redPoints());
            double avgRatio = 0;
            for (int i = 1; i <= 6; i++) {
                for (int j = i; j <= 6; j++) {
                    Dice newDice = new Dice(i, j);

                    LEVEL nextTreeLevel = currentTreeLevel;

                    if(dices.isDoubleDices() && !dices.wereDoubleDices()) {
                        newDice.setWereDoubleDices();
                        nextTreeLevel = nextTreeLevel.nextLevel().nextLevel().nextLevel();
                    }
                    else nextTreeLevel = nextTreeLevel.nextLevel();

                    avgRatio += choseMoves(nextTreeLevel, currentDepth + 1, totalDepth, avgValue, table, newDice).ratio;
                }
            }
            moves[0]=moves[1]=null;
            avgValue = new OneMove(moves, avgRatio/21);
            return avgValue;
        }

        return null;    //can't happen, error
    }

    public void playMoves() {
        OneMove chosenMoves = choseMoves(LEVEL.MYMOVE, 0, game.getTreeDepth(), new OneMove(new Move[2], game.table.whitePoints(), game.table.redPoints()), game.table, game.dices);

        try {
            game.table.move(chosenMoves.moves[0], game.dices);

            if(game.checkPat()) return;

            game.table.move(chosenMoves.moves[1], game.dices);
        }catch(Exception e){}
    }
}
