import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.concurrent.*;

public class GUI extends JPanel {
	//area
	private static JButton[][] map;
	private static int[][] cells;
   
   /*
	 * 0 = grass
	 * 1 = water
	 * 2 = tree
	 * 3 = food
	 * 4 = dagger player
    * 5 = sword and shield player
    * 6 = two handed sword player
	 * 7 = player dead
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
	
	public GUI() 
   {
		setLayout(new BorderLayout());
		      
		JPanel area = new JPanel(); //main battle area
		area.setLayout(new GridLayout(100, 100));
		add(area, BorderLayout.CENTER);
		map = new JButton[100][100];
		cells=new int[100][100];
		
		for(int r = 0; r < map.length; r++) 
      {
			for(int c = 0; c < map[0].length; c++) 
         {
				map[r][c] = new JButton();
				map[r][c].setBackground(new Color(47, 230, 36));
            map[r][c].setBorderPainted(false);
				area.add(map[r][c]);
            cells[r][c]=0;
			}
		}
      
      waterPlacement();
      treePlacement();
		
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
				
		playerNames = new JList(playerNameList());
		leaderboard.add(playerNames, BorderLayout.NORTH);
      
      keyPanel=new JPanel(); //color key
      keyPanel.setLayout(new GridLayout(8, 2));
      keyCreation();
      leaderboard.add(keyPanel, BorderLayout.CENTER);
      		
		reset = new JButton("Reset");
		reset.addActionListener(new Reset());
      leaderboard.add(reset, BorderLayout.SOUTH);
      
      ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
      ses.scheduleWithFixedDelay(new Runnable() 
      {
         public void run()
         {
            for(int j=0; j<players.size(); j++)
            {
               if(players.get(j).getFood()<=0||players.get(j).getWater()<=0)
                  players.get(j).setAlive(false);
            }
            for(int i=0; i<players.size(); i++) //entire loop for players fighting each other
            {
               for(int j=0; j<players.size(); j++)
               {
                  if(players.get(i).isActive()&&players.get(j).isActive())
                  {
                     if(fightingDistance(players.get(j).getX(), players.get(j).getY(), players.get(i).getX(), players.get(i).getY()))
                     {
                        if(players.get(j).isWinner(players.get(i)))
                        {
                           players.get(i).setAlive(false);
                           players.get(j).setActive(false);
                           players.get(j).setXP(players.get(j).getXP()+50);
                           players.get(j).setWater(players.get(j).getWater()-15);
                           players.get(j).setFood(players.get(j).getFood()-10);
                        }
                        else 
                        {
                           players.get(j).setAlive(false);
                           players.get(i).setActive(false);
                           players.get(i).setXP(players.get(i).getXP()+50);
                           players.get(i).setWater(players.get(i).getWater()-15);
                           players.get(i).setFood(players.get(i).getFood()-10);
                        }
                     }
                  }
               }
            }
            
            for(int c=0; c<players.size(); c++)
            {
               if(players.get(c).isActive())
               {
                  if(detectWater(players.get(c).getX(), players.get(c).getY())&&players.get(c).getEnergy()<=180)
                  {
                     players.get(c).drink();
                     players.get(c).setActive(false);
                  }
                  else if(detectFood(players.get(c).getX(), players.get(c).getY())&&players.get(c).getEnergy()<=185)
                  {
                     players.get(c).eat();
                     players.get(c).setActive(false);
                  }
               }
            }

            for(int c = 0; c < players.size(); c++) 
            {
               if(players.get(c).isActive())
               {
                  int movement=(int)(Math.random()*4+1);
                  if(players.get(c).isActive())
                  {
                     if(movement==1)
                        players.get(c).moveUp();
                     else if(movement==2)
                        players.get(c).moveDown();
                     else if(movement==3)
                        players.get(c).moveLeft();
                     else
                        players.get(c).moveRight();
                  }
               }
            }
                        
            foodPlacement();
            playerNames.setListData(playerNameList());
            
            for(int i=0; i<players.size(); i++) //makes players active for the next turn
            {
               if(players.get(i).isAlive())
               {
                  players.get(i).setActive(true);
                  players.get(i).setXP(players.get(i).getXP()+1);
                  players.get(i).setFood(players.get(i).getFood()-1);
                  players.get(i).setWater(players.get(i).getWater()-1);
               }
            }
            if(countAlive()<=1)
            {
               ses.shutdown();
               System.out.print("HEY BABY");
            }
         } 
      }, 0, 1, TimeUnit.SECONDS);
	}
   
   public static int[][] getCells()
   {
      return cells;
   }
   
   public static JButton[][] getMap()
   {
      return map;
   }
	
   //creates a key for the various colors in the grid
   public void keyCreation()
   {
      JButton [] cList=new JButton[8];
      JLabel [] cKey=new JLabel[8];
      
      cList[0]=new JButton();
      cList[0].setBackground(new Color(47, 230, 36));
      cKey[0]=new JLabel("Grass");
      keyPanel.add(cList[0]);
      keyPanel.add(cKey[0]);
      
      cList[1]=new JButton();
      cList[1].setBackground(new Color(26, 23, 224));
      cKey[1]=new JLabel("Water");
      keyPanel.add(cList[1]);
      keyPanel.add(cKey[1]);
      
      cList[2]=new JButton();
      cList[2].setBackground(new Color(139, 69, 19));
      cKey[2]=new JLabel("Trees");
      keyPanel.add(cList[2]);
      keyPanel.add(cKey[2]);
      
      cList[3]=new JButton();
      cList[3].setBackground(new Color (255, 178, 102));
      cKey[3]=new JLabel("Food");
      keyPanel.add(cList[3]);
      keyPanel.add(cKey[3]);
      
      cList[4]=new JButton();
      cList[4].setBackground(Color.red);
      cKey[4]=new JLabel("Dagger Player");
      keyPanel.add(cList[4]);
      keyPanel.add(cKey[4]);
      
      cList[5]=new JButton();
      cList[5].setBackground(new Color(0, 255, 255));
      cKey[5]=new JLabel("Sword and Shield Player");
      keyPanel.add(cList[5]);
      keyPanel.add(cKey[5]);
      
      cList[6]=new JButton();
      cList[6].setBackground(Color.white);
      cKey[6]=new JLabel("Two Handed Sword Player");
      keyPanel.add(cList[6]);
      keyPanel.add(cKey[6]);
      
      cList[7]=new JButton();
      cList[7].setBackground(Color.black);
      cKey[7]=new JLabel("Dead Player");
      keyPanel.add(cList[7]);
      keyPanel.add(cKey[7]);
   }
   
   //post: generates 20 bodies of water
	public void waterPlacement() {
		int num = (int)(Math.random() * 6 + 10); //number of bodies of water 10-15
		for(int c = 0; c < num; c++) {
			waterBodyGen();
		}
	}
   
   //method of waterPlacement()
	//post: generates a 5x5 to 8x8 body of water
	private void waterBodyGen() 
   {
		int x = (int)(Math.random() * map.length);
		int y = (int)(Math.random() * map[0].length);
		int size = (int)(Math.random() * 4 + 5); //5 - 8 size

		while(x < 0 || x >= map.length || y < 0 || y >= map[0].length || cells[y][x] == 1) //checks if coordinates are within bounds and aren't on top of water
      {
			x = (int)(Math.random() * map.length);
			y = (int)(Math.random() * map[0].length);
		} //generates new coordinates
		
		for(int r = y; r < y + size; r++) 
      {
			for(int c = x ; c < x + size; c++) 
         {
				if(r < map.length && c < map[0].length) //checks if r & c are within bounds
            {
					map[r][c].setBackground(new Color(26, 23, 224));
					cells[r][c] = 1;
				}
			}
		}
	}
   
   public void treePlacement() {
		int num = (int)(Math.random() * 6 + 15); //number of bodies of trees 15-20
		for(int c = 0; c < num; c++) {
			treeBodyGen();
		}
	}

	//method of treePlacement()
	//post: generates a 3/6x3/6 tree block
	private void treeBodyGen() {
		int x = (int)(Math.random() * map[0].length);
		int y = (int)(Math.random() * map.length);
		int xSize = (int)(Math.random() * 5 + 3); //2 - 6 width
		int ySize = (int)(Math.random() * 5 + 3); //2 - 6 height
		while(ySize == xSize) {
			ySize = (int)(Math.random() * 5 + 3); //2 - 6 height
		}
		while(x < 0 || x >= map.length || y < 0 || y >= map[0].length) { //checks if coordinates are within bounds
			x = (int)(Math.random() * map.length);
			y = (int)(Math.random() * map[0].length);
		} //generates new coordinates

		for(int r = y; r < y + ySize; r++) {
			for(int c = x ; c < x + xSize; c++) {
				if(r < map.length && c < map[0].length && getSpace(c, r) != 1) { //checks if r & c are within bounds
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
			while(x < 0 || x >= map.length || y < 0 || y >= map[0].length || getSpace(x, y)!=0 || detectPlayers(x, y)) { //checks if coordinates are within bounds and aren't on top of water
				x = (int)(Math.random() * map[0].length);
				y = (int)(Math.random() * map.length);
			} //generates new coordinates
		   
         if(players.get(c).getType().equals("Dagger"))
         {
			   cells[y][x] = 4;
            map[y][x].setBackground(Color.red);
            players.set(c, new Dagger(players.get(c).getName(), x, y));
         }
         else if(players.get(c).getType().equals("S&S"))
         {
            cells[y][x]=5;
            map[y][x].setBackground(new Color(0, 255, 255));
            players.set(c, new SwordAndShield(players.get(c).getName(), x, y));
         }
         else
         {
            cells[y][x]=6;
            map[y][x].setBackground(Color.white);
            players.set(c, new TwoHanded(players.get(c).getName(), x, y));
         }
		}
	}
   
   //post: places food on the map
   public void foodPlacement()
   {
      for(int i=0; i<2; i++)
      {
         int x = (int)(Math.random() * map[0].length);
			int y = (int)(Math.random() * map.length);
         while(x<0||x>=map[0].length||y<0||y>=map.length||getSpace(x, y)!=0)
         {
            x = (int)(Math.random() * map[0].length);
			   y = (int)(Math.random() * map.length);
         }
         cells[y][x]=3;
         map[y][x].setBackground(new Color (255, 178, 102));
      }
   }
   
   //method of playerPlacement()
	//post: returns if there is a player within a 5x5 square of the given coordinates
	public boolean detectPlayers(int x, int y) {
		for(int r = y - 5; r <= y + 5; r++) {
			for(int c = x - 5; c <= x + 5; c++) {
				if(r >= 0 && r < map.length && c >= 0 && c < map[0].length) { //checks if r & c are within bounds
					if(cells[r][c] == 4 || cells[r][c]==5 || cells[r][c]==6) //if there is a player
						return true;
				}
			}
		}
		
		return false;
	}
   
   public boolean detectWater(int x, int y)
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
   
   public boolean detectFood(int x, int y)
   {
		for(int r = y - 1; r <= y + 1; r++) {
			for(int c = x - 1; c <= x + 1; c++) {
				if(r >= 0 && r < map.length && c >= 0 && c < map[0].length) { //checks if r & c are within bounds
					if(cells[r][c] == 3) //if there is food
               {
                  map[r][c].setBackground(new Color(47, 230, 36));
                  cells[r][c]=0;
						return true;
               }
				}
			}
		}
		
		return false;
	}
   
   public boolean fightingDistance(int x, int y, int otherX, int otherY)
   {
      if((x+1==otherX&&y==otherY)||(x-1==otherX&&y==otherY)||(y+1==otherY&&x==otherX)||(y-1==otherY&&x==otherX)||(x-1==otherX&&y-1==otherY)||(x-1==otherX&&y+1==otherY)||(x+1==otherX&&y-1==otherY)||(x+1==otherX&&y+1==otherY))
         return true;
      else 
         return false;
   }
   
   public static int getSpace(int x, int y)
   {
      if(x>=0&&x<map[0].length&&y>=0&&y<map.length)
         return cells[y][x];
      else
         return -1;
   }
   
	//post: returns an array of the player names
	public String[] playerNameList() {
		String[] n = new String[players.size()+1];
      n[0]="Number of players left: " + countAlive();
		for(int c = 1; c < players.size(); c++) {
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
			playerNames.setListData(playerNameList());
			
			for(int r = 0; r < map.length; r++) {
				for(int c = 0; c < map[0].length; c++) {
					map[r][c].setBackground(new Color(47, 230, 36));
					cells[r][c] = 0;
				}
			} //clears map
			waterPlacement(); //generates bodies of water
			playerPlacement();
         treePlacement();
         foodPlacement();
		}
	}
}
