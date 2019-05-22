import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JPanel {
	
	//area
	private JButton[][] map;
	private int[][] cells;
	/**
	 * 0 = grass
	 * 1 = water
	 * 2 = tree
	 * 3 = food
	 * 4 = player alive
	 * 5 = player dead
	 */
	
	
	//leaderboard
	private JLabel numPlayers; //shows number of players alive
	private JList<String> playerNames; //lists player names
	private JButton reset;
	
	private ArrayList<Player> players; //stores the players
	private ArrayList<String> nameGen = new ArrayList<String>(); //name generator
	private ArrayList<String> typeGen = new ArrayList<String>(); //type generator
	
	public GUI() {
		setLayout(new BorderLayout());
		
		JPanel area = new JPanel(); //main battle area
		area.setLayout(new GridLayout(100, 100));
		add(area, BorderLayout.CENTER);
		map = new JButton[100][100];
		cells = new int[100][100];
		
		for(int r = 0; r < map.length; r++) {
			for(int c = 0; c < map[0].length; c++) {
				map[r][c] = new JButton();
				map[r][c].setBackground(new Color(47, 230, 36));
				area.add(map[r][c]);
				cells[r][c] = 0;
			}
		}
		waterPlacement(); //generates bodies of water
		
		nameGen(); //adds names to nameGen
		typeGen(); //adds types to typeGen
		players = new ArrayList<Player>();
		playerCreation(); //adds players
		
		playerPlacement(); //generates players
		
		JPanel leaderboard = new JPanel(); //leaderboard
		leaderboard.setLayout(new BorderLayout());
		add(leaderboard, BorderLayout.EAST);
		
		numPlayers = new JLabel("Number of players left: " + countAlive());
		leaderboard.add(numPlayers, BorderLayout.NORTH);
		
		playerNames = new JList<String>(playerNameList());
		leaderboard.add(playerNames, BorderLayout.CENTER);
		
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
		leaderboard.add(reset, BorderLayout.SOUTH);
	}
	
	//post: generates 20 bodies of water
	public void waterPlacement() {
		int num = (int)(Math.random() * 6 + 10); //number of bodies of water 10-15
		for(int c = 0; c < num; c++) {
			waterBodyGen();
		}
	}
	
	//method of waterGen()
	//post: generates a 4x4 body of water
	private void waterBodyGen() {
		int x = (int)(Math.random() * map.length);
		int y = (int)(Math.random() * map[0].length);
		while(x < 0 || x >= map.length || y < 0 || y >= map[0].length || cells[y][x] == 1) { //checks if coordinates are within bounds and aren't on top of water
			x = (int)(Math.random() * map.length);
			y = (int)(Math.random() * map[0].length);
		} //generates new coordinates
		
		for(int r = y; r < y + 5; r++) {
			for(int c = x ; c < x + 5; c++) {
				if(r < map.length && c < map[0].length) { //checks if r & c are within bounds
					map[r][c].setBackground(new Color(26, 23, 224));
					cells[r][c] = 1;
				}
			}
		}
	}
	
	//post: places players on the map
	public void playerPlacement() {
		for(int c = 0; c < players.size(); c++) {
			int x = (int)(Math.random() * map.length);
			int y = (int)(Math.random() * map[0].length);
			while(x < 0 || x >= map.length || y < 0 || y >= map[0].length || cells[y][x] == 1 || detectPlayers(x, y)) { //checks if coordinates are within bounds and aren't on top of water
				x = (int)(Math.random() * map.length);
				y = (int)(Math.random() * map[0].length);
			} //generates new coordinates
		
			cells[y][x] = 4;
			map[y][x].setBackground(Color.red);
		}
	}
	
	//method of playerPlacement()
	//post: returns if there is a player within a 5x5 square of the given coordinates
	private boolean detectPlayers(int x, int y) {
		for(int r = y - 5; r <= y + 5; r++) {
			for(int c = x - 5; c <= x + 5; c++) {
				if(r >= 0 && r < map.length && c >= 0 && c < map[0].length) { //checks if r & c are within bounds
					if(cells[r][c] == 4) //if there is a player
						return true;
				}
			}
		}
		
		return false;
	}

	//post: returns an array of the player names
	public String[] playerNameList() {
		String[] n = new String[players.size()];
		for(int c = 0; c < players.size(); c++) {
			n[c] = players.get(c).toString();
		}
		
		return n;
	}
	
	//name generator
	public void nameGen() { 
		nameGen.add("John");
		nameGen.add("Nikhil");
		nameGen.add("Mahajan");
		nameGen.add("Franco");
		nameGen.add("Medrano");
		nameGen.add("Jack");
		nameGen.add("Jason");
		nameGen.add("Mason");
		nameGen.add("David");
		nameGen.add("Peter");
		nameGen.add("Anna");
		nameGen.add("Sydney");
		nameGen.add("Sean");
		nameGen.add("McLellan");
		nameGen.add("Hannah");
		nameGen.add("McNeal");
		nameGen.add("Ronald");
		nameGen.add("McDonald");
		nameGen.add("Donald");
		nameGen.add("Trump");
		nameGen.add("Harsh");
		nameGen.add("Daniel");
		nameGen.add("Emily");
		nameGen.add("Silva");
		nameGen.add("Kelly");
		nameGen.add("Tatian");
		nameGen.add("Edmund");
		nameGen.add("Lau");
		nameGen.add("Zach");
		nameGen.add("Zack");
		nameGen.add("Rick");
		nameGen.add("Pedro");
		nameGen.add("Juan");
		nameGen.add("Chavez");
		nameGen.add("Robert");
		nameGen.add("Roberto");
		nameGen.add("Sarah");
		nameGen.add("Matt");
		nameGen.add("Lucas");
		nameGen.add("Scott");
		nameGen.add("Dhruv");
		nameGen.add("Ethan");
		nameGen.add("Cooper");
		nameGen.add("Tyler");
		nameGen.add("Gordon");
		nameGen.add("Greg");
		nameGen.add("Gregory");
		nameGen.add("Vincent");
		nameGen.add("Vinnie");
		nameGen.add("Neil");
		nameGen.add("Cyrus");
	}

	//type generator
	public void typeGen() { 
		typeGen.add("S&S");
		typeGen.add("Dagger");
		typeGen.add("2H");
	}
	
	//post: creates 20 players
	public void playerCreation() {
		for(int c = 0; c < 20; c++) { //adds 20 players
			String firstName = nameGen.get((int)(Math.random() * nameGen.size()));
			String lastName = nameGen.get((int)(Math.random() * nameGen.size()));
			while(lastName.equals(firstName)) { //checks if the last name equals the first name
				lastName = nameGen.get((int)(Math.random() * nameGen.size()));
			}
			String name = firstName + " " + lastName;
			
			for(int k = 0; c < players.size(); k++) { //checks if the name is already used
				while(name.equals(players.get(k).getName())) {
					firstName = nameGen.get((int)(Math.random() * nameGen.size()));
					lastName = nameGen.get((int)(Math.random() * nameGen.size()));
					while(lastName.equals(firstName)) {
						lastName = nameGen.get((int)(Math.random() * nameGen.size()));
						k = 0; //iterates thru entire array again
					}
					name = firstName + " " + lastName;
				}
			}
			
			String type = typeGen.get((int)(Math.random() * typeGen.size()));
			Player p;
			if(type.equals("2H")) {
				p = new TwoHanded(name);
			}
			else if(type.equals("Dagger")) {
				p = new Dagger(name);
			}
			else {
				p = new SwordAndShield(name);
			}
			
			players.add(p);
		}
	}
	
	//post: returns how many players are alive
	public int countAlive() {
		int count = 0;
		for(int c = 0; c < players.size(); c++) { //iterates thru players
			if(players.get(c).isAlive) //if they are alive
				count++;
		}
		
		return count;
	}
	
	//reset button
	private class Reset implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			players.clear(); //clears all players
			playerCreation(); //adds 20 players
			numPlayers.setText("Number of players left: " + countAlive());
			playerNames.setListData(playerNameList());
			
			for(int r = 0; r < map.length; r++) {
				for(int c = 0; c < map[0].length; c++) {
					map[r][c].setBackground(new Color(47, 230, 36));
					cells[r][c] = 0;
				}
			} //clears map
			waterPlacement(); //generates bodies of water
			playerPlacement();
		}
	}
}
