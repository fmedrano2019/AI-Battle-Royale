import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;

public class GUI extends JPanel {

	//area
	private static JButton[][] map;
	private static int[][] cells;
	private static final int PLAYER_NUMBER = 25;
	private static final int MAP_SIDE_LENGTH = 100;
	private static final int RUNTIME = 200;
	
	/**
	 * 0 = grass ; Color(47, 230, 36)
	 * 1 = water ; Color(26, 23, 224)
	 * 2 = tree ; Color(139, 69, 19)
	 * 3 = food ; Color(255, 178, 102)
	 * 4 = dagger player ; Color.red
	 * 5 = sword and shield player ; Color(0, 255, 255)
	 * 6 = two handed sword player ; Color.white
	 * 7 = player dead ; Color.black
	 */

	//leaderboard
	private JList<String> playerNames; //lists player names
	private JButton reset;

	//color key
	private JPanel keyPanel;

	private static ArrayList<Player> players; //stores the players
	private ArrayList<String> firstNameGen = new ArrayList<String>(); //first name generator
	private ArrayList<String> lastNameGen=new ArrayList<String>(); //last name generator
	private ArrayList<String> typeGen = new ArrayList<String>(); //type generator

	ScheduledExecutorService ses; 
	
	public GUI() {
		setLayout(new BorderLayout());

		JPanel area = new JPanel(); //main battle area
		area.setLayout(new GridLayout(MAP_SIDE_LENGTH, MAP_SIDE_LENGTH));
		add(area, BorderLayout.CENTER);
		map = new JButton[MAP_SIDE_LENGTH][MAP_SIDE_LENGTH];
		cells = new int[MAP_SIDE_LENGTH][MAP_SIDE_LENGTH];

		for(int r = 0; r < map.length; r++) {
			for(int c = 0; c < map[0].length; c++) {
				map[r][c] = new JButton();
				map[r][c].setBackground(new Color(47, 230, 36));
				map[r][c].setBorderPainted(false);
				area.add(map[r][c]);
				cells[r][c] = 0;
			}
		}

		waterPlacement(); //generates bodies of water
		treePlacement(); //generates trees
		
		firstNameGen(); //adds first names to firstNameGen
		lastNameGen(); //adds last names to lastNameGen
		typeGen(); //adds types to typeGen
		players = new ArrayList<Player>();
		playerCreation(); //adds players

		playerPlacement(); //generates players

		foodPlacement(); //generates food
		
		JPanel leaderboard = new JPanel(); //leaderboard
		leaderboard.setLayout(new BorderLayout());
		add(leaderboard, BorderLayout.EAST);

		String[] temp = playerNameList();
		playerNames = new JList<String>(temp);
		leaderboard.add(playerNames, BorderLayout.NORTH);

		keyPanel=new JPanel(); //color key
		keyPanel.setLayout(new GridLayout(8, 2));
		keyCreation();
		leaderboard.add(keyPanel, BorderLayout.CENTER);

		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
		leaderboard.add(reset, BorderLayout.SOUTH);
		
		ses = Executors.newSingleThreadScheduledExecutor();
		ses.scheduleWithFixedDelay(new Runnable() { //repeats tasks such as player actions and placing food every set amount of time to imitate turns
			public void run() {
				try {
					for(int j = 0; j < players.size(); j++) {
						if(players.get(j).getFood() <= 0 || players.get(j).getWater() <= 0 || players.get(j).getEnergy() <= 0) { //if the player has no food/water/energy, they die
							players.get(j).setAlive(false);
							cells[players.get(j).getY()][players.get(j).getX()] = 7;
							map[players.get(j).getY()][players.get(j).getX()].setBackground(Color.black);
						}
					}

					for(int c = 0; c < players.size(); c++) {
						if(players.get(c).isAlive()) {
							players.get(c).decision(); //player either eats, drinks, or moves if they are alive
						}
					}

					foodPlacement(); //places food on the map every turn
					playerNames.setListData(playerNameList()); //updates list

					for(int i = 0; i < players.size(); i++) { //makes players active for the next turn
						if(players.get(i).isAlive()) {
							players.get(i).setActive(true); 
							players.get(i).setXP(players.get(i).getXP()+1); //increment xp of every alive player each turn
							players.get(i).setFood(players.get(i).getFood()-1); //decrements food of every alive player each turn
							players.get(i).setWater(players.get(i).getWater()-1); //decrements water of every alive player each turn
						}
					}

					if(countAlive()<=1) { //game ends when there are 1 or less players alive and a pop-up window emerges
						String winner = determineWinner();
						JOptionPane.showMessageDialog(null, winner);
						ses.shutdown();
					}
				} 
            catch (Throwable e) { 
					e.printStackTrace();
				}
			}
		}, 0, RUNTIME, TimeUnit.MILLISECONDS);
	}

	//post: generates 10-15 bodies of water
	public void waterPlacement() {
		int num = (int)(Math.random() * 6 + 10); //number of bodies of water 10-15
		for(int c = 0; c < num; c++) {
			waterBodyGen();
		}
	}

	//method of waterPlacement()
	//post: generates a body of water between a 5x5 to 8x8 size
	private void waterBodyGen() {
		int x = (int)(Math.random() * map[0].length);
		int y = (int)(Math.random() * map.length);
		int size = (int)(Math.random() * 4 + 5); //5 - 8 size
		
		while(x < 0 || x >= map.length || y < 0 || y >= map[0].length || cells[y][x] == 1) { //checks if coordinates are within bounds and aren't on top of water
			x = (int)(Math.random() * map.length);
			y = (int)(Math.random() * map[0].length);
		} //generates new coordinates

		for(int r = y; r < y + size; r++) {
			for(int c = x ; c < x + size; c++) {
				if(r < map.length && c < map[0].length) { //checks if r & c are within bounds
					map[r][c].setBackground(new Color(26, 23, 224));
					cells[r][c] = 1;
				}
			}
		}
	}

   //post: returns true if there is water a block away from the given coordinates, false otherwise
	public static boolean detectWater(int x, int y)
	{
		for(int r = y - 1; r <= y + 1; r++) {
			for(int c = x - 1; c <= x + 1; c++) {
				if(r >= 0 && r < map.length && c >= 0 && c < map[0].length) { //checks if r & c are within bounds
					if(cells[r][c] == 1) //if there is water
						return true;
				}
			}
		}

		return false;
	}

	//post: generates 15 - 20 tree bodies
	public void treePlacement() {
		int num = (int)(Math.random() * 6 + 15); //number of bodies of trees 15-20
		for(int c = 0; c < num; c++) {
			treeBodyGen();
		}
	}

	//method of treePlacement()
	//post: generates a tree block of 2x2 to 6x6 size
	private void treeBodyGen() {
		int x = (int)(Math.random() * map[0].length);
		int y = (int)(Math.random() * map.length);
		int xSize = (int)(Math.random() * 5 + 2); //2 - 6 width
		int ySize = (int)(Math.random() * 5 + 2); //2 - 6 height
		while(ySize == xSize) {
			ySize = (int)(Math.random() * 5 + 2); //2 - 6 height
		}
		while(x < 0 || x >= map.length || y < 0 || y >= map[0].length) { //checks if coordinates are within bounds
			x = (int)(Math.random() * map.length);
			y = (int)(Math.random() * map[0].length);
		} //generates new coordinates

		for(int r = y; r < y + ySize; r++) {
			for(int c = x ; c < x + xSize; c++) {
				if(r < map.length && c < map[0].length && getSpace(c, r) != 1) { //checks if r & c are within bounds and that there is no water there
					map[r][c].setBackground(new Color(139, 69, 19));
					cells[r][c] = 2;
				}
			}
		}
	}

	//post: places players on the map
	public void playerPlacement() {
		for(int c = 0; c < players.size(); c++) {
			int x = (int)(Math.random() * map.length);
			int y = (int)(Math.random() * map[0].length);
			while(x < 0 || x >= map.length || y < 0 || y >= map[0].length || getSpace(x, y) != 0 || detectPlayers(x, y)) { //checks if coordinates are within bounds and aren't on top of water
				x = (int)(Math.random() * map.length);
				y = (int)(Math.random() * map[0].length);
			} //generates new coordinates

			if(players.get(c).getType().equals("Dagger")) //places a dagger player
			{
				cells[y][x] = 4;
				map[y][x].setBackground(Color.red);
				players.set(c, new Dagger(players.get(c).getName(), x, y));
			}
			else if(players.get(c).getType().equals("S&S")) //places a sword and shield player
			{
				cells[y][x] = 5;
				map[y][x].setBackground(new Color(0, 255, 255));
				players.set(c, new SwordAndShield(players.get(c).getName(), x, y));
			}
			else //places a two-handed sword player
			{
				cells[y][x] = 6;
				map[y][x].setBackground(Color.white);
				players.set(c, new TwoHanded(players.get(c).getName(), x, y));
			}
		}
	}

	//method of playerPlacement()
	//post: returns true if there is a player within a 11x11 square of the given coordinates, false otherwise
	public boolean detectPlayers(int x, int y) {
		for(int r = y - 5; r <= y + 5; r++) {
			for(int c = x - 5; c <= x + 5; c++) {
				if(r >= 0 && r < map.length && c >= 0 && c < map[0].length) { //checks if r & c are within bounds
					if(getSpace(c, r) == 4 || getSpace(c, r) == 5 || getSpace(c, r) == 6) //if there is a player
						return true;
				}
			}
		}

		return false;
	}
	
   //post: places 2 food blocks on the map
	public void foodPlacement()
	{
		for(int i = 0; i < 2; i++)
		{
			int x = (int)(Math.random() * map[0].length);
			int y = (int)(Math.random() * map.length);
			while(x < 0 || x >= map[0].length || y < 0 || y >= map.length || getSpace(x, y) != 0) //checks if the x and y coordinate are in bounds and updates if necessary
			{
				x = (int)(Math.random() * map[0].length);
				y = (int)(Math.random() * map.length);
			}
			cells[y][x] = 3;
			map[y][x].setBackground(new Color (255, 178, 102));
		}
	}
	
   //post: returns true if there is food a block away from the given coordinates, false otherwise
	public static boolean detectFood(int x, int y)
	{
		for(int r = y - 1; r <= y + 1; r++) {
			for(int c = x - 1; c <= x + 1; c++) {
				if(r >= 0 && r < map.length && c >= 0 && c < map[0].length) { //checks if r & c are within bounds
					if(cells[r][c] == 3) //if there is food, it is "eaten" and the food block becomes grass
					{
						map[r][c].setBackground(new Color(47, 230, 36)); //updates to grass block
						cells[r][c]=0; //updates to grass block
						return true;
					}
				}
			}
		}

		return false;
	}

	//post: returns the value in cells at parameter coordinates, -1 if out of bounds
	public static int getSpace(int x, int y)
	{
		if(x >= 0 && x < map[0].length && y >= 0 && y < map.length) //checks if x and y are in bounds
			return cells[y][x];
		else
			return -1;
	}
	
   //post: returns the player with the coordinates given as parameters
	public static Player getPlayer(int x, int y) {
		for(int c = 0; c < players.size(); c++) {
			if(players.get(c).getX() == x && players.get(c).getY() == y) //checks if the player's coordinates match that of the parameter's
				if (players.get(c).isAlive) {
					return players.get(c);
				} 
            else { //given player is dead
					return null;
				}
		}
		
		return null;
	}

	//post: returns an array of the number of players alive and player names
	public String[] playerNameList() {
		String[] n = new String[players.size() + 1]; 
		n[0] = "Number of players left: " + countAlive(); //leaderboard shows the number of players alive
		for(int c = 0; c < players.size(); c++) {
			n[c + 1] = players.get(c).toString();
		}

		return n;
	}

	//post: adds first names to a first name bank
	public void firstNameGen() { 
		firstNameGen.add("John");
		firstNameGen.add("Nikhil");
		firstNameGen.add("Jake");
		firstNameGen.add("Franco");
		firstNameGen.add("Tim");
		firstNameGen.add("Jack");
		firstNameGen.add("Jason");
		firstNameGen.add("Mason");
		firstNameGen.add("David");
		firstNameGen.add("Peter");
		firstNameGen.add("Anna");
		firstNameGen.add("Sydney");
		firstNameGen.add("Sean");
		firstNameGen.add("Joey");
		firstNameGen.add("Hannah");
		firstNameGen.add("Andrea");
		firstNameGen.add("Ronald");
		firstNameGen.add("Catarina");
		firstNameGen.add("Donald");
		firstNameGen.add("Sam");
		firstNameGen.add("Harsh");
		firstNameGen.add("Daniel");
		firstNameGen.add("Emily");
		firstNameGen.add("Silva");
		firstNameGen.add("Kelly");
		firstNameGen.add("Emma");
		firstNameGen.add("Edmund");
		firstNameGen.add("Gabe");
		firstNameGen.add("Zach");
		firstNameGen.add("Amber");
		firstNameGen.add("Rick");
		firstNameGen.add("Pedro");
		firstNameGen.add("Juan");
		firstNameGen.add("Michael");
		firstNameGen.add("Robert");
		firstNameGen.add("Roberto");
		firstNameGen.add("Sarah");
		firstNameGen.add("Matt");
		firstNameGen.add("Lucas");
		firstNameGen.add("Scott");
		firstNameGen.add("Dhruv");
		firstNameGen.add("Ethan");
		firstNameGen.add("Cooper");
		firstNameGen.add("Tyler");
		firstNameGen.add("Gordon");
		firstNameGen.add("Noah");
		firstNameGen.add("Gregory");
		firstNameGen.add("Vincent");
		firstNameGen.add("Jay");
		firstNameGen.add("Neil");
		firstNameGen.add("Fred");
	}

	//post: adds last names to a last name bank
	public void lastNameGen()
	{
		lastNameGen.add("Donaldson");
		lastNameGen.add("Medrano");
		lastNameGen.add("Mahajan");
		lastNameGen.add("Mohamed");
		lastNameGen.add("Kim");
		lastNameGen.add("Lee");
		lastNameGen.add("Cyrus");
		lastNameGen.add("Turner");
		lastNameGen.add("Anderson");
		lastNameGen.add("Sargent");
		lastNameGen.add("Jordan");
		lastNameGen.add("Murray");
		lastNameGen.add("Zhang");
		lastNameGen.add("Griffin");
		lastNameGen.add("Jung");
		lastNameGen.add("Williams");
		lastNameGen.add("Lau");
		lastNameGen.add("Brown");
		lastNameGen.add("White");
		lastNameGen.add("Siamon");
		lastNameGen.add("Gustin");
		lastNameGen.add("Freeman");
		lastNameGen.add("Gaffney");
		lastNameGen.add("Chung");
		lastNameGen.add("Tran");
		lastNameGen.add("Simmons");
		lastNameGen.add("Colet");
		lastNameGen.add("Jones");
		lastNameGen.add("Udalov");
		lastNameGen.add("Joya");
		lastNameGen.add("Ingle");
		lastNameGen.add("Martinez");
		lastNameGen.add("Walker");
		lastNameGen.add("Barta");
		lastNameGen.add("Tatum");
		lastNameGen.add("Neuffer");
		lastNameGen.add("Hussein");
		lastNameGen.add("Levine");
		lastNameGen.add("Zucker");
		lastNameGen.add("Kruz");
		lastNameGen.add("Damiao");
		lastNameGen.add("Kang");
		lastNameGen.add("Park");
		lastNameGen.add("Thompson");
		lastNameGen.add("Wiggins");
		lastNameGen.add("Ingram");
		lastNameGen.add("Green");
		lastNameGen.add("Harris");
		lastNameGen.add("Musco");
		lastNameGen.add("Dennelind");
		lastNameGen.add("Robertson");
	}

	//post: adds the three different types of players to a type bank
	public void typeGen() { 
		typeGen.add("S&S"); //sword and shield
		typeGen.add("Dagger"); //dagger
		typeGen.add("2H"); //two handed
	}

	//post: creates 25 players
	public void playerCreation() {
		for(int c = 0; c < PLAYER_NUMBER; c++) { //adds 20 players
			String firstName = firstNameGen.get((int)(Math.random() * firstNameGen.size()));
			String lastName = lastNameGen.get((int)(Math.random() * lastNameGen.size()));
			String name = firstName + " " + lastName;

			String type = typeGen.get((int)(Math.random() * typeGen.size())); //gives the player a random type
			Player p;
			if(type.equals("2H")) {
				p = new TwoHanded(name); //creates a two handed player
			}
			else if(type.equals("Dagger")) {
				p = new Dagger(name); //creates a dagger player
			}
			else {
				p = new SwordAndShield(name); //creates a sword and shield player
			}

			players.add(p);
		}
	}

	//post: returns how many players are alive
	public int countAlive() {
		int count = 0;
		for(int c = 0; c < players.size(); c++) { //iterates through all players
			if(players.get(c).isAlive) //if they are alive, count increments 
				count++;
		}

		return count;
	}

	//post: creates a key grid for the various colors in the map
	public void keyCreation()
	{
		JButton[] cList = new JButton[8];
		JLabel[] cKey = new JLabel[8];

		cList[0]=new JButton(); //grass
		cList[0].setBackground(new Color(47, 230, 36));
		cKey[0]=new JLabel("Grass");
		keyPanel.add(cList[0]);
		keyPanel.add(cKey[0]);

		cList[1]=new JButton(); //water
		cList[1].setBackground(new Color(26, 23, 224));
		cKey[1]=new JLabel("Water");
		keyPanel.add(cList[1]);
		keyPanel.add(cKey[1]);


		cList[2]=new JButton(); //trees
		cList[2].setBackground(new Color(139, 69, 19));
		cKey[2]=new JLabel("Trees");
		keyPanel.add(cList[2]);
		keyPanel.add(cKey[2]);
		
		cList[3]=new JButton(); //food
		cList[3].setBackground(new Color (255, 178, 102));
		cKey[3]=new JLabel("Food");
		keyPanel.add(cList[3]);
		keyPanel.add(cKey[3]);

		cList[4]=new JButton(); //dagger player
		cList[4].setBackground(Color.red);
		cKey[4]=new JLabel("Dagger Player");
		keyPanel.add(cList[4]);
		keyPanel.add(cKey[4]);

		cList[5]=new JButton(); //sword and shield player
		cList[5].setBackground(new Color(0, 255, 255));
		cKey[5]=new JLabel("Sword and Shield Player");
		keyPanel.add(cList[5]);
		keyPanel.add(cKey[5]);

		cList[6]=new JButton(); //two handed sword player
		cList[6].setBackground(Color.white);
		cKey[6]=new JLabel("Two Handed Sword Player");
		keyPanel.add(cList[6]);
		keyPanel.add(cKey[6]);

		cList[7]=new JButton(); //dead player
		cList[7].setBackground(Color.black);
		cKey[7]=new JLabel("Dead Player");
		keyPanel.add(cList[7]);
		keyPanel.add(cKey[7]);
	}

   //post: determines the name of the winner of the battle royale, or nobody if everyone is dead
	public String determineWinner() {
		for(int c = 0; c < players.size(); c++) {
			if(players.get(c).isAlive()) //finds the last player alive
				return players.get(c).getName() + " is the winner!";
		}
		
		return "Nobody won!";
	}
	
   //post: returns the 2D array of all the elements in the grid
	public static int[][] getCells(){
		return cells;
	}
	
   //post: returns the 2D array of all the elements in the map (the colors)
	public static JButton[][] getMap(){
		return map;
	}
	
	//post: creates and sets up the reset button
	private class Reset implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			players.clear(); //clears all players
			playerCreation(); //adds 20 players
			playerNames.setListData(playerNameList()); //lists new players

			for(int r = 0; r < map.length; r++) {
				for(int c = 0; c < map[0].length; c++) {
					map[r][c].setBackground(new Color(47, 230, 36));
					cells[r][c] = 0;
				}
			} //clears map
			waterPlacement(); //generates bodies of water
			treePlacement(); //generates trees
			playerPlacement(); //generates players
			foodPlacement(); //generates food
			
			ses = Executors.newSingleThreadScheduledExecutor();
			ses.scheduleWithFixedDelay(new Runnable() {
				public void run() {
					try {
						for(int j = 0; j < players.size(); j++) {
							if(players.get(j).getFood() <= 0 || players.get(j).getWater() <= 0 || players.get(j).getEnergy() <= 0) { //if the player has no food/water/energy, they die
								players.get(j).setAlive(false);
								cells[players.get(j).getY()][players.get(j).getX()] = 7;
								map[players.get(j).getY()][players.get(j).getX()].setBackground(Color.black);
							}
						}

						for(int c = 0; c < players.size(); c++) {
							if(players.get(c).isAlive()) {
								players.get(c).decision();
							}
						}

						foodPlacement();
						playerNames.setListData(playerNameList()); //updates list

						for(int i = 0; i < players.size(); i++) { //makes players active for the next turn
							if(players.get(i).isAlive()) {
								players.get(i).setActive(true);
								players.get(i).setXP(players.get(i).getXP()+1);
								players.get(i).setFood(players.get(i).getFood()-1);
								players.get(i).setWater(players.get(i).getWater()-1);
							}
						}

						if(countAlive()<=1) {
							String winner = determineWinner();
							JOptionPane.showMessageDialog(null, winner);
							ses.shutdown();
						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			}, 0, RUNTIME, TimeUnit.MILLISECONDS);
		}
	}
}
