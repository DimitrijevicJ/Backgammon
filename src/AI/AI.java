package AI;

import Game.Dice;
import Moves.Move;
import Table.Table;

/**
 * Created by dz on 21.1.17..
 */
public class AI {
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
        }
    }

    public int choseMove(LEVEL currentTreeLevel, int currentDepth, int totalDepth, Table table, Dice[] dice) {
        //TODO each call of choseMove changes the currentTreeLevel as the currentTreeLevel.next(), currentDepth+1
        //TODO MYMOVE and OPPMOVE change the table, they create new table from the given one and send it to every function call everything dones is with the new table it also changes the dices it uses
        //TODO MYROLL and OPPROLL change the dices as they set them table stays the same
        if (currentTreeLevel == LEVEL.MYMOVE) {
            for (int i = 1; i <= 24; i++) {
                for (int j = 0; j < 2; j++)
                    if (canMove(from = i, to = i - dices[j])) {
                        move(from = i, to = i - dices[j]);
                        int value = choseMove(...)
                        if (value > max) max = value;
                    }
            }
            return max;
        }
        if (currentTreeLevel == LEVEL.OPPMOVE) {
            for (int i = 1; i <= 24; i++) {
                for (int j = 0; j < 2; j++)
                    if (canMove(from = i, to = i - dices[j])) {
                        move(from = i, to = i - dices[j]);
                        int value = choseMove(...)
                        if (value > mIN) mIN = value;
                    }
            }
            return mIN;
        }
        if (currentTreeLevel == LEVEL.MYROLL) {
            for (int i = 1; i <= 6; i++) {
                for (int j = i; j <= 6; j++)
                    dice[0] = i;
                dice[1] = 2;
                int value = choseMOve();
                sum += value;
            }
            return sum / 21;
        }
        if (currentTreeLevel == LEVEL.OPPROLL) {
            for (int i = 1; i <= 6; i++) {
                for (int j = i; j <= 6; j++)
                    dice[0] = i;
                dice[1] = 2;
                int value = choseMOve();
                sum += value;
            }
            return sum / 21;
        }
    }
}
