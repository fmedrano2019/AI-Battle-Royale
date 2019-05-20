public abstract class Player {
	
	public String name;
	public String type;
	public int kills;
	public int xp;
	public int food;
	public int water;
	public int energy;
	public boolean isAlive;
	
	/** CONSTRUCTORS **/
	
	public Player(String n, String t) {
		name = n;
		type = t;
		kills = 0;
		xp = 0;
		food = 50;
		water = 50;
		energy = food + water;
		isAlive = true;
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
		return kills;
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
	
	//post: returns isAlive
	public boolean getIsAlive() {
		return isAlive;
	}
	
	//post: sets isAlive
	public void setIsAlive(boolean a) {
		isAlive = a;
	}
	
	public String toString() {
		return name + " (" + type + ") " + ": " + kills;
	}
}