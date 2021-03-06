
package guiCluedo.ui;

import java.awt.*;
import java.awt.Component;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import guiCluedo.game.Board;
import guiCluedo.game.Card;
import guiCluedo.game.Character;
import guiCluedo.game.Guess;
import guiCluedo.game.Player;
import guiCluedo.game.Room;
import guiCluedo.game.Weapon;

public class UI extends javax.swing.JFrame implements KeyListener{

	private static final long serialVersionUID = 1L;
	private static final int IFW = JComponent.WHEN_IN_FOCUSED_WINDOW;
	private Player currentPlayer;
	public Board b;
	private BoardCanvas canvas;
	private HandCanvas hCanvas;
	private static final String MOVE_UP = "move up";
	private static final String MOVE_RIGHT = "move right";
	private static final String MOVE_DOWN = "move down";
	private static final String MOVE_LEFT = "move left";
	private static final String VK_N = "next turn";
	private static final String VK_S = "make suggestion";
	private static final String VK_A = "make accusation";
	private static final String VK_NG = "new game";
	private boolean isGuess = true;
	private Card selectedCard;
	private Player previousPlayer;
	private ArrayList<Card> guessHand;
	private boolean infiniteMove = false;

	static JLabel obj1 = new JLabel();
	static JLabel shortCuts = new JLabel();


	/**
	 * Creates new form UI
	 */
	public UI(ArrayList<Player> players) {	
		initComponents();
		b = new Board(players);

		playGame(b, 0);
		this.addKeyListener(this);
		canvas = new BoardCanvas(b, boardArea.getWidth(), boardArea.getHeight());
		hCanvas = new HandCanvas(b, handArea.getWidth(), handArea.getHeight(), currentPlayer);
		handArea.add(hCanvas);
		boardArea.add(canvas);

		keyBindings();

		this.addComponentListener(new ComponentAdapter() 
		{  
			public void componentResized(ComponentEvent evt) {
				Component c = (Component)evt.getSource();
				boardArea.setSize(boardArea.getWidth(), boardArea.getHeight());
				handArea.setSize(handArea.getWidth(), handArea.getHeight());
				canvas.setWidth(boardArea.getWidth());
				hCanvas.setWidth(handArea.getWidth());
				canvas.setHeight(boardArea.getHeight());
				hCanvas.setHeight(handArea.getHeight());
				canvas.repaint();
				hCanvas.repaint();
			}
		});
	}

	/**
	 * Binds the given keyboard inputs to actions so pressing the key calls
	 * a new move action.
	 */
	private void keyBindings() {
		//Arrow keys
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("UP"), MOVE_UP);
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("RIGHT"), MOVE_RIGHT);
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("DOWN"), MOVE_DOWN);
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("LEFT"), MOVE_LEFT);

		//Next turn key (press N)
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("N"), VK_N);
		obj1.getActionMap().put(VK_N, new MoveAction("N", currentPlayer, this.canvas, b, this));

		//Suggestion button (S)
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("S"), VK_S);
		obj1.getActionMap().put(VK_S, new MoveAction("S", currentPlayer, this.canvas, b, this));

		//Accusation button (A)
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("A"), VK_A);
		obj1.getActionMap().put(VK_A, new MoveAction("A", currentPlayer, this.canvas, b, this));

		//New Game button (G)
		obj1.getInputMap(IFW).put(KeyStroke.getKeyStroke("G"), VK_NG);
		obj1.getActionMap().put(VK_NG, new MoveAction("G", currentPlayer, this.canvas, b, this));

		obj1.getActionMap().put(MOVE_UP, new MoveAction("Up", currentPlayer, this.canvas, b, this));
		obj1.getActionMap().put(MOVE_RIGHT, new MoveAction("Right", currentPlayer, this.canvas, b, this));
		obj1.getActionMap().put(MOVE_DOWN, new MoveAction("Down", currentPlayer, this.canvas, b, this));
		obj1.getActionMap().put(MOVE_LEFT, new MoveAction("Left", currentPlayer, this.canvas, b, this));
		add(obj1);
	}

	/**
	 * Executes when accusation button is pressed
	 * @param e
	 */
	public void accusButtonActionPerformed(ActionEvent e) {
		isGuess = false;
		guessRoom.setEnabled(true);
		guessDialoge.setVisible(true);
		accusButton.setEnabled(false);
	}

	/**
	 * Executes when suggestion button is pressed
	 * @param e
	 */
	public void guessButtonActionPerformed(ActionEvent e) {
		isGuess = true;
		guessRoom.setEnabled(false);
		if(findContainingRoom(b) == null){
			errorDialog.setVisible(true);
			return;
		}
		guessDialoge.setVisible(true);
		guessButton.setEnabled(false);
	}

	/**
	 * Executes when end turn button is pressed
	 * @param e
	 */
	public void endTurnActionPerformed(ActionEvent e) {
		playGame(b, currentPlayer.getNum());
		youRolledText.setText("You rolled 0");
		//HandCanvas h = new HandCanvas(b, handArea.getWidth(), handArea.getHeight(), currentPlayer);
		rollDice.setEnabled(true);
		hCanvas.setHand(currentPlayer);
		this.hCanvas.repaint();

	}

	/**
	 * Executes when the ok button is pressed within a suggestion or accusation
	 * Creates a new guess will end the game if accusation is correct.
	 * If accusation is wrong, will eliminate the player who made the accusation.
	 * If player is eliminated and only one player remains, that player wins.
	 * @param e
	 */
	private void guessOKButtonActionPerformed(ActionEvent e) {
		if(isGuess)
		{
			//Suggestion logic
			String room = findContainingRoom(b);
			String character = guessCharacter.getSelectedItem().toString();
			String weapon = guessWeapon.getSelectedItem().toString();

			//Make cards from these and add to arraylist
			ArrayList<String> cards = new ArrayList<String>();
			cards.add(room);
			cards.add(weapon);
			cards.add(character);
			ArrayList<Card> guessHand = createGuess(cards, b);
			//Pass Arraylist to the guess class
			Guess g = new Guess(true, guessHand, currentPlayer, b);
			g.moveIcons(guessHand, b);
			Player previousPlayer = currentPlayer;
			if(checkHasCard(guessHand) == null)
			{
				guessDialoge.setVisible(false);
				//Display Box saying that no one has any cards
				errorDialog.setTitle("Suggestion");
				errorDialog.setVisible(true);
				errorText1.setText("No other players have any of the suggested cards");
				errorText2.setText("");
				return;

			}
			else if(checkHasCard(guessHand) != null)//If someone has a card that matches
			{
				currentPlayer = checkHasCard(guessHand);
				canvas.repaint();
				guessDialoge.setVisible(false);

				//Call method that iterates over all the players and finds the first
				//one with a matching card from the guess hand
				//Then displays the popup asking for them to click on a card in their hand that matches
				guessDiagPlayerNameText.setText(currentPlayer.getName());
				line1Text.setText(previousPlayer.getName() + " guessed these three cards:");
				line2Text.setText(guessHand.get(0).getName() + ", " + guessHand.get(1).getName() + " and " + guessHand.get(2).getName());
				line3Text.setText("Please select one of these cards from your hand then click ok");
				hCanvas.setHand(currentPlayer);
				hCanvas.repaint();
				suggestionDialog.setVisible(true);
				suggestionDialog.setAlwaysOnTop(true);
				this.previousPlayer = previousPlayer;
				this.guessHand = guessHand;
			}
		}
		else
		{
			//Accusation logic
			String room = guessRoom.getSelectedItem().toString();;
			String character = guessCharacter.getSelectedItem().toString();
			String weapon = guessWeapon.getSelectedItem().toString();

			//Make cards from these and add to arraylist
			ArrayList<String> cards = new ArrayList<String>();
			cards.add(room);
			cards.add(weapon);
			cards.add(character);
			ArrayList<Card> guessHand = createGuess(cards, b);
			//Pass Arraylist to the guess class
			Guess g = new Guess(false, guessHand, currentPlayer, b);
			guessDialoge.setVisible(false);
			if(g.getEliminatedPlayer()!=null){
				b.players.remove(g.getEliminatedPlayer());
				canvas.repaint();
				playGame(b, currentPlayer.getNum());
				rollDice.setEnabled(true);
				hCanvas.setHand(currentPlayer);
				this.hCanvas.repaint();

				//Show message that you have been eliminated
				errorDialog.setTitle("YOU HAVE BEEN ELIMINATED!!");
				errorText1.setText(currentPlayer.getName() + " you have been eliminated!");
				errorText2.setText("");
				errorDialog.setVisible(true);



				if(b.getPlayers().size() == 1){

					try {
						Thread.sleep(4000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}



					//Show message that you have Won
					errorDialog.setTitle("YOU HAVE WON!!");
					errorText1.setText(currentPlayer.getName() + " you have won the game!");
					errorText2.setText("Congratulations!");
					errorDialog.setVisible(true);
					this.setVisible(false);
				}
			}
			else{
				boolean won = true;
				for(Card card : guessHand){
					if(!b.getAnswer().contains(card)){
						won = false;
					}
				}
				if(won == true){
					errorDialog.setTitle("YOU HAVE WON!!");
					errorText1.setText(currentPlayer.getName() + " you have won the game!");
					errorText2.setText("Congratulations!");
					errorDialog.setVisible(true);
					this.setVisible(false);
				}
			}
		}


	}

	/**
	 * Iterate clockwise from the current player trying to find a player who has one of the suggested cards
	 * @param guess
	 * @return
	 */
	private Player checkHasCard(ArrayList<Card> guess){
		for (Card card : guess) {
			int playerNum = currentPlayer.getNum();
			playerNum = (playerNum % b.players.size()) + 1;
			int start = currentPlayer.getNum();
			while (playerNum != start) {	//iterate over all the other players
				ArrayList<Card> cards = b.players.get(playerNum-1).getHand();
				for (Card c : cards) {	//Check the hand of all the other players
					if (c.equals(card)) { //If a player has one of the suggested cards in their hand
						return b.players.get(playerNum-1);
					}
				}
				playerNum = (playerNum % b.players.size()) + 1;
			}
		}
		return null;
	}

	/**
	 * Find the room the current player is in if any
	 * @param b
	 * @return
	 */
	public String findContainingRoom(Board b)
	{
		double x = currentPlayer.getLocation().getX();
		double y = currentPlayer.getLocation().getY();

		for(Room r: b.getRooms())
		{
			Polygon poly = r.getBoundingBox();
			if(poly.contains(x, y))
			{
				return r.getName();
			}
		}

		return null;
	}

	/**
	 * creates a new guess given the 3 cards the player selected
	 * @param cards
	 * @param b
	 * @return
	 */
	private static ArrayList<Card> createGuess(ArrayList<String> cards, Board b) {
		ArrayList<Card> guessHand = new ArrayList<Card>();
		for(int i = 0; i < 3; i++)
		{
			if(i == 0){
				String roomName = cards.get(i);
				int index = b.getRoomNames().indexOf(roomName);
				Room guessRoom = b.getRooms().get(index);
				guessHand.add(guessRoom);
			}
			else if(i == 1){
				String weaponName = cards.get(i);
				int indexW = b.getWeaponNames().indexOf(weaponName);
				Weapon guessWeapon = b.getWeapons().get(indexW);
				guessHand.add(guessWeapon);
			}
			else if(i == 2)
			{
				String characterN = cards.get(i);
				int indexC = b.getCharacterNames().indexOf(characterN);
				Character guessCharacter = b.getCharacters().get(indexC);
				guessHand.add(guessCharacter);
			}
		}
		return guessHand;

	}

	/**
	 * Executes when player hits ok on an error dialog
	 * (Closes the error dialog)
	 * @param e
	 */
	private void errorOKActionPerformed(ActionEvent e) {
		errorDialog.setVisible(false);
		guessDialoge.setVisible(false);
	}

	/**
	 * Executes when the new game button is pressed.
	 * @param e
	 */
	public void newGameActionPerformed(ActionEvent e) {
		b.players.clear();
		startScreen sc = new startScreen();
		sc.startScreenForm.setVisible(true);
		this.setVisible(false);
	}

	private void handAreaMouseClicked(MouseEvent e) {
		// TODO add your code here
	}

	/**
	 * Diplays the shortcuts when the button is pressed
	 * @param e
	 */
	private void showShortcutsActionPerformed(ActionEvent e) {
		shortcuts.setVisible(true);
	}

	/**
	 * Executes when show discovered cards is pressed.
	 * @param e
	 */
	private void discoveredCardsmenuItemActionPerformed(ActionEvent e) {
		//Display Discovered cards dialog
		discoveredCardsDiag.setVisible(true);
		//Put the cards in the hand
		hCanvas.setHandCards(currentPlayer.getDiscoveredCards());
		hCanvas.repaint();

	}

	/**
	 * Toggle answer cheat
	 * @param e
	 */
	private void cheatAnswerItemStateChanged(ItemEvent e) {
		if(e.getStateChange() == 1)
		{
			//Display answer popup box
			errorDialog.setTitle("ANSWER");
			errorText1.setText("The 3 answer cards are:");
			errorText2.setText(b.getAnswer().get(0).getName() + ", " + b.getAnswer().get(1).getName() + " and " + b.getAnswer().get(2).getName());
			errorDialog.setVisible(true);
			errorDialog.setAlwaysOnTop(true);
		}
		else
		{
			errorDialog.setVisible(false);
		}
	}

	private void guessDiagOkButtonActionPerformed(ActionEvent e) {
		this.selectedCard = hCanvas.getSelectedCard();
		if(this.selectedCard != null){
			for(Card card : guessHand){
				if(selectedCard.equals(card)){
					suggestionDialog.setVisible(false);
					if(!previousPlayer.getDiscoveredCards().contains(card))
					{
						previousPlayer.addToDiscoveredCards(card);
					}
					currentPlayer = previousPlayer;
					suggestionDialog.setVisible(false);
					hCanvas.setHand(currentPlayer);
					hCanvas.repaint();
				}
			}
		}
	}

	private void shortcutOKActionPerformed(ActionEvent e) {
		shortcuts.setVisible(false);
	}

	private void disOKButtonActionPerformed(ActionEvent e) {
		discoveredCardsDiag.setVisible(false);
		hCanvas.setHandCards(currentPlayer.getHand());
		hCanvas.repaint();
	}

	/**
	 * toggle infinite movement
	 * @param e
	 */
	private void cheatInfiniteMoveItemStateChanged(ItemEvent e) {
		if(e.getStateChange() == 1)
		{
			infiniteMove = true;
		}
		else
		{
			infiniteMove = false;
		}
	}

	private void checkBoxMenuItem1ItemStateChanged(ItemEvent e) {
		// TODO add your code here
	}

	/**
	 * Display the answer
	 * @param e
	 */
	private void cheatAnswerActionPerformed(ActionEvent e) {
		//Display answer popup box
		errorDialog.setTitle("ANSWER");
		errorText1.setText("The 3 answer cards are:");
		errorText2.setText(b.getAnswer().get(0).getName() + ", " + b.getAnswer().get(1).getName() + " and " + b.getAnswer().get(2).getName());
		errorDialog.setVisible(true);
		errorDialog.setAlwaysOnTop(true);
	}


	/**
	 * Sets current player to the next player
	 * @param b - The current board
	 * @param playerNum - the prior players number
	 */
	public void playGame(Board b, int playerNum) {
		guessButton.setEnabled(true);
		accusButton.setEnabled(true);
		playerNum = (playerNum % b.getPlayers().size()) + 1; //go to the next player number

		currentPlayer = b.getPlayers().get(playerNum - 1);
		playerTurnText.setText(currentPlayer.getName() + " / " + currentPlayer.getCharacterName());

		keyBindings();
	}

	/**
	 * Initialises the buttons, frame, labels and pane
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	// Generated using JFormDesigner Evaluation license - James Barfoote
	private void initComponents() {
		jMenuBar = new JMenuBar();
		fileMenu = new JMenu();
		newGame = new JMenuItem();
		showShortcuts = new JMenuItem();
		GameMenu = new JMenu();
		discoveredCardsmenuItem = new JMenuItem();
		menu1 = new JMenu();
		cheatInfiniteMove = new JCheckBoxMenuItem();
		cheatAnswer = new JMenuItem();
		rollDice = new JButton();
		endTurn = new JButton();
		guessButton = new JButton();
		accusButton = new JButton();
		boardArea = new JLayeredPane();
		handArea = new JLayeredPane();
		yourhandText = new JLabel();
		youRolledText = new JLabel();
		playerTurnText = new JLabel();
		separator1 = new JSeparator();
		guessDialoge = new Dialog(this);
		guessOKButton = new JButton();
		guessWeapon = new JComboBox();
		guessCharacter = new JComboBox();
		guessRoom = new JComboBox();
		label2 = new JLabel();
		label1 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		errorDialog = new JDialog();
		errorText1 = new JLabel();
		errorOK = new JButton();
		errorText2 = new JLabel();
		suggestionDialog = new JDialog();
		line1Text = new JLabel();
		line2Text = new JLabel();
		line3Text = new JLabel();
		guessDiagOkButton = new JButton();
		guessDiagPlayerNameText = new JLabel();
		shortcuts = new JDialog();
		label8 = new JLabel();
		label9 = new JLabel();
		label10 = new JLabel();
		label11 = new JLabel();
		label12 = new JLabel();
		shortcutOK = new JButton();
		discoveredCardsDiag = new JDialog();
		dis1 = new JLabel();
		dis2 = new JLabel();
		dis3 = new JLabel();
		disOKButton = new JButton();
		
		guessWeapon.addItem("Knife");
		guessWeapon.addItem("Revolver");
		guessWeapon.addItem("Lead Pipe");
		guessWeapon.addItem("Rope");
		guessWeapon.addItem("Candlestick");
		guessWeapon.addItem("Wrench");

		guessCharacter.addItem("Colonel Mustard");
		guessCharacter.addItem("Miss Scarlet");
		guessCharacter.addItem("Mrs. White");
		guessCharacter.addItem("Mrs. Peacock");
		guessCharacter.addItem("Mr. Green");
		guessCharacter.addItem("Professor Plum");

		guessRoom.addItem("Ballroom");
		guessRoom.addItem("Billard Room");
		guessRoom.addItem("Conservatory");
		guessRoom.addItem("Dining Room");
		guessRoom.addItem("Hall");
		guessRoom.addItem("Kitchen");
		guessRoom.addItem("Library");
		guessRoom.addItem("Lounge");
		guessRoom.addItem("Study");


		//======== this ========
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Cluedo");
		Container contentPane = getContentPane();

		//======== jMenuBar ========
		{

			//======== fileMenu ========
			{
				fileMenu.setText("File");

				//---- newGame ----
				newGame.setText("New Game");
				newGame.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						newGameActionPerformed(e);
					}
				});
				fileMenu.add(newGame);

				//---- showShortcuts ----
				showShortcuts.setText("Show shortcuts");
				showShortcuts.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showShortcutsActionPerformed(e);
					}
				});
				fileMenu.add(showShortcuts);
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

				//---- discoveredCardsmenuItem ----
				discoveredCardsmenuItem.setText("Show discovered cards");
				discoveredCardsmenuItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						discoveredCardsmenuItemActionPerformed(e);
						discoveredCardsmenuItemActionPerformed(e);
					}
				});
				GameMenu.add(discoveredCardsmenuItem);
			}
			jMenuBar.add(GameMenu);

			//======== menu1 ========
			{
				menu1.setText("Cheats");

				//---- cheatInfiniteMove ----
				cheatInfiniteMove.setText("Infinite move");
				cheatInfiniteMove.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						checkBoxMenuItem1ItemStateChanged(e);
						cheatInfiniteMoveItemStateChanged(e);
						cheatInfiniteMoveItemStateChanged(e);
					}
				});
				menu1.add(cheatInfiniteMove);

				//---- cheatAnswer ----
				cheatAnswer.setText("Show answer");
				cheatAnswer.addItemListener(new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent e) {
						cheatAnswerItemStateChanged(e);
						cheatAnswerItemStateChanged(e);
						cheatAnswerItemStateChanged(e);
					}
				});
				cheatAnswer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						cheatAnswerActionPerformed(e);
					}
				});
				menu1.add(cheatAnswer);
			}
			jMenuBar.add(menu1);
		}
		setJMenuBar(jMenuBar);

		//---- rollDice ----
		rollDice.setText("Roll Dice");
		rollDice.setName("rollDice");
		rollDice.setToolTipText("Roll the dice so that you can move");
		rollDice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				rollDiceActionPerformed(e);
			}
		});

		//---- endTurn ----
		endTurn.setText("End Turn");
		endTurn.setToolTipText("End your turn so that the next player can go");
		endTurn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				endTurnActionPerformed(e);
			}
		});

		//---- guessButton ----
		guessButton.setText("Suggestion");
		guessButton.setToolTipText("Make a guess as to what cards you think are the answer");
		guessButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				guessButtonActionPerformed(e);
			}
		});

		//---- accusButton ----
		accusButton.setText("Accusation");
		accusButton.setToolTipText("If you are sure what the answer if click me the make your accusation");
		accusButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				accusButtonActionPerformed(e);
			}
		});

		//======== boardArea ========
		{
			boardArea.setDoubleBuffered(true);
		}

		//======== handArea ========
		{
			handArea.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
			handArea.setDoubleBuffered(true);
			handArea.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					handAreaMouseClicked(e);
				}
			});
		}

		//---- yourhandText ----
		yourhandText.setText("Your Hand:");

		//---- youRolledText ----
		youRolledText.setText("You rolled a ");

		//---- playerTurnText ----
		playerTurnText.setText("Hello it is your turn");
		playerTurnText.setFont(new Font("Tahoma", Font.BOLD, 12));

		GroupLayout contentPaneLayout = new GroupLayout(contentPane);
		contentPane.setLayout(contentPaneLayout);
		contentPaneLayout.setHorizontalGroup(
			contentPaneLayout.createParallelGroup()
				.addComponent(boardArea)
				.addGroup(contentPaneLayout.createSequentialGroup()
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addGroup(contentPaneLayout.createParallelGroup()
								.addComponent(youRolledText)
								.addComponent(rollDice, GroupLayout.PREFERRED_SIZE, 93, GroupLayout.PREFERRED_SIZE)
								.addComponent(playerTurnText, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPaneLayout.createParallelGroup()
								.addComponent(endTurn, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE)
								.addComponent(accusButton)
								.addGroup(contentPaneLayout.createSequentialGroup()
									.addComponent(guessButton)
									.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
									.addComponent(yourhandText)))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(handArea))
						.addGroup(contentPaneLayout.createSequentialGroup()
							.addComponent(separator1, GroupLayout.PREFERRED_SIZE, 444, GroupLayout.PREFERRED_SIZE)
							.addGap(0, 0, Short.MAX_VALUE)))
					.addGap(10, 10, 10))
		);
		contentPaneLayout.setVerticalGroup(
			contentPaneLayout.createParallelGroup()
				.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
					.addComponent(boardArea, GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addComponent(separator1, GroupLayout.PREFERRED_SIZE, 2, GroupLayout.PREFERRED_SIZE)
					.addGroup(contentPaneLayout.createParallelGroup()
						.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
							.addGap(10, 10, 10)
							.addComponent(endTurn)
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addGroup(contentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(guessButton)
								.addComponent(yourhandText))
							.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
							.addComponent(accusButton)
							.addContainerGap())
						.addGroup(contentPaneLayout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, contentPaneLayout.createSequentialGroup()
								.addGap(6, 6, 6)
								.addComponent(playerTurnText, GroupLayout.PREFERRED_SIZE, 16, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(youRolledText)
								.addGap(26, 26, 26)
								.addComponent(rollDice, GroupLayout.PREFERRED_SIZE, 28, GroupLayout.PREFERRED_SIZE)
								.addGap(6, 6, 6))
							.addGroup(contentPaneLayout.createSequentialGroup()
								.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(handArea, GroupLayout.PREFERRED_SIZE, 81, GroupLayout.PREFERRED_SIZE)
								.addContainerGap()))))
		);
		pack();
		setLocationRelativeTo(getOwner());

		//======== guessDialoge ========
		{
			guessDialoge.setTitle("Suggestion / Accusation");

			//---- guessOKButton ----
			guessOKButton.setText("OK");
			guessOKButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					guessOKButtonActionPerformed(e);
				}
			});

			//---- label2 ----
			label2.setText("Make a Guess by selecting one from each of the drop down boxes");

			//---- label1 ----
			label1.setText("Weapon");

			//---- label3 ----
			label3.setText("Character");

			//---- label4 ----
			label4.setText("Room");

			GroupLayout guessDialogeLayout = new GroupLayout(guessDialoge);
			guessDialoge.setLayout(guessDialogeLayout);
			guessDialogeLayout.setHorizontalGroup(
				guessDialogeLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, guessDialogeLayout.createSequentialGroup()
						.addContainerGap(70, Short.MAX_VALUE)
						.addGroup(guessDialogeLayout.createParallelGroup()
							.addGroup(GroupLayout.Alignment.TRAILING, guessDialogeLayout.createSequentialGroup()
								.addComponent(guessOKButton, GroupLayout.PREFERRED_SIZE, 145, GroupLayout.PREFERRED_SIZE)
								.addGap(143, 143, 143))
							.addGroup(GroupLayout.Alignment.TRAILING, guessDialogeLayout.createSequentialGroup()
								.addComponent(label2)
								.addGap(43, 43, 43))))
					.addGroup(GroupLayout.Alignment.TRAILING, guessDialogeLayout.createSequentialGroup()
						.addContainerGap()
						.addComponent(guessWeapon, GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE)
						.addGap(27, 27, 27)
						.addComponent(guessCharacter, GroupLayout.DEFAULT_SIZE, 122, Short.MAX_VALUE)
						.addGap(18, 18, 18)
						.addComponent(guessRoom, GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
						.addGap(21, 21, 21))
					.addGroup(guessDialogeLayout.createSequentialGroup()
						.addGap(45, 45, 45)
						.addComponent(label1)
						.addGap(96, 96, 96)
						.addComponent(label3)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 103, Short.MAX_VALUE)
						.addComponent(label4)
						.addGap(70, 70, 70))
			);
			guessDialogeLayout.setVerticalGroup(
				guessDialogeLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, guessDialogeLayout.createSequentialGroup()
						.addGap(12, 12, 12)
						.addComponent(label2)
						.addGap(8, 8, 8)
						.addGroup(guessDialogeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(label1)
							.addComponent(label3)
							.addComponent(label4))
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(guessDialogeLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
							.addComponent(guessRoom, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
							.addComponent(guessWeapon, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
							.addComponent(guessCharacter, GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE))
						.addGap(18, 18, 18)
						.addComponent(guessOKButton, GroupLayout.PREFERRED_SIZE, 38, GroupLayout.PREFERRED_SIZE)
						.addContainerGap())
			);
			guessDialoge.pack();
			guessDialoge.setLocationRelativeTo(guessDialoge.getOwner());
		}

		//======== errorDialog ========
		{
			errorDialog.setTitle("ERROR!");
			Container errorDialogContentPane = errorDialog.getContentPane();

			//---- errorText1 ----
			errorText1.setText("You have tried to perform a suggestion ");
			errorText1.setFont(new Font("Tahoma", Font.PLAIN, 14));

			//---- errorOK ----
			errorOK.setText("OK");
			errorOK.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					errorOKActionPerformed(e);
					errorOKActionPerformed(e);
					errorOKActionPerformed(e);
				}
			});

			//---- errorText2 ----
			errorText2.setText("without being in a room");
			errorText2.setFont(new Font("Tahoma", Font.PLAIN, 14));

			GroupLayout errorDialogContentPaneLayout = new GroupLayout(errorDialogContentPane);
			errorDialogContentPane.setLayout(errorDialogContentPaneLayout);
			errorDialogContentPaneLayout.setHorizontalGroup(
				errorDialogContentPaneLayout.createParallelGroup()
					.addGroup(errorDialogContentPaneLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(errorDialogContentPaneLayout.createParallelGroup()
							.addComponent(errorText1)
							.addGroup(errorDialogContentPaneLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addComponent(errorOK)
								.addComponent(errorText2)))
						.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
			);
			errorDialogContentPaneLayout.setVerticalGroup(
				errorDialogContentPaneLayout.createParallelGroup()
					.addGroup(errorDialogContentPaneLayout.createSequentialGroup()
						.addGap(22, 22, 22)
						.addComponent(errorText1)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(errorText2)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
						.addComponent(errorOK)
						.addContainerGap())
			);
			errorDialog.pack();
			errorDialog.setLocationRelativeTo(errorDialog.getOwner());
		}

		//======== suggestionDialog ========
		{
			suggestionDialog.setTitle("Suggestion");
			suggestionDialog.setLocationByPlatform(true);
			suggestionDialog.setMinimumSize(new Dimension(310, 210));
			Container suggestionDialogContentPane = suggestionDialog.getContentPane();

			//---- line1Text ----
			line1Text.setText("Player blah guessed these cards:");
			line1Text.setHorizontalAlignment(SwingConstants.CENTER);

			//---- line2Text ----
			line2Text.setText("Insert 3 cards here");
			line2Text.setHorizontalAlignment(SwingConstants.CENTER);

			//---- line3Text ----
			line3Text.setText("Please click ok and then select one of your cards");
			line3Text.setHorizontalAlignment(SwingConstants.CENTER);

			//---- guessDiagOkButton ----
			guessDiagOkButton.setText("OK");
			guessDiagOkButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					guessDiagOkButtonActionPerformed(e);
				}
			});

			//---- guessDiagPlayerNameText ----
			guessDiagPlayerNameText.setText("Player 1");

			GroupLayout suggestionDialogContentPaneLayout = new GroupLayout(suggestionDialogContentPane);
			suggestionDialogContentPane.setLayout(suggestionDialogContentPaneLayout);
			suggestionDialogContentPaneLayout.setHorizontalGroup(
				suggestionDialogContentPaneLayout.createParallelGroup()
					.addGroup(GroupLayout.Alignment.TRAILING, suggestionDialogContentPaneLayout.createSequentialGroup()
						.addGap(0, 38, Short.MAX_VALUE)
						.addComponent(line3Text)
						.addGap(19, 19, 19))
					.addGroup(suggestionDialogContentPaneLayout.createSequentialGroup()
						.addGroup(suggestionDialogContentPaneLayout.createParallelGroup()
							.addGroup(suggestionDialogContentPaneLayout.createSequentialGroup()
								.addGap(56, 56, 56)
								.addComponent(line1Text))
							.addGroup(suggestionDialogContentPaneLayout.createSequentialGroup()
								.addGap(105, 105, 105)
								.addComponent(guessDiagPlayerNameText))
							.addGroup(suggestionDialogContentPaneLayout.createSequentialGroup()
								.addGap(84, 84, 84)
								.addComponent(line2Text))
							.addGroup(suggestionDialogContentPaneLayout.createSequentialGroup()
								.addGap(114, 114, 114)
								.addComponent(guessDiagOkButton)))
						.addContainerGap(74, Short.MAX_VALUE))
			);
			suggestionDialogContentPaneLayout.setVerticalGroup(
				suggestionDialogContentPaneLayout.createParallelGroup()
					.addGroup(suggestionDialogContentPaneLayout.createSequentialGroup()
						.addGap(7, 7, 7)
						.addComponent(guessDiagPlayerNameText)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(line1Text)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(line2Text)
						.addGap(11, 11, 11)
						.addComponent(line3Text)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
						.addComponent(guessDiagOkButton)
						.addContainerGap())
			);
			suggestionDialog.pack();
			suggestionDialog.setLocationRelativeTo(suggestionDialog.getOwner());
		}

		//======== shortcuts ========
		{
			shortcuts.setTitle("Shortcuts");
			Container shortcutsContentPane = shortcuts.getContentPane();

			//---- label8 ----
			label8.setText("Press any of these keys to perform their corresponding action");

			//---- label9 ----
			label9.setText("N = Next player / end turn");

			//---- label10 ----
			label10.setText("S = Perform Suggestion");

			//---- label11 ----
			label11.setText("A = Perform Accusation");

			//---- label12 ----
			label12.setText("G = New Game");

			//---- shortcutOK ----
			shortcutOK.setText("OK");
			shortcutOK.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					shortcutOKActionPerformed(e);
				}
			});

			GroupLayout shortcutsContentPaneLayout = new GroupLayout(shortcutsContentPane);
			shortcutsContentPane.setLayout(shortcutsContentPaneLayout);
			shortcutsContentPaneLayout.setHorizontalGroup(
				shortcutsContentPaneLayout.createParallelGroup()
					.addGroup(shortcutsContentPaneLayout.createSequentialGroup()
						.addGroup(shortcutsContentPaneLayout.createParallelGroup()
							.addGroup(shortcutsContentPaneLayout.createSequentialGroup()
								.addContainerGap()
								.addGroup(shortcutsContentPaneLayout.createParallelGroup()
									.addComponent(label8)
									.addComponent(label9)
									.addComponent(label10)
									.addComponent(label11)
									.addComponent(label12)))
							.addGroup(shortcutsContentPaneLayout.createSequentialGroup()
								.addGap(111, 111, 111)
								.addComponent(shortcutOK, GroupLayout.PREFERRED_SIZE, 82, GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(7, Short.MAX_VALUE))
			);
			shortcutsContentPaneLayout.setVerticalGroup(
				shortcutsContentPaneLayout.createParallelGroup()
					.addGroup(shortcutsContentPaneLayout.createSequentialGroup()
						.addGap(19, 19, 19)
						.addComponent(label8)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(label9)
						.addGap(18, 18, 18)
						.addComponent(label10)
						.addGap(18, 18, 18)
						.addComponent(label11)
						.addGap(18, 18, 18)
						.addComponent(label12)
						.addGap(18, 18, 18)
						.addComponent(shortcutOK, GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
						.addContainerGap())
			);
			shortcuts.pack();
			shortcuts.setLocationRelativeTo(shortcuts.getOwner());
		}

		//======== discoveredCardsDiag ========
		{
			discoveredCardsDiag.setTitle("Discovered Cards");
			Container discoveredCardsDiagContentPane = discoveredCardsDiag.getContentPane();

			//---- dis1 ----
			dis1.setText("The cards that you have discovered");

			//---- dis2 ----
			dis2.setText("will display where your hand normally");

			//---- dis3 ----
			dis3.setText("displays until you press OK");

			//---- disOKButton ----
			disOKButton.setText("OK");
			disOKButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					disOKButtonActionPerformed(e);
					disOKButtonActionPerformed(e);
				}
			});

			GroupLayout discoveredCardsDiagContentPaneLayout = new GroupLayout(discoveredCardsDiagContentPane);
			discoveredCardsDiagContentPane.setLayout(discoveredCardsDiagContentPaneLayout);
			discoveredCardsDiagContentPaneLayout.setHorizontalGroup(
				discoveredCardsDiagContentPaneLayout.createParallelGroup()
					.addGroup(discoveredCardsDiagContentPaneLayout.createSequentialGroup()
						.addGroup(discoveredCardsDiagContentPaneLayout.createParallelGroup()
							.addGroup(discoveredCardsDiagContentPaneLayout.createSequentialGroup()
								.addGap(28, 28, 28)
								.addGroup(discoveredCardsDiagContentPaneLayout.createParallelGroup()
									.addComponent(dis3)
									.addComponent(dis2)
									.addComponent(dis1)))
							.addGroup(discoveredCardsDiagContentPaneLayout.createSequentialGroup()
								.addGap(91, 91, 91)
								.addComponent(disOKButton)))
						.addGap(28, 28, 28))
			);
			discoveredCardsDiagContentPaneLayout.setVerticalGroup(
				discoveredCardsDiagContentPaneLayout.createParallelGroup()
					.addGroup(discoveredCardsDiagContentPaneLayout.createSequentialGroup()
						.addGap(26, 26, 26)
						.addComponent(dis1)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(dis2)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
						.addComponent(dis3)
						.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
						.addComponent(disOKButton)
						.addContainerGap())
			);
			discoveredCardsDiag.pack();
			discoveredCardsDiag.setLocationRelativeTo(discoveredCardsDiag.getOwner());
		}
	}// </editor-fold>//GEN-END:initComponents

	/**
	 * Executes when roll dice button is pressed
	 * @param evt
	 */
	private void rollDiceActionPerformed(java.awt.event.ActionEvent evt) {
		int roll = 0;
		if(infiniteMove)
		{
			roll = (int) Double.POSITIVE_INFINITY;
		}
		else
		{
			roll = ((int) Math.ceil(Math.random()*11)) + 1; // generate a random number between 2 and 12 inclusive
		}
		currentPlayer.setRoll(roll);
		youRolledText.setText("You rolled " + roll);
		rollDice.setEnabled(false);
	}

	private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
		// TODO add your handling code here:
	}//GEN-LAST:event_jTextField2ActionPerformed

	private void GameMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_GameMenuMenuSelected
		// TODO add your handling code here:
	}//GEN-LAST:event_GameMenuMenuSelected

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - James Barfoote
	private JMenuBar jMenuBar;
	private JMenu fileMenu;
	private JMenuItem newGame;
	private JMenuItem showShortcuts;
	private JMenu GameMenu;
	private JMenuItem discoveredCardsmenuItem;
	private JMenu menu1;
	private JCheckBoxMenuItem cheatInfiniteMove;
	private JMenuItem cheatAnswer;
	private JButton rollDice;
	private JButton endTurn;
	private JButton guessButton;
	private JButton accusButton;
	private JLayeredPane boardArea;
	private JLayeredPane handArea;
	private JLabel yourhandText;
	private JLabel youRolledText;
	private JLabel playerTurnText;
	private JSeparator separator1;
	private Dialog guessDialoge;
	private JButton guessOKButton;
	private JComboBox guessWeapon;
	private JComboBox guessCharacter;
	private JComboBox guessRoom;
	private JLabel label2;
	private JLabel label1;
	private JLabel label3;
	private JLabel label4;
	private JDialog errorDialog;
	private JLabel errorText1;
	private JButton errorOK;
	private JLabel errorText2;
	private JDialog suggestionDialog;
	private JLabel line1Text;
	private JLabel line2Text;
	private JLabel line3Text;
	private JButton guessDiagOkButton;
	private JLabel guessDiagPlayerNameText;
	private JDialog shortcuts;
	private JLabel label8;
	private JLabel label9;
	private JLabel label10;
	private JLabel label11;
	private JLabel label12;
	private JButton shortcutOK;
	private JDialog discoveredCardsDiag;
	private JLabel dis1;
	private JLabel dis2;
	private JLabel dis3;
	private JButton disOKButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables


	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.equals(KeyEvent.VK_P))
		{
			this.setFocusable(true);
			endTurnActionPerformed(null);
		}

	}


}