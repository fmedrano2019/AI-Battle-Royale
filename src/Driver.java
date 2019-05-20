import java.util.*;
import javax.swing.*;

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
		frame.setSize(1230, 1000);
		frame.setLocation(200, 0);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new GUI());
		frame.setVisible(true);
		
	}
}
