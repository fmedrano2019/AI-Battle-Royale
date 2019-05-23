import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GUI extends JPanel {
	//area
	private JButton[][] map;
	private int[][] cells;
   
   /*
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
   
   //color key
   private JPanel keyPanel;
	
	private ArrayList<Player> players; //stores the players
	private ArrayList<String> firstNameGen = new ArrayList<String>(); //first name generator
   private ArrayList<String> lastNameGen=new ArrayList<String>(); //last name generator
	private ArrayList<String> typeGen = new ArrayList<String>(); //type generator
	
	public GUI() {
		setLayout(new BorderLayout());
		      
		JPanel area = new JPanel(); //main battle area
		area.setLayout(new GridLayout(100, 100));
		add(area, BorderLayout.CENTER);
		map = new JButton[100][100];
		cells=new int[100][100];
		
		for(int r = 0; r < map.length; r++) {
			for(int c = 0; c < map[0].length; c++) {
				map[r][c] = new JButton();
				map[r][c].setBackground(new Color(47, 230, 36));
				area.add(map[r][c]);
            cells[r][c]=0;
			}
		}
      
      waterPlacement();
		
		firstNameGen(); //adds first names to firstNameGen
      lastNameGen(); //adds last names to lastNameGen
		typeGen(); //adds types to typeGen
		players = new ArrayList<Player>();
		playerCreation(); // adds players
      
      playerPlacement(); //generates players
		
		foodPlacement(); //places food
	
		
		JPanel leaderboard = new JPanel(); //leaderboard
		leaderboard.setLayout(new BorderLayout());
		add(leaderboard, BorderLayout.EAST);
		
		numPlayers = new JLabel("Number of players left: " + countAlive());
		leaderboard.add(numPlayers, BorderLayout.NORTH);
		
		playerNames = new JList(playerNameList());
		leaderboard.add(playerNames, BorderLayout.NORTH);
      
      keyPanel=new JPanel(); //color key
      keyPanel.setLayout(new GridLayout(7, 2));
      keyCreation();
      leaderboard.add(keyPanel, BorderLayout.CENTER);
      		
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
      leaderboard.add(reset, BorderLayout.SOUTH);
	}
	
   //creates a key for the various colors in the grid
   public void keyCreation()
   {
      JButton [] cList=new JButton[7];
      JLabel [] cKey=new JLabel[7];
      
      cList[0]=new JButton();
      cList[0].setBackground(Color.blue);
      cKey[0]=new JLabel("Water");
      keyPanel.add(cList[0]);
      keyPanel.add(cKey[0]);
      
      cList[1]=new JButton();
      cList[1].setBackground(new Color (255, 140, 0));
      cKey[1]=new JLabel("Food");
      keyPanel.add(cList[1]);
      keyPanel.add(cKey[1]);
      
      cList[2]=new JButton();
      cList[2].setBackground(new Color(139, 69, 19));
      cKey[2]=new JLabel("Trees");
      keyPanel.add(cList[2]);
      keyPanel.add(cKey[2]);
      
      cList[3]=new JButton();
      cList[3].setBackground(new Color(205, 92, 92));
      cKey[3]=new JLabel("Dagger Player");
      keyPanel.add(cList[3]);
      keyPanel.add(cKey[3]);
      
      cList[4]=new JButton();
      cList[4].setBackground(Color.red);
      cKey[4]=new JLabel("Sword and Shield Player");
      keyPanel.add(cList[4]);
      keyPanel.add(cKey[4]);
      
      cList[5]=new JButton();
      cList[5].setBackground(Color.red.darker());
      cKey[5]=new JLabel("Two Handed Sword Player");
      keyPanel.add(cList[5]);
      keyPanel.add(cKey[5]);
      
      cList[6]=new JButton();
      cList[6].setBackground(Color.black);
      cKey[6]=new JLabel("Dead Player");
      keyPanel.add(cList[6]);
      keyPanel.add(cKey[6]);
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
	
	//post: places food on the map
   public void foodPlacement()
   {
      for(int i=0; i<3; i++)
      {
         int x = (int)(Math.random() * map[0].length);
			int y = (int)(Math.random() * map.length);
         while(x<0||x>=map[0].length||y<0||y>=map.length||getSpace(x, y)!=0)
         {
            x = (int)(Math.random() * map[0].length);
			   y = (int)(Math.random() * map.length);
         }
         cells[y][x]=3;
         map[y][x].setBackground(new Color (255, 140, 0));
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
	
	public int getSpace(int x, int y)
    {
      if(x>=0&&x<map[0].length&&y>=0&&y<map.length)
         return cells[y][x];
      else
         return -1;
    }
   
	//post: returns an array of the player names
	public String[] playerNameList() {
		String[] n = new String[players.size()];
		for(int c = 0; c < players.size(); c++) {
			n[c] = players.get(c).toString();
		}
		
		return n;
	}
	
	//first name generator
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
   
   //last name generator
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

	//type generator
	public void typeGen() { 
		typeGen.add("S&S");
		typeGen.add("Dagger");
		typeGen.add("2H");
	}
	
	//post: creates 20 players
	public void playerCreation() {
		for(int c = 0; c < 20; c++) { //adds 20 players
			String firstName = firstNameGen.get((int)(Math.random() * firstNameGen.size()));
			String lastName = lastNameGen.get((int)(Math.random() * lastNameGen.size()));
			String name = firstName + " " + lastName;
			
			for(int k = 0; k < players.size(); k++) { //checks if the name is already used
				while(firstName.equals(players.get(k).getFirstName())||lastName.equals(players.get(k).getLastName())) { //ensures there are no duplicates
               if(firstName.equals(players.get(k).getFirstName()))
					   firstName = firstNameGen.get((int)(Math.random() * firstNameGen.size()));
               if(lastName.equals(players.get(k).getLastName()))
					   lastName = lastNameGen.get((int)(Math.random() * lastNameGen.size()));
					name = firstName + " " + lastName;
               k=0;
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
