import java.util.*;

import javax.swing.event.*;

/**
 * An object modeling a game of mancala.
 * @author Team7
 *
 */
public class Model
{
	private Pit[][] pits;
	private Pit[][] backupPits;
	private int[] score;
	private int[] reclaimedScore;
	private int[] numberUndo;
	private int activePlayer;
	private int backupActivePlayer;

	private ArrayList<ChangeListener> listeners;

	private boolean undo;
	private boolean freeTurn;
	private boolean endGame;
	
	private static final int UNDO_MAX = 3;
	/**
	 * Contructs a Mancala object
	 * @param stones number of stones(3 or 4)
	 */
	public Model(int stones)
	{
		pits = new Pit[2][6];
		score = new int[2];
		numberUndo = new int[2];
		backupPits = new Pit[2][];
		listeners = new ArrayList<ChangeListener>();
		endGame = false;

		//Setting stones for the initial screen
		for (int i = 0; i < 2; i++) // i: 0 -> 1
		{
			score[i] = 0;
			numberUndo[i] = 0; //No inital undos available

			for (int j = 0; j < 6; j++) 
			{ // j:0 -> 5
				pits[i][j] = new Pit();
				pits[i][j].setCount(stones); //Setting desired number of stones per pit
			}
		}
		activePlayer = 0;
		undo = false;
		freeTurn = false;
		backupActivePlayer = 0;
		preUndo();
	}

	/**
	 * Move stones on the board
	 * @param player a player
	 * @param pit a pit
	 */
	public void move(int player, int pit)
	{
		if (player != activePlayer) 
		{ 
			//Checks if user is active or not
			throw new IllegalArgumentException("Player not currently active.");
		}
		// Checking if an empty pit was clicked
		if (pits[player][pit].getCount() == 0) 
		{
			return;
		}
		preUndo();
		if (freeTurn) 
		{
			numberUndo[player] = 0;
			freeTurn = false;
		} 
		else if (numberUndo[player] == 0)
		{
			//Resets the next player's undo count
			numberUndo[nextPlayer(player)] = 0;
			backupActivePlayer = player;
		}

		//Allows an undo after first move
		undo = true;

		//Gets number of stones per pit
		int tempStone = pits[player][pit].getCount();

		//Sets number of stone back to 0 after player distribute stones of the pit
		pits[player][pit].setCount(0);

		//Checks if pits have stones before distributing.
		while (tempStone > 0)
		{
			//Move to the next pit
			pit = moveNextPit(pit);

			//If all of the pits have been distributed to, the next pit to be distributed to
			//should be the mancala pit
			if (pit == 0)
			{		
				// Checks for mancala, if so current side has one more move.
				if (player == activePlayer)
				{
					++score[player];
					--tempStone;
					if (tempStone <= 0)
					{
						checkAllPit();
						freeTurn = true;
						update();
						return;
					}
				}
				player = nextPlayer(player);
			}
			pits[player][pit].setCount(pits[player][pit].getCount()+1);
			tempStone-=1;
		}
		endTurn(player, pit);
	}

	/**
	 * Allows player to undo to the previous state of the board
	 */
	public void undo()
	{
		//Checks is player can undo or not
		if(!isUndoable())
		{
			return;
		}

		//Restores all data of pits when user undo
		for (int i = 0; i < 2; i++)
		{
			pits[i] = backupPits[i].clone();
		}
		//Restoring all data of mancalas when user undo
		score = reclaimedScore.clone();
		//increase number of undo time
		numberUndo[backupActivePlayer]++;
		//undo player
		activePlayer = backupActivePlayer;
		//Disable undo option
		undo = false;
		//Disable free turn
		freeTurn = false;
		//Notify change listener
		update();
	}

	/**
	 * Attaches a listener to the mancala to update changes on the board
	 * @param listener a listener to update changes
	 */
	public void attach(ChangeListener listener)
	{
		listeners.add(listener);
		/* Update the newly added Controller */
		update();
	}

	/**
	 * Updates the listeners so the view knows that a change has been made
	 */
	public void update()
	{
		// Commandline output of stones
		/**
		 * 
		 *System.out.print(" ");
		 *for (int p = pits[1].length - 1; p >= 0; p--)
		 *	System.out.print(" " + pits[1][p]);
		 *if (mancalas[1] < 10)
		 *	System.out.print("\n" + mancalas[1] + "             " + mancalas[0] + "\n ");
		 *else
		 *	System.out.print("\n" + mancalas[1] + "           " + mancalas[0] + "\n ");
		 *for (int p = 0; p < pits[0].length; p++)
		 *	System.out.print(" " + pits[0][p]);
		 *System.out.println("\n");
		 */

		for (ChangeListener listener : listeners)
		{
			listener.stateChanged(new ChangeEvent(this));
		}
	}

	/**
	 * Get the player's pits
	 * @return a 2-dimensional array of player pits
	 */
	public Pit[][] getPits()
	{
		return pits.clone();
	}

	/**
	 * Get Mancala
	 * @return a single array of player Mancalas of stones
	 */
	public int[] getMancalas()
	{
		return score.clone();
	}

	/**
	 * Set the status of the board back as before a player makes a selection
	 * of a pit.
	 */
	private void preUndo()
	{
		//copy data from mancalas before user move in to backup mancalas
		reclaimedScore = score.clone();
		//copy data in pits before user move into backup pits
		for (int i = 0; i < 2; i++) 
		{
			backupPits[i] = pits[i].clone();
		}
	}

	/**
	 * Check if the last stone that a player drops is in his own empty pit.
	 * If so, the player will take that stone and all of his opponent's
	 * adjacent stones and put them in his Mancala. Also, checks if one
	 * side of the board is empty
	 * @param player which player
	 * @param pit a pit
	 */
	private void endTurn(int player, int pit)
	{
		/* What happens at the end of a turn? 
		 *	The case of ending in a mancala is handled in move().
		 *	Ending in an empty pit on your own side allows you to
		 *		take that stone and all stones in the adjacent pit.
		 *	Your turn ends when your hand is empty.
		 */
		if (player == activePlayer && pits[player][pit].getCount() == 1)
		{
			score[player] += 1 + pits[nextPlayer(player)][6 - pit - 1].getCount();
			pits[player][pit].setCount(0);
			pits[nextPlayer(player)][6 - pit - 1].setCount(0);
			freeTurn = true;
		}
		else
		{
			activePlayer = nextPlayer(activePlayer);
		}

		//Checking if all pits are empty, then end game
		checkAllPit();

		//Notify change
		update();
	}

	/**
	 * Determines if the game is over
	 * @return whether the game is over or not
	 */
	public boolean isGameEnd()
	{
		return endGame;
	}

	/**
	 * Checks if either side is empty and ends the game
	 */
	private void checkAllPit()
	{
		Pit[][] checkPits = getPits();
		int empty;
		for (int i = 0; i < 2; i++)
		{
			empty = 0;
			for (int j = 0; j < 6; j++)
			{
				if (checkPits[i][j].getCount() == 0)
					empty++;
			}

			if (empty == 6)
			{
				endGame(nextPlayer(i));
				break;
			}
		}
	}

	/**
	 * Empties the pits and moves them to the corresponding mancala
	 * afterwards it sets the winner as the active player
	 */
	private void endGame(int player)
	{
		endGame = true;

		// empties the remainder of the board
		for (int i = 0; i < 6; i++) 
		{
			score[player] += pits[player][i].getCount();
			pits[player][i].setCount(0);
		}

		// sets the winner as the active player
		if (score[activePlayer] == score[nextPlayer(activePlayer)]) 
		{
			activePlayer = -1;
		}
		else if (score[activePlayer] < score[nextPlayer(activePlayer)]) 
		{
			activePlayer = nextPlayer(activePlayer);
		}
	}

	/**
	 * Go to next pit
	 * @param pit a pit
	 * @return the next pit
	 */
	private int moveNextPit(int pit)
	{
		pit+=1; //moving current pit to next pit
		if (pit >= 6) 
		{
			pit = 0; //if current pit is 6, then back to pit 1 (pit : 0 -> 5)
		}
		return pit;
	}

	/**
	 * Get the next size of the players
	 * @param player a size of the players
	 * @return the next size of the current size of the players
	 */
	private int nextPlayer(int player)
	{
		player+=1;//move to next player
		if (player >= 2) 
		{
			player = 0; // if player is > 2 then move back to 0 (player: 0 - > 1)
		}
		return player;
	}

	/**
	 * Gets the active player
	 * @return the active player
	 */
	public int getActive()
	{
		return activePlayer;
	}

	/**
	 * Checks for the active player
	 * @return the string representation of the active player
	 */
	public String getPlayer()
	{
		return "Player "+(activePlayer+1)+"'s Move";
	}

	/**
	 * Checks how many undo moves is possible
	 * @return undo count
	 */
	public int getUndoCount()
	{
		return UNDO_MAX - numberUndo[backupActivePlayer];
	}

	public boolean isUndoable() {
		//Cannot undo at first move of the game.
		//Player already undo on that row
		//Cannot undo when there is no more undo time
		if (isGameEnd() || !undo || (activePlayer == backupActivePlayer && !undo) ||
				(numberUndo[backupActivePlayer] == UNDO_MAX)) 
		{
			return false;
		}
		else 
		{
			return true;
		}
	}
}
