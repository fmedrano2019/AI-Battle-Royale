import java.util.*;
import javax.swing.*;
import java.awt.Toolkit;

public class Driver {
	public static void main(String[] args) {
		boolean USE_CROSS_PLATFORM_UI = true;
		if (USE_CROSS_PLATFORM_UI) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		JFrame frame = new JFrame("Battle Royale");
      Toolkit tk = Toolkit.getDefaultToolkit();
      int xSize = ((int) tk.getScreenSize().getWidth());
      int ySize = ((int) tk.getScreenSize().getHeight());
		frame.setSize(xSize, ySize);
		frame.setLocation(200, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new GUI());
		frame.setVisible(true);
	}
}