
package guiCluedo.ui;

import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.border.*;
import javax.swing.event.*;
import guiCluedo.game.Board;
import guiCluedo.game.Card;
import guiCluedo.game.Character;
import guiCluedo.game.Player;
import guiCluedo.game.Room;
import guiCluedo.game.Weapon;

public class UI extends javax.swing.JFrame {
	
	private static final long serialVersionUID = 1L;
	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	private Player currentPlayer;
	private Board b;
	private BoardCanvas canvas;
	private static final String MOVE_UP = "move up";
	private static final String MOVE_RIGHT = "move right";
	private static final String MOVE_DOWN = "move down";
	private static final String MOVE_LEFT = "move left";
	
	static JLabel obj1 = new JLabel();

    /**
     * Creates new form UI
     */
    public UI() {
        initComponents();
        b = new Board();
        System.out.println("Board created");
        System.out.println("boardArea.getWidth() = " + boardArea.getWidth());
        canvas = new BoardCanvas(b, boardArea.getWidth(), boardArea.getHeight());
        System.out.println("Canvas created");
        boardArea.add(canvas);
        System.out.println("canvas added");
        playGame(b, 0);
        System.out.println("Game played");
        obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
        obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
        obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
        obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);
        
        obj1.getActionMap().put(MOVE_UP, new MoveAction("Up", currentPlayer, this.canvas));
        obj1.getActionMap().put(MOVE_RIGHT, new MoveAction("Right", currentPlayer, this.canvas));
        obj1.getActionMap().put(MOVE_DOWN, new MoveAction("Down", currentPlayer, this.canvas));
        obj1.getActionMap().put(MOVE_LEFT, new MoveAction("Left", currentPlayer, this.canvas));
        add(obj1);
        
        this.addComponentListener(new ComponentAdapter() 
        {  
                public void componentResized(ComponentEvent evt) {
                    Component c = (Component)evt.getSource();
                    System.out.println("Redrawn");
                    int height = (int) (boardArea.getWidth()*0.44);
                    boardArea.setSize(boardArea.getWidth(), height);
                    System.out.println("Width = " + boardArea.getWidth());
                    System.out.println("Height = " + boardArea.getHeight());
                    canvas.setWidth(boardArea.getWidth());
                    canvas.setHeight(boardArea.getHeight());
                    canvas.repaint();
                }
        });
    }

	private void accusButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void guessButtonActionPerformed(ActionEvent e) {
		// TODO add your code here
	}

	private void endTurnActionPerformed(ActionEvent e) {
		// TODO add your code here
	}
	
	/**
	 * Loop through all the players while the game hasn't been won. If a player
	 * gets eliminated, break the loop then remove the player and start the loop
	 * again from where it left off.
	 * 
	 * @param scan - the scanner used for accessing user input
	 * @param b - The current board
	 * @param playerNum - the prior players number
	 */
	public void playGame(Board b, int playerNum) {

		playerNum = (playerNum % b.getPlayers().size()) + 1; //go to the next player number
		currentPlayer = b.getPlayers().get(playerNum - 1);
		Player eliminatedPlayer = null;
		playerNum = (playerNum % b.getPlayers().size()) + 1;	//go to the next player
		currentPlayer = b.getPlayers().get(playerNum - 1);
//		while (finished == false) {	//While the game has not been won (or lost)
//			Room room = null;
//			for(Room r : b.getRooms()){
//				if(r.getBoundingBox().contains(currentPlayer.getLocation())){
//					room = r; 
//				}
//			}
//			if (room == null) {	//If the player is not within a room.
//
//				if (true) {	//If player chose to make an accusation				
//					ArrayList<Card> guessHand = createGuess(scan, b);//Create the guess hand
//					while (guessHand == null) {
//						guessHand = createGuess(scan, b);
//					}
//					boolean opt = false;//Accusation
//
//					Guess guess = new Guess(opt, guessHand, currentPlayer, b);
//
//					if (guess.getEliminatedPlayer() != null) {
//						eliminatedPlayer = guess.getEliminatedPlayer(); //Set the player to be eliminated 
//						System.out.println("You guessed wrong");
//						System.out.println("You have been eliminated!");
//						
//						if(b.getPlayers().size() == 2)//If there is no one left in the game then exit
//						{
//							System.out.println("");
//							System.out.println("Game over! No one guessed correctly");
//						}
//						
//						break;//Break out
//					} else if (guess.hasWon()) {
//						finished = true;
//						System.out.println("Congratulations " + currentPlayer.getName() + " you have won!");
//						return;
//					}
//
//				}
//
//			} else {	//If the player made it to the room they wanted
//
//				if () {	//If they chose to make either an accusation of a suggestion
//
//					Guess guess = null;
//					do{	//Select the 3 cards that make up the guess					
//						ArrayList<Card> guessHand = createGuess(scan, b);
//						while (guessHand == null) {
//							guessHand = createGuess(scan, b);
//						}
//
//						// create a guess hand
//						boolean opt = false;
//						if () {	//if accusation
//							opt = true;
//						}
//
//						guess = new Guess(opt, guessHand, currentPlayer, b);
//					}while(guess.getFailed() == true);	//if not all 3 were selected
//
//					if (guess.getEliminatedPlayer() != null) {
//						eliminatedPlayer = guess.getEliminatedPlayer();
//						System.out.println("You guessed wrong");
//						System.out.println("You have been eliminated!");
//						
//						if(b.getPlayers().size() == 2)//If there is no one left in the game exit
//						{
//							System.out.println("Game over! No one guessed correctly");
//						}
//						
//						break;
//					} else if (guess.hasWon()) {
//						finished = true;
//						System.out.println("Congratulations " + currentPlayer.getName() + " you have won!");
//						return;
//					}
//
//				}
//			}
//		}

//		playerNum = (playerNum % b.players.size()) - 1;
//		b.players.remove(eliminatedPlayer);
//		while (b.getPlayers().size() > 1) {	//While there is more than 1 player left keep playing
//			playGame(scan, b, playerNum);
//		}
	}
    
    /**
     * 
     */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	// Generated using JFormDesigner Evaluation license - James Barfoote
	private void initComponents() {
		jMenuBar = new JMenuBar();
		fileMenu = new JMenu();
		newGame = new JMenuItem();
		GameMenu = new JMenu();
		jSeparator1 = new JSeparator();
		rollDice = new JButton();
		endTurn = new JButton();
		guessButton = new JButton();
		accusButton = new JButton();
		boardArea = new JLayeredPane();
		handArea = new JLayeredPane();
		yourhandText = new JLabel();
		youRolledText = new JLabel();
		jFrame1 = new JFrame();

		//======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();

		//======== jMenuBar ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");

				//---- newGame ----
				newGame.setText("New Game");
				fileMenu.add(newGame);
			}
			jMenuBar.add(fileMenu);

			//======== GameMenu ========
			{
				GameMenu.setText("Game");
				GameMenu.addMenuListener(new MenuListener() {
					@Override
					public void menuCanceled(MenuEvent e) {}
					@Override
					public void menuDeselected(MenuEvent e) {}
					@Override
					public void menuSelected(MenuEvent e) {
						GameMenuMenuSelected(e);
					}
				});
			}
			jMenuBar.add(GameMenu);
		}
		setJMenuBar(jMenuBar);

		//---- rollDice ----
		rollDice.setText("Roll Dice");
		rollDice.setName("rollDice");
		rollDice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rollDiceActionPerformed(e);
			}
		});

		//---- endTurn ----
		endTurn.setText("End Turn");
		endTurn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				endTurnActionPerformed(e);
			}
		});

		//---- guessButton ----
		guessButton.setText("Guess");
		guessButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guessButtonActionPerformed(e);
			}
		});

		//---- accusButton ----
		accusButton.setText("Accusation");
		accusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accusButtonActionPerformed(e);
			}
		});

		//======== handArea ========
		{
			handArea.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
		}

		//---- yourhandText ----
		yourhandText.setText("Your Hand:");

		//---- youRolledText ----
		youRolledText.setText("You rolled a ");

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(jSeparator1)
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addComponent(rollDice)
						.addComponent(youRolledText))
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(accusButton)
							.addGap(96, 96, 96))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(guessButton, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
								.addComponent(endTurn, GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(yourhandText)
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)))
					.addComponent(handArea, GroupLayout.DEFAULT_SIZE, 115, Short.MAX_VALUE)
					.addContainerGap())
				.addComponent(boardArea, GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addComponent(boardArea, GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(jSeparator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(rollDice)
								.addComponent(endTurn))
							.addGroup(contentPaneLayout.createParallelGroup()
								.addGroup(contentPaneLayout.createSequentialGroup()
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
										.addComponent(guessButton)
										.addComponent(yourhandText))
									.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
									.addComponent(accusButton))
								.addGroup(contentPaneLayout.createSequentialGroup()
									.addGap(18, 18, 18)
									.addComponent(youRolledText))))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGap(11, 11, 11)
							.addComponent(handArea, GroupLayout.DEFAULT_SIZE, 71, Short.MAX_VALUE)
							.addGap(16, 16, 16)))
					.addContainerGap())
		);
		pack();
		setLocationRelativeTo(getOwner());

		//======== jFrame1 ========
		{
			jFrame1.setAlwaysOnTop(true);
			Container jFrame1ContentPane = jFrame1.getContentPane();

			GroupLayout jFrame1ContentPaneLayout = new GroupLayout(jFrame1ContentPane);
			jFrame1ContentPane.setLayout(jFrame1ContentPaneLayout);
			jFrame1ContentPaneLayout.setHorizontalGroup(
				jFrame1ContentPaneLayout.createParallelGroup()
					.addGap(0, 400, Short.MAX_VALUE)
			);
			jFrame1ContentPaneLayout.setVerticalGroup(
				jFrame1ContentPaneLayout.createParallelGroup()
					.addGap(0, 300, Short.MAX_VALUE)
			);
		}
    }// </editor-fold>//GEN-END:initComponents

    private void rollDiceActionPerformed(java.awt.event.ActionEvent evt) {
    	int roll = ((int) Math.ceil(Math.random()*11)) + 1; // generate a random number between 2 and 12 inclusive
		currentPlayer.setRoll(roll);
		System.out.println(roll);
    }

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void GameMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_GameMenuMenuSelected
        // TODO add your handling code here:
    }//GEN-LAST:event_GameMenuMenuSelected
    

//    private javax.swing.JMenu GameMenu;
//    private javax.swing.JLabel diceRolled;
//    private javax.swing.JMenu fileMenu;
//    private static javax.swing.JFrame jFrame1;
//    private javax.swing.JMenuBar jMenuBar;
//    private javax.swing.JSeparator jSeparator1;
//    private javax.swing.JLabel yourHandText;
//    private javax.swing.JMenuItem newGame;
//    private javax.swing.JButton rollDice; 
   // private javax.swing.JLayeredPane boardArea;
    //private javax.swing.JLayeredPane handArea;


	/**
	 * Creates a guess for the specified board. A guess consists of 3 cards used for
	 * either a suggestion or an accusation
	 * @param scan - The scanner used for accessing user input
	 * @param b - The current board
	 * @return The list of 3 cards to be used in the suggestion/accusation.
	 */
	private static ArrayList<Card> createGuess(Scanner scan, Board b) {
		String roomName = scan.next();
		int index = b.getRoomNames().indexOf(roomName);
		Room guessRoom = null;
		if (index != -1) {
			guessRoom = b.getRooms().get(index);
		} else {
			System.out.println("Room name was incorrect, please type the 3 cards again");
			return null;
		}

		int indexW = b.getWeaponNames().indexOf(scan.next());
		Weapon guessWeapon = null;
		if (indexW != -1) {
			guessWeapon = b.getWeapons().get(indexW);
		} else {
			System.out.println("Weapon name was incorrect, please type the 3 cards again");
			return null;
		}

		String characterN = scan.next();
		int indexC = b.getCharacterNames().indexOf(characterN);
		Character guessCharacter = null;
		if (indexC != -1) {
			guessCharacter = b.getCharacters().get(indexC);
		} else {
			System.out.println("Character name was incorrect, please type the 3 cards again");
			return null;
		}

		ArrayList<Card> guessHand = new ArrayList<Card>();
		guessHand.add(guessRoom);
		guessHand.add(guessWeapon);
		guessHand.add(guessCharacter);

		return guessHand;

	}
	
	/**
	 * Checks if the given string can be parsed to an integer.
	 * @param s - The string to be parsed
	 * @return boolean of whether it is or not
	 */
	private static boolean isInteger(String s) {
		  try { 
		      Integer.parseInt(s); 
		   } catch(NumberFormatException e) { 
		      return false; 
		   }
		   // only got here if we didn't return false
		   return true;
		}
	/**
	 * Returns a number the user specifies given it's valid.
	 * 
	 * @param scan - The scanner used for accessing user input
	 * @param minNum - The minimum possible value
	 * @param maxNum - The maximum possible number
	 * @param num - The number to be checked.
	 * @return the integer the player gave
	 */
	private static int isCorrectNumber(Scanner scan, int minNum, int maxNum, String num){
		int numPlayers = 0;
		while(true){
			if(isInteger(num)){
				numPlayers = Integer.parseInt(num); 
				if((numPlayers >= minNum) && (numPlayers<=maxNum)){
					break;
				}
			}
			System.out.println("Input must be between " +  minNum + " and " + maxNum);
			num = scan.next();
		}
		return numPlayers;
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - James Barfoote
	private JMenuBar jMenuBar;
	private JMenu fileMenu;
	private JMenuItem newGame;
	private JMenu GameMenu;
	private JSeparator jSeparator1;
	private JButton rollDice;
	private JButton endTurn;
	private JButton guessButton;
	private JButton accusButton;
	private JLayeredPane boardArea;
	private JLayeredPane handArea;
	private JLabel yourhandText;
	private JLabel youRolledText;
	private JFrame jFrame1;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}