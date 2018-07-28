package Solitaire;

import javax.swing.JPanel;
import java.util.Vector;
import javax.swing.JButton;
import java.awt.event.ActionListener;

/* This class if the part of a solitaire game that
 * contains the remaining cards of the deck that 
 * can be flipped and played.  It supports draw 1
 * and draw 3 methods of play. */
public class DeckColumn extends JPanel {
	
	private Vector<Card>faceUp;
	private Vector<Card>faceDown;
	private JButton btnReset;
	private int drawNum;
	
	/* initialize a deck column */
	public DeckColumn()
	{
		super();
		setSize(Card.WIDTH*2+10+Card.WIDTH_PART*2, Card.HEIGHT);
		setLocation(20,25);
		setLayout(null);
		faceUp = new Vector<Card>();
		faceDown = new Vector<Card>();
		btnReset = new JButton("XX");
		btnReset.setBounds(Card.WIDTH/2-25,Card.HEIGHT/2-10,50,20);
		btnReset.setActionCommand("emptyDeckColumn");
		add(btnReset);
	}
	
	/* add an action listener to the empty button */
	public void addALtOXX(ActionListener al)
	{
		btnReset.addActionListener(al);
	}
	
	/* set the draw number */
	public void setDrawNum(int d)
	{
		drawNum = d;
	}
	
	/* reset the deck column */
	public void reset()
	{
		faceUp.removeAllElements();
		faceDown.removeAllElements();
		removeAll();
		add(btnReset);
		btnReset.setVisible(false);
	}
	
	/* called from above when the game is ready to play */
	public void firstRun()
	{
		resetDeck(0);
	}
	
	/* either draw 1 or draw 3 depending on the set mode */
	public boolean flip()
	{
		if (drawNum==1||faceUp.size()+faceDown.size()<3) 
			return drawOne();
		else return drawThree(3,10+Card.WIDTH);
	}
	
	/* accept the card dealt to the deck column and return true
	 * returns false if the card was null */
	public boolean acceptDealtCard(Card c)
	{
		if (c!=null) 
		{
			c.setActionCommand("deckFaceDown");
			c.setVisible(false);
			c.setLocation(0,0);
			c.setMode(2);
			add(c);
			faceDown.addElement(c);
			return true;
		}
		else return false;
	}
	
	/* select the top card of the deck column */
	public boolean selectTop()
	{
		if (!faceUp.isEmpty())
		{
			faceUp.firstElement().setSelected(true);
			return true;
		}
		else return false;
	}
	
	/* return the selected (top card) of the deck */
	public Card getSelected()
	{
		if (!faceUp.isEmpty() && faceUp.firstElement().isSelected())
		{
			return faceUp.firstElement();
		}
		else return null;
	}
	
	/* remove the selected (top card) of the deck */
	public void removeSelected()
	{
		Card c = getSelected();
		if (c!=null)
		{
			remove(c);
			faceUp.removeElement(c);
		}
		if (!faceUp.isEmpty())
		{
			faceUp.get(0).setVisible(true);
			faceUp.get(0).setMode(0);
		}
	}
	
	/* deselect the top card of the deck */
	public boolean deselectAll()
	{
		if (!faceUp.isEmpty())
		{
			faceUp.firstElement().setSelected(false);
			return true;
		}
		return false;
	}
	
	/* the deck is reset (cards from faceup put into facedown)
	 * except for N cards that are kept in faceup */
	public boolean resetDeck(int N)
	{
		Card c;
		if (!faceDown.isEmpty() || !faceUp.isEmpty())
		{
			btnReset.setVisible(false);
			while (faceUp.size()>N)
			{
				c=faceUp.remove(faceUp.size()-1); //remove the last element
				c.setMode(2);
				c.setVisible(false);
				c.setLocation(0,0);
				c.setActionCommand("deckFaceDown");
				faceDown.add(c);
			}
			faceDown.get(0).setVisible(true);
			return true;
		}
		return false;
	}
	
	/* method for drawing 1 card */
	private boolean drawOne()
	{
		Card c;
		if (faceDown.isEmpty())
			return resetDeck(0);
		else
		{
			if (!faceUp.isEmpty()) faceUp.get(0).setVisible(false);
			c = faceDown.remove(0);
			c.setVisible(true);
			c.setMode(0);
			c.setLocation(Card.WIDTH+10, 0);
			c.setActionCommand("deckFaceUp");
			faceUp.add(0, c);
			if (!faceDown.isEmpty())
				faceDown.get(0).setVisible(true);
			else 
				btnReset.setVisible(true);
			return true;
		}
	}
	
	/* method for drawing three cards
	 * Note: if only 2 cards are left in the facedown
	 * pile, this method will draw both of them,
	 * then reset the deck, leaving those 2 and then draw
	 * the third one. This is a recursive function. */
	private boolean drawThree(int C, int cardx)
	{
		Card c;
		int counter = 3-C, i;
		for(i=0; i<faceUp.size() && i<3 && C==3; i++)
		{
			faceUp.get(i).setVisible(false);
			faceUp.get(i).setLocation(Card.WIDTH+10,0);
		}
		
		while(!faceDown.isEmpty() && counter<3)
		{
			c = faceDown.remove(0);
			c.setMode(4);
			c.setVisible(true);
			c.setActionCommand("deckFaceUp");
			c.setLocation(cardx,0);
			cardx+=Card.WIDTH_PART;
			faceUp.add(0,c);
			counter++;
		}
		
		if (counter<3)
		{
			if (faceUp.size()>=3)
				resetDeck(counter);
			else resetDeck(0);
			return drawThree(3-counter, cardx); /* recursive call */
		}
		else 
		{
			if (!faceUp.isEmpty())
				faceUp.get(0).setMode(0);
				
			if (!faceDown.isEmpty())
				faceDown.get(0).setVisible(true);
			else btnReset.setVisible(true);
		}
		return true;
	}
}
