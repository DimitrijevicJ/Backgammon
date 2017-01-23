package Game;

import Game.EndGames.Backgammon;
import Game.EndGames.EndGame;
import Game.EndGames.Gammon;
import Game.EndGames.Victory;
import Player.Player;
import Table.Table;

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
        }
    }

    public Turn getGameTurn(){return gameTurn;}

    public int getOtherPlayer(){ return 1-onTheMove; }

    private EndGame checkEndGameStatus(){
        if(players[onTheMove].getPoints()!=0) return null;

        int losingPlayer = getOtherPlayer();

        //backgammon
        if(!table.checkCenter(losingPlayer) || table.checkOpposing(losingPlayer)) return new Backgammon();

        //gammon
        if (players[losingPlayer].getTokensInGame() == 15
                || players[losingPlayer].getTokensHome() < players[losingPlayer].getTokensInGame()) return new Gammon();

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
