public class TwoHanded extends Player {

   //no coordinate constructor
   public TwoHanded(String n)
   {
      super(n, "2H");
   }

   //constructor with coordinates
	public TwoHanded(String n, int x, int y) {
		super(n, "2H", x, y);
	}
}