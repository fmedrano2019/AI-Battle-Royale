import java.util.*;
import java.awt.*;
import javax.swing.*;

public abstract class Player {
	
	private static final int SIGHT_RANGE = 5; 
	private static final int STARTING_FOOD = 150;
	private static final int STARTING_WATER = 150;
	private static final int FOOD_THRESHOLD = 50;
	private static final int WATER_THRESHOLD = 50;
	private static final int ENERGY_UPPER_THRESHOLD = 500;
	private static final int MAX_FOOD_WATER_DIFF = 1000;
	
	
	
	public String name; 
	public String type;
	public int kills;
	public int xp;
	public int food;
	public int water;
	public int energy;
	public int x;
	public int y;
	public boolean isAlive;
	public boolean active;
	public int[][] nearby = new int[((SIGHT_RANGE * 2) + 1)*((SIGHT_RANGE * 2) + 1)][3];
	
	/** CONSTRUCTORS **/
	
	//no coordinate constructor
	public Player(String n, String t) {
		name = n;
		type = t;
		kills = 0;
		xp = 0;
		food = STARTING_FOOD;
		water = STARTING_WATER;
		energy = food + water;
		isAlive = true;
		active = true;
	}

	//constructor with coordinate
	public Player(String n, String t, int x, int y) {
		name = n;
		type = t;
		kills = 0;
		xp = 0;
		food = STARTING_FOOD;
		water = STARTING_WATER;
		energy = food + water;
		this.x = x;
		this.y = y;
		isAlive = true;
		active = true;
	}

	/** ACCESSORS **/
	
	//post: returns name
	public String getName() {
		return name;
	}
	
	//post: sets name
	public void setName(String n) {
		name = n;
	}

	//post: returns only the first name
   	public String getFirstName()
	{
		return name.substring(0, name.indexOf(" "));
	}

	//post: sets first name
	public void setFirstName(String n)
	{
   		name = n + name.substring(name.indexOf(" "));
	}

	//post: returns only the last name
	public String getLastName()
	{
		return name.substring(name.indexOf(" ")+1);
	}

	//post: sets only last name
	public void setLastName(String n)
	{
		name=name.substring(0, name.indexOf(" ")+1) + n;
	}
	
	//post: returns type
	public String getType() {
		return type;
	}
	
   //post: returns the integer associated the player's type
	public abstract int getTypeNum();
	
	//post: sets type
	public void setType(String t) {
		type = t;
	}
	
	//post: returns kills
	public int getKills() {
		return kills;
	}
	
	//post: sets kills
	public void setKills(int k) {
		kills = k;
	}
	
	//post: returns xp
	public int getXP() {
		return xp;
	}
	
	//post: sets xp
	public void setXP(int x) {
		xp = x;
	}
	
	//post: returns food
	public int getFood() {
		return food;
	}
	
	//post: sets food
	public void setFood(int f) {
		food = f;
	}
	
	//post: returns water
	public int getWater() {
		return water;
	}
	
	//post: sets water
	public void setWater(int w) {
		water = w;
	}
	
	//post: returns energy
	public int getEnergy() {
		return energy;
	}
	
	//post: sets energy
	public void setEnergy(int e) {
		energy = e;
	}
	
	public void setEnergy() {
		energy = water + food;
	}
	
	//post: returns the x coordinate
	public int getX() {
		return x;
	}

	//post: sets the x coordinate
	public void setX(int v) {
		x = v;
	}

	//post: returns the y coordinate
	public int getY() {
		return y;
	}

	//post: sets the y coordinate
	public void setY(int v) {
		y = v;
	}
	
	//post: returns isAlive
	public boolean isAlive() {
		return isAlive;
	}
	
	//post: sets isAlive
	public void setAlive(boolean a) {
		int [][] cells=GUI.getCells();
		JButton [][] map=GUI.getMap();
		isAlive = a;
		if(!isAlive) { //if the player is made dead, the map and element is updated at their given coordinate
			active = false;
			cells[y][x] = 7;
			map[y][x].setBackground(Color.black);
		}
	}
	
	//post: returns true if the player has not done something that turn yet, false if they have
	public boolean isActive() {
		return active;
	}

	//post: sets active to boolean a
	public void setActive(boolean a) {
		active = a;
	}
	
   //post: returns an integer based on who is the superior type
	public int superiorType(Player other)
	{
		if(type.equals(other.getType())) //equal type
			return 0;
		else if((type.equals("S&S")&&other.getType().equals("2H"))||(type.equals("2H")&&other.getType().equals("Dagger"))||(type.equals("Dagger")&&other.getType().equals("S&S"))) //superior type
			return 50;
		else //inferior type
			return -50;
	}
	
   //post: returns true if the coordinates are one block away from each other, false otherwise
	public boolean fightingDistance(int x, int y, int otherX, int otherY)
	{
		if((x+1==otherX&&y==otherY)||(x-1==otherX&&y==otherY)||(y+1==otherY&&x==otherX)||(y-1==otherY&&x==otherX)||(x-1==otherX&&y-1==otherY)||(x-1==otherX&&y+1==otherY)||(x+1==otherX&&y-1==otherY)||(x+1==otherX&&y+1==otherY))
			return true;
		else 
			return false;
	}
	
	//post: returns true if the player should chase based on superior typing, false otherwise with some probability involved
	public boolean superiorType(int t) { //for deciding whether to chase, semi-random
		if (Math.random() < 0.3) { //30% probability that the player chases no matter what
			return true;
		}
		else if((this.getTypeNum() == 5 && t == 6) || (this.getTypeNum() == 6 && t == 4) || (this.getTypeNum() == 4 && t == 5))
			return true;
		else 
			return false;
	}

   //post: returns true if the player beats the other player, false otherwise
	public boolean isWinner(Player other)
	{
		double score=0; //score of this player
		double otherScore=0; //score of the other player

		int typeWinner=superiorType(other);

		score+=typeWinner;
		otherScore+=typeWinner;

		double energyDifference=0; //energy difference between the two players
		if(energy>other.getEnergy())
			energyDifference=energy-other.getEnergy();
		else
			energyDifference=other.getEnergy()-energy;

		energyDifference=Math.exp(energyDifference);
		energyDifference=Math.pow(energyDifference, 1.0/45.0);

      if(energy>other.getEnergy()) //score adjusts according to who has higher energy
      {
   		score+=energyDifference;
   		otherScore-=energyDifference;
      }
      else
      {
         score-=energyDifference;
   		otherScore+=energyDifference;
      }

		double xpDifference=0; //xp difference between the two players
		if(xp>other.getXP())
			xpDifference=xp-other.getXP();
		else
			xpDifference=other.getXP()-xp;

		xpDifference=Math.exp(xpDifference);
		xpDifference=Math.pow(xpDifference, 1.0/60.0);

      if(xp>other.getXP()) //score adjusts according to who has higher xp
      {
   		score+=xpDifference; 
   		otherScore-=xpDifference;
      }
      else
      {
         score-=xpDifference;
         otherScore+=xpDifference;
      }

		return score>=otherScore; //player with the higher score wins the battle, based on typing, energy, and xp
	}

   //post: moves the player up and updates the map
	public void moveUp()
	{  
		int [][] cells=GUI.getCells();
		JButton [][] map=GUI.getMap();
		if(y > 0 && GUI.getSpace(x, y-1)==0)
		{
			cells[y-1][x] = GUI.getSpace(x, y);
			cells[y][x] = 0;
			map[y][x].setBackground(new Color(47, 230, 36));
			map[y-1][x].setBackground(this.getColor());
			y--;
		}
	}

   //post: moves the player down and updates the map
	public void moveDown()
	{
		int [][] cells=GUI.getCells();
		JButton [][] map=GUI.getMap();
		if(y < map.length-1 && GUI.getSpace(x, y+1)==0)
		{
			cells[y+1][x] = GUI.getSpace(x, y);
			cells[y][x] = 0;
			map[y][x].setBackground(new Color(47, 230, 36));
			map[y+1][x].setBackground(this.getColor());
			y++;
		}
	}

   //post: moves the player to the left and updates the map
	public void moveLeft()
	{  
		int [][] cells=GUI.getCells();
		JButton [][] map=GUI.getMap();
		if(x > 0 && GUI.getSpace(x-1, y)==0)
		{
			cells[y][x-1] = GUI.getSpace(x, y);
			cells[y][x] = 0;
			map[y][x].setBackground(new Color(47, 230, 36));
			map[y][x-1].setBackground(this.getColor());
			x--;
		}
	}

   //post: moves the player to the right and updates the map
	public void moveRight()
	{
		int [][] cells=GUI.getCells();
		JButton [][] map=GUI.getMap();
		if(x < map[0].length-1 && GUI.getSpace(x+1, y)==0)
		{
			cells[y][x+1] = GUI.getSpace(x, y);
			cells[y][x] = 0;
			map[y][x].setBackground(new Color(47, 230, 36));
			map[y][x+1].setBackground(this.getColor());
			x++;
		}
	}

   //post: updates the food, energy, and xp of player after eating
	public void eat()
	{
		food+=25;
		energy+=25;
		xp += 5;
	}

   //post: updates the water, energy, and xp of player after drinking
	public void drink()
	{
		water+=35;
		energy+=35;
		xp += 10;
	}   

   //post: returns the color associated with the type of player
	public abstract Color getColor();
	
   //post: returns a 2D array of the coordinates and type of element at that given coordinate in a 11x11 square from the player, essentially everything near the player
	public int[][] sight() {
		int [][] cells=GUI.getCells();
		int count = 0;
		for(int r = y - SIGHT_RANGE; r <= y + SIGHT_RANGE; r++) {
			for(int c = x - SIGHT_RANGE; c <= x + SIGHT_RANGE; c++) {
				if(r >= 0 && r < cells.length && c >= 0 && c < cells[0].length && !(r == y && c == x)) { //checks if r & c are within bounds
					nearby[count][0]=c;
					nearby[count][1]=r;
					nearby[count][2]=GUI.getSpace(c, r);
				}
				count++;
			}
		}
		
		return nearby;
	}

   //post: the player makes a decision according to a behavior tree with thirst/hunger taking precedence, then interactions with other players, then stockpiling resources, then randomness
	public void decision()
	{
		sight(); //player receives an idea of things nearby them
		ArrayList<int[]> nearbyFood = new ArrayList<int[]>();
		for(int c = 0; c < nearby.length; c++) { //checks if there's food in sight
			if(nearby[c][2] == 3)
				nearbyFood.add(new int[]{nearby[c][0], nearby[c][1]});
		}

		ArrayList<int[]> nearbyWater = new ArrayList<int[]>();
		for(int c = 0; c < nearby.length; c++) { //checks if there's water in sight
			if(nearby[c][2] == 1)
				nearbyWater.add(new int[]{nearby[c][0], nearby[c][1]});
		}

		ArrayList<int[]> nearbyPlayers = new ArrayList<int[]>();
		for(int c = 0; c < nearby.length; c++) { //checks if there's players in sight
			if(nearby[c][2] == 4 || nearby[c][2] == 5 || nearby[c][2] == 6)
			{
				if (GUI.getPlayer(nearby[c][0] , nearby[c][1] ) != null) {
					nearbyPlayers.add(new int[]{nearby[c][0], nearby[c][1], nearby[c][2]});
				}
			}
		}
	    
		if(GUI.detectFood(x, y))
			eat();
		else if(food < FOOD_THRESHOLD || water < WATER_THRESHOLD) { //if needs food/water
			if(food <= water) { //if food <= water
				if(GUI.detectFood(x, y)) { //if food is right next to player
					eat();
				}
				else { //if food isn't right next to player
					if(nearbyFood.size() >= 1) { //nearby food is located
						int xDifference = Math.abs(x - nearbyFood.get(0)[0]);
						int yDifference = Math.abs(y - nearbyFood.get(0)[1]);
						int xMinDifference = xDifference;
						int yMinDifference = yDifference;
						int distance = xDifference + yDifference;
						int xMin = nearbyFood.get(0)[0]; //x of food
						int yMin = nearbyFood.get(0)[1]; //y of food
						for(int i = 1; i < nearbyFood.size(); i++) { //checks for closer food
							xDifference = Math.abs(x - nearbyFood.get(i)[0]);
							yDifference = Math.abs(y - nearbyFood.get(i)[1]);
							if(xDifference + yDifference < distance) {
								xMin = nearbyFood.get(i)[0]; //x of food
								yMin = nearbyFood.get(i)[1]; //y of food
								xMinDifference = Math.abs(x - nearbyFood.get(i)[0]);
								yMinDifference = Math.abs(y - nearbyFood.get(i)[1]);
								distance = xMinDifference + yMinDifference;
							}
						}
						if(xMinDifference != 0) {
							if(xMin < x)
								moveLeft();
							else
								moveRight();
						}
						else { //if y != 0
							if(yMin < y)
								moveUp();
							else
								moveDown();
						}
					}

					else //there is no nearby food, the player moves randomly
					{
						int movement = (int)(Math.random() * 4 + 1);
						if(active) {
							if(movement == 1)
								moveUp();
							else if(movement == 2)
								moveDown();
							else if(movement == 3)
								moveLeft();
							else
								moveRight();
						}
					}
				}
			}
			
			else { //if water < food
				if(GUI.detectWater(x, y)) { //if water is right next to player, drink 
					drink();
				}
				else {
					if(nearbyWater.size() >= 1) { //nearby water is located
						int xDifference = Math.abs(x - nearbyWater.get(0)[0]);
						int yDifference = Math.abs(y - nearbyWater.get(0)[1]);
						int xMinDifference = xDifference;
						int yMinDifference = yDifference;
						int distance = xDifference + yDifference;
						int xMin = nearbyWater.get(0)[0]; //x of food
						int yMin = nearbyWater.get(0)[1]; //y of food
						for(int i = 1; i < nearbyWater.size(); i++) { //checks for closer water
							xDifference = Math.abs(x - nearbyWater.get(i)[0]);
							yDifference = Math.abs(y - nearbyWater.get(i)[1]);
							if(xDifference + yDifference < distance) {
								xMin = nearbyWater.get(i)[0]; //x of food
								yMin = nearbyWater.get(i)[1]; //y of food
								xMinDifference = Math.abs(x - nearbyWater.get(i)[0]);
								yMinDifference = Math.abs(y - nearbyWater.get(i)[1]);
								distance = xMinDifference + yMinDifference;
							}
						}
						if(xMinDifference != 0) { //if x is closer than y
							if(xMin < x)
								moveLeft();
							else
								moveRight();
						}
						else { //if y != 0
							if(yMin < y)
								moveUp();
							else
								moveDown();
						}
					}

					else if(nearbyFood.size()>=1&&energy<=ENERGY_UPPER_THRESHOLD)
					{
						if(GUI.detectFood(x, y)) //if food is right next to player
							eat();
						else //if food isn't right next to player
						{
							int xDifference=Math.abs(x-nearbyFood.get(0)[0]);
							int yDifference=Math.abs(y-nearbyFood.get(0)[1]);
							int xMinDifference=xDifference;
							int yMinDifference=yDifference;
							int distance=xMinDifference+yMinDifference;
							int xMin=nearbyFood.get(0)[0];
							int yMin=nearbyFood.get(0)[1];
							for(int i=1; i<nearbyFood.size(); i++)
							{
								xDifference=Math.abs(x-nearbyFood.get(i)[0]);
								yDifference=Math.abs(y-nearbyFood.get(i)[1]);
								if(xDifference+yDifference<distance)
								{
									xMin=nearbyFood.get(i)[0];
									yMin=nearbyFood.get(i)[1];
									xMinDifference=xDifference;
									yMinDifference=yDifference;
									distance=xMinDifference+yMinDifference;
								}
							}
							if(xMinDifference != 0)
							{
								if(xMin < x)
									moveLeft();
								else
									moveRight();
							}
							else
							{
								if(yMin < y)
									moveUp();
								else
									moveDown();
							}
						}
					}

					else //there is no nearby water, the player moves randomly
					{
						int movement = (int)(Math.random() * 4 + 1);
						if(active) 
						{
							if(movement == 1)
								moveUp();
							else if(movement == 2)
								moveDown();
							else if(movement == 3)
								moveLeft();
							else
								moveRight();
						}
					}
				}
			}
		}
		
		else if(nearbyPlayers.size() >= 1) { //if there are nearby players
			boolean run = false;
			for (int c = 0; c < nearbyPlayers.size(); c++) { // checks if there's a superior player
				if (nearbyPlayers.get(c).length > 2 && !superiorType(nearbyPlayers.get(c)[2])) {
					run = true; //sets the player in run mode if there is a nearby threat
					break;
				}
			}

			if(run) { //runs away from nearest threat
				int xDifference = Math.abs(x - nearbyPlayers.get(0)[0]);
				int yDifference = Math.abs(y - nearbyPlayers.get(0)[1]);
				int xMinDifference = xDifference;
				int yMinDifference = yDifference;
				int distance = xDifference + yDifference;
				int xMin = nearbyPlayers.get(0)[0]; //x of player
				int yMin = nearbyPlayers.get(0)[1]; //y of player
				for(int i = 1; i < nearbyPlayers.size(); i++) { //checks for closer player
					xDifference = Math.abs(x - nearbyPlayers.get(i)[0]);
					yDifference = Math.abs(y - nearbyPlayers.get(i)[1]);
					if(xDifference + yDifference < distance) {
						xMin = nearbyPlayers.get(i)[0]; //x of player
						yMin = nearbyPlayers.get(i)[1]; //y of player
						xMinDifference = Math.abs(x - nearbyPlayers.get(i)[0]);
						yMinDifference = Math.abs(y - nearbyPlayers.get(i)[1]);
						distance = xMinDifference + yMinDifference;
					}
				}
				if(xMinDifference != 0) {
					if(xMin < x)
						moveRight();
					else
						moveLeft();
				}
				else { //if y != 0
					if(yMin < y)
						moveDown();
					else
						moveUp();
				}
			}
			
			else {
				boolean fought = false;
				for(int c = 0; c < nearbyPlayers.size(); c++) { //iterates through nearbyPlayers
					if(fightingDistance(x, y, nearbyPlayers.get(c)[0], nearbyPlayers.get(c)[1])) { //if there's an immediate player, fights them
						Player p = GUI.getPlayer(nearbyPlayers.get(c)[0], nearbyPlayers.get(c)[1]);
						
						if(isWinner(p)) { //if wins
							p.setAlive(false);
							xp += 50;
							water -= 15;
							food -= 10;
							setEnergy();
							kills++;
						}
						else { //if lose
							setAlive(false);
							p.setXP(getXP() + 50);
							p.setWater(p.getWater() - 15);
							p.setFood(p.getFood() - 10);
							p.setEnergy();
							p.setKills(p.getKills() + 1);
						}
						fought = true;
						break;
					}
				}

				if(!fought) { //chases after nearest player
					int xDifference = Math.abs(x - nearbyPlayers.get(0)[0]);
					int yDifference = Math.abs(y - nearbyPlayers.get(0)[1]);
					int xMinDifference = xDifference;
					int yMinDifference = yDifference;
					int distance = xDifference + yDifference;
					int xMin = nearbyPlayers.get(0)[0]; //x of player
					int yMin = nearbyPlayers.get(0)[1]; //y of player
					for(int i = 1; i < nearbyPlayers.size(); i++) { //checks for closer player
						xDifference = Math.abs(x - nearbyPlayers.get(i)[0]);
						yDifference = Math.abs(y - nearbyPlayers.get(i)[1]);
						if(xDifference + yDifference < distance) {
							xMin = nearbyPlayers.get(i)[0]; //x of player
							yMin = nearbyPlayers.get(i)[1]; //y of player
							xMinDifference = Math.abs(x - nearbyPlayers.get(i)[0]);
							yMinDifference = Math.abs(y - nearbyPlayers.get(i)[1]);
							distance = xMinDifference + yMinDifference;
						}
					}
					if(xMinDifference != 0) {
						if(xMin < x)
							moveLeft();
						else
							moveRight();
					}
					else { //if y != 0
						if(yMin < y)
							moveUp();
						else
							moveDown();
					}
				}
			}
		}

		else if(Math.abs(water-food) < MAX_FOOD_WATER_DIFF && nearbyWater.size() >= 1 && energy <= ENERGY_UPPER_THRESHOLD)
		{
			if(GUI.detectWater(x, y)) //if water is right next to player
				drink();
			else //finds nearest water
			{
				int xDifference=Math.abs(x-nearbyWater.get(0)[0]);
				int yDifference=Math.abs(y-nearbyWater.get(0)[1]);
				int xMinDifference=xDifference;
				int yMinDifference=yDifference;
				int distance=xMinDifference+yMinDifference;
				int xMin=nearbyWater.get(0)[0];
				int yMin=nearbyWater.get(0)[1];
				for(int i=1; i<nearbyWater.size(); i++)
				{
					xDifference=Math.abs(x-nearbyWater.get(i)[0]);
					yDifference=Math.abs(y-nearbyWater.get(i)[1]);
					if(xDifference+yDifference<distance)
					{
						xMin=nearbyWater.get(i)[0];
						yMin=nearbyWater.get(i)[1];
						xMinDifference=xDifference;
						yMinDifference=yDifference;
						distance=xMinDifference+yMinDifference;
					}
				}
				if(xMinDifference!=0)
				{
					if(xMin<x)
						moveLeft();
					else
						moveRight();
				}
				else
				{
					if(yMin<y)
						moveUp();
					else
						moveDown();
				}
			}
		}

		else if(nearbyFood.size() >= 1 && energy <= ENERGY_UPPER_THRESHOLD)
		{
			if(GUI.detectFood(x, y)) //if food is right next to player
				eat();
			else //finds nearest food
			{
				int xDifference=Math.abs(x-nearbyFood.get(0)[0]);
				int yDifference=Math.abs(y-nearbyFood.get(0)[1]);
				int xMinDifference=xDifference;
				int yMinDifference=yDifference;
				int distance=xMinDifference+yMinDifference;
				int xMin=nearbyFood.get(0)[0];
				int yMin=nearbyFood.get(0)[1];
				for(int i=1; i<nearbyFood.size(); i++)
				{
					xDifference=Math.abs(x-nearbyFood.get(i)[0]);
					yDifference=Math.abs(y-nearbyFood.get(i)[1]);
					if(xDifference+yDifference<distance)
					{
						xMin=nearbyFood.get(i)[0];
						yMin=nearbyFood.get(i)[1];
						xMinDifference=xDifference;
						yMinDifference=yDifference;
						distance=xMinDifference+yMinDifference;
					}
				}
				if(xMinDifference!=0)
				{
					if(xMin<x)
						moveLeft();
					else
						moveRight();
				}
				else
				{
					if(yMin<y)
						moveUp();
					else
						moveDown();
				}
			}
		}

		else //moves randomly
		{
			int movement = (int)(Math.random() * 4 + 1);
			if(active) 
			{
				if(movement == 1)
					moveUp();
				else if(movement == 2)
					moveDown();
				else if(movement == 3)
					moveLeft();
				else
					moveRight();
			}
		}
		
		active = false;
	}
	
   //post: returns the toString of the player (name, type, kills, food, water, xp)
	public String toString() {
		return name + " (" + type + ") " + ": " + kills + " - Food: " + food + " - Water: " + water + " - XP: " + xp;
	}
}