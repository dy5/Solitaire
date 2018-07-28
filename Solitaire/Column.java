package Solitaire;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JPanel;

/* One of the 7 columns in a solitaire game */
public class Column extends JPanel {
	
	private int mySize;
	private int yValue;
	private Vector<Card> faceDown;
	private Vector<Card> faceUp;
	private JButton emptyColumn;
	private int columnX, columnY;
	
	/* Constructor.  Takes the column number (1-7) */
	public Column(int sz)
	{
		super();
		mySize = sz;
		yValue = 0;
		setLayout(null); /* do not use a layout manager */
		setSize(77,500);
		columnX = 75*(mySize-1)+50;
		columnY = 150;
		setLocation(columnX, columnY);
		faceDown = new Vector<Card>();
		faceUp = new Vector<Card>();
		emptyColumn = new JButton("XX");
		emptyColumn.setBounds(73/2-25,20,50,20);
		emptyColumn.setActionCommand("emptyColumn");
		emptyColumn.setFocusable(false);
		emptyColumn.setRolloverEnabled(false);
		emptyColumn.setVisible(false);
		add(emptyColumn);
	}
	
	/* Allows anything to add an action listener
	 * to the column empty button */
	public void addALtoXX(ActionListener al)
	{
		emptyColumn.addActionListener(al);
	}
	
	/* Accepts a card coming from the deck */
	public boolean acceptDealtCard(Card c)
	{
		if (faceDown.size() < mySize && faceUp.isEmpty())
		{
			c.setLocation(0,yValue); /* set location of the card in the JPanel */
			yValue+=5;
			faceDown.add(0, c); /* add it to the front of the faceDown pile */
			if (faceDown.size() == mySize)
				c.setMode(2); //fullBack
			else 
				c.setMode(3); //partBack
			add(c); /* add it to the JPanel */
			return true;
		}
		else return false;
	}
	
	/* Reset the column */
	public void reset()
	{
		faceDown.removeAllElements();
		faceUp.removeAllElements();
		removeAll();
		add(emptyColumn);
		emptyColumn.setVisible(false);
		yValue = 0;
	}
	
	
	/* flip one card from the faceDown pile to the faceUp pile
	 * only if the faceUp pile is empty */
	public boolean flip() 
	{
		Card c;
		if (needsFlipped())
		{
			c = faceDown.remove(0); /* c will already have the actionListener */
			c.setMode(0); //fullFront
			faceUp.addElement(c);
			yValue = Card.HEIGHT_PART_BACK*faceDown.size()+Card.HEIGHT_PART_FRONT*faceUp.size(); /* recalculate */
			return true;
		}
		else return false;
	}
	
	/* Returns true if the facedown card needs flipped */
	public boolean needsFlipped()
	{
		return faceUp.isEmpty() && !faceDown.isEmpty();
	}
	
	
	/* Selects all cards in the column that are ordered
	 * a solitaire fashion such as
	 * Index  Card
	 * 0		5 Diamonds
	 * 1		4 Spades
	 * 2		3 Hearts
	 */
	public boolean selectCardsRelatedTo(Card c)
	{
		if (selectNextCard(c))
			return true;
		else
		{
			deselectAll();
			return false;
		}
	}
	
	public boolean isLast(Card c)
	{
		if (!faceUp.isEmpty() && faceUp.lastElement().equals(c))
			return true;
		else return false;
	}
	
	/* A recursive function to help select
	 * all the cards that are in a solitaire fashion */
	private boolean selectNextCard(Card cCurrent)
	{
		Card cPrevious;
		Card cNext;
		
		/* base case: only 1 card */
		if (faceUp.size() == 1) 
		{
			cCurrent.setSelected(true);
			return true;
		}
		
		/* base case, if this is the last card possible */
		else if (faceUp.size()-1==faceUp.indexOf(cCurrent)) 
		{
			cPrevious = faceUp.elementAt(faceUp.indexOf(cCurrent)-1);
			if (  ( (cPrevious.isRed()&& !cCurrent.isRed())  || /* if red, then black is next */
				  (!cPrevious.isRed()&& cCurrent.isRed())  )	 /* if black, then red is next */
				  && (cPrevious.getRank().getValue() ==   /* current rank is 1 less than previous rank */
				  	cCurrent.getRank().getValue()+1))
			{
				/* final card is valid, select it */
				cCurrent.setSelected(true);
				return true;
			}
			else return false;
		}
		
		/* recursive case */
		else
		{
			cNext = faceUp.elementAt(faceUp.indexOf(cCurrent)+1);
			if (   ((cCurrent.isRed()&&!cNext.isRed())  || //check if card is valid
				   (!cCurrent.isRed()&&cNext.isRed()))
				   	&& (cCurrent.getRank().getValue() 
				   		== cNext.getRank().getValue()+1) )
			{
				/* this card is valid, but check the next */
				cCurrent.setSelected(true);
				return selectNextCard(
					faceUp.elementAt(
						faceUp.indexOf(cCurrent)+1)); /* recursive call to the next card */
			}
			else return false; /* end recursion */
		}
	}
	
	/* Returns a vector containing the column's selected cards */
	public Vector<Card> getSelectedCards()
	{
		Vector<Card> result = new Vector<Card>();
		int i;
		Card c;
		for (i=0; i<faceUp.size(); i++)
		{
			c = faceUp.elementAt(i);
			if (c.isSelected())
				result.add(c);
		}
		return result;
	}
	
	/* Deselect all the cards in the column */
	public void deselectAll()
	{
		int i;
		for (i=0; i<faceUp.size(); i++)
		{
			faceUp.elementAt(i).setSelected(false);
		}
		
	}
	
	/* If possible, accept one card and return true.
	 * Otherwise return false */
	public boolean acceptCard(Card c)
	{
		if (!faceUp.isEmpty())
		{
			Card myBottomCard = faceUp.lastElement();
			if (myBottomCard.getRank().getValue() == c.getRank().getValue()+1 &&
				((myBottomCard.isRed() && !c.isRed()) ||
				(!myBottomCard.isRed() && c.isRed())) )
				{
					myBottomCard.setMode(1);
					c.setLocation(0, yValue);
					yValue+=30;
					c.setMode(0);
					c.setActionCommand("column");
					add(c);
					faceUp.add(c);
					return true;
				}
		}
		/* if the column is empty, only accept a king */
		else if (c.getRank()==Card.Rank.KING)
		{
			c.setLocation(0,0);
			yValue = 30;
			c.setMode(0);
			c.setActionCommand("column");
			add(c);
			faceUp.add(c);
			emptyColumn.setVisible(false);
			return true;
		}
		return false;
	}
	
	public boolean acceptCardsFrom(Column source)
	{
		/* remember to accept a King on top of an empty faceUp and empty faceDown only */
		Vector<Card> otherCards = source.getSelectedCards();
		Card otherTopCard = otherCards.elementAt(0);
		Card myBottomCard, c;
		int i;
		
		/* case where column is totally empty and user sends
		 * cards starting with a king */
		if (otherTopCard.getRank()==Card.Rank.KING && faceUp.isEmpty() && faceDown.isEmpty())
		{
			/* accept all the cards */
			for (i=0; i<otherCards.size(); i++)
			{
				c = otherCards.elementAt(i);
				c.setLocation(0, yValue);
				yValue+=30;
				if (i==otherCards.size()-1) /* last card */
					c.setMode(0); //fullFront
				else c.setMode(1); //partFront
				add(c);
				faceUp.add(c);
			}
			emptyColumn.setVisible(false);
			source.removeSelectedCards(); /* remove the selected cards from the source */
			return true;
		}
		else if (needsFlipped())
			return false; // need to do a flip
		else if (faceUp.isEmpty() && faceDown.isEmpty())
			return false; //both empty, most do a king (above)
		else
		{
			myBottomCard = faceUp.lastElement();
			if (myBottomCard.getRank().getValue() == otherTopCard.getRank().getValue()+1 &&
				((myBottomCard.isRed() && !otherTopCard.isRed()) ||
				(!myBottomCard.isRed() && otherTopCard.isRed())) )
			{
				/* accept all the cards, adding them to the end */
				myBottomCard.setMode(1); //partFront
				for (i=0; i<otherCards.size(); i++)
				{
					c = otherCards.elementAt(i);
					c.setLocation(0, yValue);
					yValue+=30;
					if (i==otherCards.size()-1) /* last card */
						c.setMode(0); //fullFront
					else c.setMode(1); //partFront
					add(c);
					faceUp.add(c);
				}
				source.removeSelectedCards();
				return true;
			}
			else return false;
		}
	}
	
	/* Remove the selected cards from the column */
	public void removeSelectedCards()
	{
		Card c;
		Iterator i;
		for (i=faceUp.iterator(); i.hasNext();)
		{
			c = (Card)i.next();
			if (c.isSelected())
			{
				i.remove(); /* remove card from the vector faceUp*/
				c.setSelected(false);
				remove(c);
			}
		}
		if (faceUp.isEmpty())
		{
			if (!faceDown.isEmpty())
				faceDown.firstElement().setMode(2); //fullBack
			else emptyColumn.setVisible(true);
		}
		else faceUp.lastElement().setMode(0);
		/* Reset where cards are printed when you do this */
		yValue = Card.HEIGHT_PART_FRONT*faceUp.size() + Card.HEIGHT_PART_BACK*faceDown.size();
		repaint();
	}
}
