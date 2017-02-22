package Game;

import AI.AI;
import Game.EndGames.Backgammon;
import Game.EndGames.EndGame;
import Game.EndGames.Gammon;
import Game.EndGames.Victory;
import Player.Player;
import Table.Table;
import Player.Human;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dz on 20.1.17..
 */
public class Game extends Thread{


    private Player[] players = new Player[2];
    private Player winner = null;
    private int playerCount = 0;
    private Turn[] turns = new Turn[2];


    public void joinPlayer(Player player) {
        players[playerCount] = player;
        turns[playerCount] = player.getTurn();
        playerCount++;
        synchronized (playersJoined) {
            if (playerCount == 2) playersJoined.notify();
        }
    }

    private int onTheMove = 0;

    private Turn gameTurn = new Turn();
    private Turn playersJoined = new Turn();
    public int waitPlayerReady = 0;

    public Dice dices = new Dice();
    private boolean wereDoubleDices = false;

    public Table table = new Table();

    public EndGame endGame=null;

    private int treeDepth = 10;
    //TODO ADD TREEDEPTH

    public void run(){
        synchronized (playersJoined){
            try{
                playersJoined.wait();
            }catch(InterruptedException e){}
        }

        while(true){
            //TODO FIX SYNCHRONIZATION
            dices.rollDices();

            //notify the player that needs to play
            synchronized (turns[onTheMove]) {
                turns[onTheMove].notify();
            }

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

    public int getTreeDepth(){return treeDepth;}

    public boolean checkPat(){
        if(!table.checkCenter(onTheMove) && !table.checkCenter(getOtherPlayer())){
            if(table.checkClosed(onTheMove) && table.checkClosed(getOtherPlayer())) return true;
        }
        return false;
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

    public static Game game;
    public static void main(String[] args){
        game = new Game();
       // game.table.print();
        game.start();
        Player player1 = new Human(game,0);
        Player player2 = new Human(game,1);
        game.joinPlayer(player1);
        game.joinPlayer(player2);
        player1.start(); player2.start();
    }
}
