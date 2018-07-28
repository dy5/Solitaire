package Solitaire;

import javax.swing.JApplet;

public class SolitaireApplet extends JApplet {
	
	public void init()
	{
		setSize(625,600);
		Solitaire s = new Solitaire();
		add(s);
		setVisible(true);
	}
}
