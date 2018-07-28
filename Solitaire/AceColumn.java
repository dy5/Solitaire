package Solitaire;

import javax.swing.JPanel;
import javax.swing.JButton;
import java.util.Vector;
import java.awt.event.ActionListener;

/* One of the four spaces in a solitaire game that
 * can accept an ace and then all the cards of that
 * ace's suit. */
public class AceColumn extends JPanel {
	
	private Vector<Card>myPile; //all the cards in the ace column
	private Card.Suit mySuit;
	private JButton emptyColumn;
	
	/* Constructor. Takes an integer that determines the location
	 * of the ace column */
	public AceColumn(int loc)
	{
		super();
		setLayout(null);
		setLocation(loc*75+75*4, 25);
		setSize(75, 100); //close to a single card's size
		myPile = new Vector<Card>();
		emptyColumn = new JButton("XX");
		emptyColumn.setBounds(73/2-25,20,50,20);
		emptyColumn.setActionCommand("emptyAceColumn");
		emptyColumn.setFocusable(false);
		emptyColumn.setRolloverEnabled(true);
		emptyColumn.setVisible(true);
		add(emptyColumn);
	}
	
	/* Enables anything to add an action listener to the
	 * empty button of an ace column */
	public void addALtoXX(ActionListener al)
	{
		emptyColumn.addActionListener(al);
	}
	
	/* Reset the ace column */
	public void reset()
	{
		myPile.removeAllElements();
		removeAll();
		add(emptyColumn);
		emptyColumn.setVisible(true);
	}
	
	/* return the top card of the ace column
	 * if it is empty, returns null */
	public Card getTop()
	{
		if (myPile.isEmpty())
			return null;
		else return myPile.lastElement();
	}
	
	/* remove the top card (if any) of the ace column */
	public void popTop()
	{
		myPile.remove(myPile.lastElement());
		if (myPile.isEmpty())
			emptyColumn.setVisible(true);
		else myPile.lastElement().setVisible(true);
		repaint();
	}
	
	/* deselects the (only) selected card of the acecolumn */
	public void deselectAll()
	{
		if (!myPile.isEmpty())
			myPile.lastElement().setSelected(false);
	}
	
	/* selects the only selectable card of the ace column */
	public void selectTop()
	{
		if (!myPile.isEmpty())
			myPile.lastElement().setSelected(true);
	}
	
	/* If a card follows solitaire rules, the ace column accepts
	 * it and returns true.  If not just return false. */
	public boolean acceptCard(Card c)
	{
		boolean flag = false;
		if (c.getRank() == Card.Rank.ACE && isEmpty()) //first card
		{
			flag = true;
			mySuit = c.getSuit(); /* Note: suit of ace column is set here */
			emptyColumn.setVisible(false);
		}
		else if (!isEmpty() && c.getSuit()==mySuit && //all other cards
			c.getRank().getValue() == myPile.lastElement().getRank().getValue()+1)
		{
			flag = true;
			myPile.lastElement().setVisible(false);
		}
		
		if (flag)
		{
			c.setActionCommand("aceColumn");
			c.setLocation(0,0);
			myPile.add(c);
			add(c);
		}
		return flag;
	}
	
	/* returns true if the ace column has 13 cards
	 * false otherwise */
	public boolean isFull()
	{
		return myPile.size()==13;
	}
	
	/* returns true if the ace column is empty */
	public boolean isEmpty()
	{
		return myPile.isEmpty();
	}
}
