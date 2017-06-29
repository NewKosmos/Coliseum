package launcher;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class KosmosLauncher {
	public static final String VERSION_PAGE = "https://raw.githubusercontent.com/Equilibrium-Games/New-Kosmos-Versions/master/version_dev.txt";

	public static void main(String[] args) {
		VersionCheck check = new VersionCheck();

		JEditorPane jep = new JEditorPane();
		jep.setEditable(false);

		try {
			jep.setPage("http://equilibrium.games/");
		} catch (IOException e) {
			jep.setContentType("text/html");
			jep.setText("<html>Could not load website. Please check your internet connection.</html>");
		}

		JScrollPane scrollPane = new JScrollPane(jep);
		JFrame f = new JFrame("New Kosmos Launcher");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(scrollPane);
		f.setSize(new Dimension(800, 600));
		f.setVisible(true);

		while (true) {
			// Dank memes
		}
	}
}
