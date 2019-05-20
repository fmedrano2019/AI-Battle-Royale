import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JPanel {
	//area
	private JButton[][] map;
	private int[][] cells;
	
	//leaderboard
	private JLabel numPlayers;
	private JList<String> playerNames;
	private JButton reset;
	
	private ArrayList<Player> players;
	private ArrayList<String> nameGen = new ArrayList<String>(); //name generator
	private ArrayList<String> typeGen = new ArrayList<String>(); //type generator
	
	public GUI() {
		setLayout(new BorderLayout());
		
		JPanel area = new JPanel(); //main battle area
		area.setLayout(new GridLayout(100, 100));
		add(area, BorderLayout.CENTER);
		map = new JButton[100][100];
		
		
		for(int r = 0; r < map.length; r++) {
			for(int c = 0; c < map[0].length; c++) {
				map[r][c] = new JButton();
				map[r][c].setBackground(Color.green);
				area.add(map[r][c]);
			}
		}
		
		nameGen(); //adds names to nameGen
		typeGen(); //adds types to typeGen
		players = new ArrayList<Player>();
		playerCreation();
		
		JPanel leaderboard = new JPanel(); //leaderboard
		leaderboard.setLayout(new BorderLayout());
		add(leaderboard, BorderLayout.EAST);
		
		numPlayers = new JLabel("Number of players left: " + countAlive());
		leaderboard.add(numPlayers, BorderLayout.NORTH);
		
		playerNames = playerNameList();
		leaderboard.add(playerNames, BorderLayout.CENTER);
		
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
		leaderboard.add(reset, BorderLayout.SOUTH);
	}
	
	//list of players
	public JList<String> playerNameList() {
		String[] n = new String[players.size()];
		for(int c = 0; c < players.size(); c++) {
			n[c] = players.get(c).toString();
		}
		
		return new JList(n);
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
		for(int c = 0; c < players.size(); c++) {
			if(players.get(c).isAlive)
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
		}
	}
}
