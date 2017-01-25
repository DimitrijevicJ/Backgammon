package Game;

import Game.EndGames.Backgammon;
import Game.EndGames.EndGame;
import Game.EndGames.Gammon;
import Game.EndGames.Victory;
import Player.Player;
import Table.Table;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dz on 20.1.17..
 */
public class Game extends Thread{
    private Player[] players = new Player[2];
    {
        players[0] = new Player(this,0);
        players[1] = new Player(this, 1);
    }
    private Player winner = null;

    private Turn[] turns = new Turn[2];
    {
        turns[0] = players[0].getTurn();
        turns[1] = players[1].getTurn();
    }

    private int onTheMove = 0;

    private Turn gameTurn = new Turn();

    public Dice dices = new Dice();
    private boolean wereDoubleDices = false;

    public Table table = new Table();

    public EndGame endGame=null;

    private int treeDepth = 0;
    //TODO ADD TREEDEPTH

    public void run(){
        players[0].start(); players[1].start();

        while(true){
            //TODO FIX SYNCHRONIZATION
            dices.rollDices();

            //notify the player that needs to play
            turns[onTheMove].notify();

            //wait until the player is done
            synchronized (gameTurn) {
                try {
                    gameTurn.wait();
                } catch (InterruptedException e) { e.printStackTrace(); }
            }

            //check if the player finished the game
            if((endGame = checkEndGameStatus())!=null){
                informPlayers(players[onTheMove],players[getOtherPlayer()]);
                break;
            }

            //change the player
            if(dices.isDoubleDices() && wereDoubleDices == false) wereDoubleDices = true;
            else{
                onTheMove = getOtherPlayer();
                table.changePlayer();
                wereDoubleDices = false;
            }
            //TODO GAME NEEDS TO CHECK FOR THE PAT AND NO CAN PLAY SITUATION
        }
    }

    public Turn getGameTurn(){return gameTurn;}

    public int getOtherPlayer(){ return 1-onTheMove; }

    public int getTreeDepth(){return treeDepth;}

    public boolean checkPat(){
        //TODO DO CHECK PAT FUNCTION
        if(!table.checkCenter(onTheMove) && !table.checkCenter(getOtherPlayer())){
            for(int i=1; i<=6; i++)
                if(table.checkClosed())
        }
        return true;
    }

    private EndGame checkEndGameStatus(){
        if(players[onTheMove].getPoints()!=0) return null;

        int losingPlayer = getOtherPlayer();

        AtomicInteger tokensHome = new AtomicInteger(), tokensInGame = new AtomicInteger();

        //backgammon
        if(!table.checkCenter(losingPlayer) || table.checkOpposing(losingPlayer)) return new Backgammon();

        table.countTokens(losingPlayer, tokensHome, tokensInGame);
        //gammon
        if (tokensInGame.get() == 15
                || tokensHome.get() < tokensInGame.get()) return new Gammon();

        //ordinary victory
        return new Victory();
    }

    private void informPlayers(Player win, Player loose){
        winner = win;
        win.interrupt();
        loose.interrupt();
    }

    public Player getWinner() {return winner;}
}
