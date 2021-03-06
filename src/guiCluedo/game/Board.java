package guiCluedo.game;
import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import guiCluedo.ui.startScreen;

public class Board {

	private ArrayList<Card> answer;
	public ArrayList<Player> players;
	private ArrayList<String> weaponNames = new ArrayList<>(Arrays.asList("Candlestick", "Knife", "Revolver", "Rope", "Lead Pipe", 
			"Wrench"));
	private ArrayList<String> characterNames = new ArrayList<>(Arrays.asList("Miss Scarlet", "Colonel Mustard","Mrs. White", "Mr. Green", 
			"Mrs. Peacock", "Professor Plum"));
	private ArrayList<String> roomNames = new ArrayList<>(Arrays.asList("Kitchen", "Ballroom", "Conservatory","Billiard Room", "Library", 
			"Study", "Hall", "Lounge", "Dining Room"));

	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();
	private ArrayList<Character> characters = new ArrayList<Character>();
	private ArrayList<Room> rooms = new ArrayList<Room>();
	private ArrayList<Point> doors = new ArrayList<Point>();
	private ArrayList<Point> stairwells = new ArrayList<Point>();
	private ArrayList<Card> allCards = new ArrayList<Card>();
	private ArrayList<Point> weaponLocations = new ArrayList<Point>();
	private ArrayList<Point> usedSquares = new ArrayList<Point>();
	private ArrayList<Point> playerSquares = new ArrayList<Point>();
	private Polygon centre;

	private String[][] board = new String[22][23];

	public Board(ArrayList<Player> players) {
		this.players = players;
		createCards();
		this.answer = genAns();
		delegateCards();
		createDoors();
		addToStairwells();
		for(Player player : players){
			usedSquares.add(player.getLocation());
			playerSquares.add(player.getLocation());
		}
	}

	/**
	 * Create the cards from the names provided in the Board class.
	 * Adds the weapons to the board
	 * @param cards
	 */
	private void createCards(){
		weaponLocations.add(new Point(2,2));
		weaponLocations.add(new Point(10,3));
		weaponLocations.add(new Point(20,11));
		weaponLocations.add(new Point(13,19));
		weaponLocations.add(new Point(5,20));
		weaponLocations.add(new Point(2,12));
		Collections.shuffle(weaponLocations);
		for(Point weapon : weaponLocations){
			usedSquares.add(weapon);
		}
		for(int i = 0; i < weaponNames.size(); i++){
			weapons.add(new Weapon(weaponNames.get(i), weaponLocations.get(i)));
		}
		for(String character : characterNames){
			characters.add(new Character(character));
		}
		for(String room : roomNames){
			if(room.equals("Kitchen")){
				int[] xCords = {0, 4, 4, 5, 5, 4, 4, 0};
				int[] yCords = {0, 0, 1, 1, 4, 4, 5, 5};
				Polygon p = new Polygon(xCords, yCords, 8);	//The polygon used as the bounding box
				rooms.add(new Room(room, p, new Point(1, 2)));
			}
			if(room.equals("Ballroom")){
				int[] xCords = {7, 12, 12, 7};
				int[] yCords = {0, 0, 5, 5};
				Polygon p = new Polygon(xCords, yCords, 4);
				rooms.add(new Room(room, p, new Point(9, 2)));
			}
			if(room.equals("Conservatory")){
				int[] xCords = {13, 18, 18, 17, 17, 14, 14, 13};
				int[] yCords = {0, 0, 5, 5, 6, 6, 5, 5};
				Polygon p = new Polygon(xCords, yCords, 8);
				rooms.add(new Room(room, p, new Point(15, 2)));
			}
			if(room.equals("Billiard Room")){
				int[] xCords = {20, 23, 23, 20};
				int[] yCords = {0, 0, 6, 6};
				Polygon p = new Polygon(xCords, yCords, 4);
				rooms.add(new Room(room, p, new Point(20, 2)));
			}
			if(room.equals("Library")){
				int[] xCords = {17, 23, 23, 17};
				int[] yCords = {7, 7, 13, 13};
				Polygon p = new Polygon(xCords, yCords, 4);
				rooms.add(new Room(room, p, new Point(19, 10)));
			}
			if(room.equals("Study")){
				int[] xCords = {18, 23, 23, 18};
				int[] yCords = {15, 15, 22, 22};
				Polygon p = new Polygon(xCords, yCords, 4);
				rooms.add(new Room(room, p, new Point(20, 18)));
			}
			if(room.equals("Hall")){
				int[] xCords = {9, 15, 15, 8, 8, 9};
				int[] yCords = {14, 14, 22, 22, 17, 17};
				Polygon p = new Polygon(xCords, yCords, 6);
				rooms.add(new Room(room, p, new Point(11, 18)));
			}
			if(room.equals("Lounge")){
				int[] xCords = {0, 6, 6, 0};
				int[] yCords = {16, 16, 22, 22};
				Polygon p = new Polygon(xCords, yCords, 4);
				rooms.add(new Room(room, p, new Point(2, 19)));
			}
			if(room.equals("Dining Room")){
				int[] xCords = {0, 1, 1, 7, 7, 1, 1, 0};
				int[] yCords = {9, 9, 7, 7, 14, 14, 12, 12};
				Polygon p = new Polygon(xCords, yCords, 8);
				rooms.add(new Room(room, p, new Point(3, 10)));
			}
		}
		int[] xCords = {9, 16, 16, 9};
		int[] yCords = {7, 7, 12, 12};
		this.centre = new Polygon(xCords, yCords, 4);
		//Add to newly created cards to allCards, allCards temporarily holds all the cards not included in the answer
		//to make it easier to delegate them out to the players
		allCards.addAll(weapons);
		allCards.addAll(characters);
		allCards.addAll(rooms);
	}

	/**
	 * Add stairwells to the board
	 */
	private void addToStairwells(){
		Point p = new Point(0,0);
		stairwells.add(p);
		p = new Point(20,0);
		stairwells.add(p);
		p = new Point(18,21);
		stairwells.add(p);
		p = new Point(0,16);
		stairwells.add(p);
		for(Point stairwell : stairwells){
			usedSquares.add(stairwell);
		}
	}

	/**
	 * Generates a random answer.
	 * An Answer is 3 random cards, one Weapon, one Character and one Room.
	 * @return ArrayList<Card>
	 */
	private ArrayList<Card> genAns() {
		ArrayList<Card> answer = new ArrayList<Card>();
		Random rand = new Random();
		int weaponNum = rand.nextInt(weapons.size());
		int characterNum = rand.nextInt(characters.size());
		int roomNum = rand.nextInt(rooms.size());
		Weapon weapon = weapons.get(weaponNum);
		Character character = characters.get(characterNum);
		Room room = rooms.get(roomNum);
		answer.add(weapon);
		answer.add(character);
		answer.add(room);
		allCards.remove(weapon);
		allCards.remove(character);
		allCards.remove(room);
		return answer;
	}

	/**
	 * delegates the cards out to the players hands
	 * @param numPlayers - The number of player in the game
	 */
	private void delegateCards() {
		//Delegate the cards out to the players.
		Collections.shuffle(allCards);
		int numCards = allCards.size();
		for(Player p: players){
			int i = 0;
			int j = 0;
			while(i <(numCards/players.size())){
				p.addToHand(allCards.get(i));
				i++;
			}
			while(j <(numCards/players.size())){
				allCards.remove(0);
				j++;
			}
		}
		//if any cards still need to be delegated, do the rest.
		if(allCards.size()>0){
			for(Player p : players){
				if(allCards.size() > 0){
					p.addToHand(allCards.get(0));
					allCards.remove(0);
				}
			}
		}
	}

	private void createDoors(){
		this.doors.add(new Point(3, 4));
		this.doors.add(new Point(8, 4));
		this.doors.add(new Point(13, 2));
		this.doors.add(new Point(15, 5));
		this.doors.add(new Point(20, 4));
		this.doors.add(new Point(20, 7));
		this.doors.add(new Point(17, 9));
		this.doors.add(new Point(17, 10));
		this.doors.add(new Point(18, 15));
		this.doors.add(new Point(14, 17));
		this.doors.add(new Point(5, 17));
		this.doors.add(new Point(4, 13));
		this.doors.add(new Point(6, 11));
		this.doors.add(new Point(6, 10));
		this.doors.add(new Point(6, 9));
		for(Point door : this.doors){
			this.usedSquares.add(door);
		}
	}

	public ArrayList<Player> getPlayers(){
		return players;
	}

	public ArrayList<String> getWeaponNames(){
		return weaponNames;
	}

	public ArrayList<String> getCharacterNames(){
		return characterNames;
	}

	public ArrayList<String> getRoomNames(){
		return roomNames;
	}

	public ArrayList<Room> getRooms(){
		return rooms;
	}

	public ArrayList<Weapon> getWeapons(){
		return weapons;
	}

	public ArrayList<Character> getCharacters(){
		return characters;
	}

	public String[][] getBoard(){
		return board;
	}

	public ArrayList<Point> getDoors(){
		return doors;
	}

	public Polygon getCentre(){
		return centre;
	}

	public ArrayList<Point> getStairwells(){
		return stairwells;
	}

	public ArrayList<Point> getUsedSquares(){
		return usedSquares;
	}
	
	public ArrayList<Point> getPlayerSquares(){
		return playerSquares;
	}
	
	public ArrayList<Card> getAnswer()
	{
		return answer;
	}
	
	public void setPlayers(ArrayList<Player> players){
		this.players = players;
	}
}