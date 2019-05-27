import java.awt.*;
import javax.swing.*;

public abstract class Player {
	
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
	public int[][] nearby = new int[121][3];
	
	/** CONSTRUCTORS **/
	
	//no coordinate constructor
	public Player(String n, String t) {
		name = n;
		type = t;
		kills = 0;
		xp = 0;
		food = 40;
		water = 60;
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
		food = 40;
		water = 60;
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
		if(!a) {
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
	
	public int superiorType(Player other)
	{
		if(type.equals(other.getType()))
			return 0;
		else if((type.equals("S&S")&&other.getType().equals("2H"))||(type.equals("2H")&&other.getType().equals("Dagger"))||(type.equals("Dagger")&&other.getType().equals("S&S")))
			return 50;
		else
			return -50;
	}

	public boolean isWinner(Player other)
	{
		double score=0;
		double otherScore=0;

		int typeWinner=superiorType(other);

		score+=typeWinner;
		otherScore+=typeWinner;

		double energyDifference=0;
		if(energy>other.getEnergy())
			energyDifference=energy-other.getEnergy();
		else
			energyDifference=other.getEnergy()-energy;

		energyDifference=Math.exp(energyDifference);
		energyDifference=Math.pow(energyDifference, 1.0/45.0);

		score+=energyDifference;
		otherScore-=energyDifference;

		double xpDifference=0;
		if(xp>other.getXP())
			xpDifference=xp-other.getXP();
		else
			xpDifference=other.getXP()-xp;

		xpDifference=Math.exp(xpDifference);
		xpDifference=Math.pow(xpDifference, 1.0/60.0);

		score+=xpDifference;
		otherScore-=xpDifference;

		return score>=otherScore;
	}

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

	public void eat()
	{
		food+=15;
		energy+=15;
		xp += 5;
	}

	public void drink()
	{
		water+=20;
		energy+=20;
		xp += 10;
	}   

	public abstract Color getColor();
	
	public int[][] sight() {
		int [][] cells=GUI.getCells();
		int count = 0;
		for(int r = y - 5; r <= y + 5; r++) {
			for(int c = x - 5; c <= x + 5; c++) {
				if(r >= 0 && r < cells.length && c >= 0 && c < cells[0].length) { //checks if r & c are within bounds
					nearby[count][0]=c;
					nearby[count][1]=r;
					nearby[count][2]=GUI.getSpace(c, r);
				}
				count++;
			}
		}
		
		return nearby;
	}
	
	public String toString() {
		return name + " (" + type + ") " + ": " + kills + " - Food: " + food + " - Water: " + water + " - XP: " + xp;
	}
}
