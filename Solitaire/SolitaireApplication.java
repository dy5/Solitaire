package Solitaire;

import javax.swing.JFrame;

public class SolitaireApplication extends JFrame {
	public SolitaireApplication()
	{
		super("Solitaire by Dan York");
		setResizable(false);
		setSize(625, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Solitaire s = new Solitaire();
		add(s);
		setVisible(true);
	}
	
	public static void main(String[] args) 
	{
		SolitaireApplication sa = new SolitaireApplication();
	}	
}
