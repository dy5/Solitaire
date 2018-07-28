package Solitaire;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

/* top level class of a solitaire game */
public class Solitaire extends JPanel implements ActionListener {
	
	private Vector<Column> column; // all 7 of the columns
	private Vector<AceColumn> aceColumn; //all 4 of the ace columns
	private Deck deck; //the 52 card deck
	private DeckColumn deckColumn; //the rest of the deck after dealing to the 7 columns
	private int numFullAces; //4 means you won
	
	/* The selector interface */
	private JRadioButton drawOne, drawThree;
	private ButtonGroup btnGroup;
	private JButton btnDeal;
	private JPanel selectPanel;
	private JButton btnReset;
	
	/* The rest is state information */
	private Card oldCard;
	private enum State
	{
		NOTHINGSELECTED, COLUMNSELECTED,
		ACESELECTED, DECKSELECTED, PREGAME
	}
	private enum WhatClicked
	{
		ACECOLUMN, COLUMN, EMPTYCOLUMN,
		EMPTYACE, DECKFACEUP, DECKFACEDOWN,
		EMPTYDECKCOLUMN
	}
	private State state;
	private WhatClicked whatClicked;
	 
	/* construct a solitaire game */
	public Solitaire() 
	{
		
		Card ceddfdf = new Card(Card.Suit.CLUBS, Card.Rank.ACE);
		/* set up data structures */
		column = new Vector<Column>();
		aceColumn = new Vector<AceColumn>();
		deck = new Deck();
		deckColumn = new DeckColumn();
		
		/* set up the panel */
		setSize(625, 600);
		setLayout(null);
		
		/* initialize columns */
		int i, j;
		Card c;
		for (i=0; i<7; i++)
		{
			column.add(i, new Column(i+1));
			column.elementAt(i).addALtoXX(this);
			add(column.elementAt(i));
		}
		
		/* initialize ace columns */
		for (i=0; i<4; i++)
		{
			aceColumn.add(i, new AceColumn(i));
			aceColumn.elementAt(i).addALtoXX(this);
			add(aceColumn.elementAt(i));
		}
		
		/* initalize the deck column */
		add(deckColumn);
		deckColumn.addALtOXX(this);
				
		/* pick draw three or draw 1 setup */
		drawOne = new JRadioButton("Draw 1", false);
		drawThree = new JRadioButton("Draw 3", true);
		btnDeal = new JButton("Deal");
		btnDeal.addActionListener(this);
		btnReset = new JButton("Reset");
		btnReset.setBounds(545, 0, 75, 17);
		btnReset.addActionListener(this);
		btnReset.setActionCommand("reset");
		btnGroup = new ButtonGroup();
		btnGroup.add(drawOne);
		btnGroup.add(drawThree);
		selectPanel = new JPanel();
		selectPanel.setBorder(BorderFactory.createTitledBorder("Select game"));
		selectPanel.setBounds(200, 200, 200, 100);
		selectPanel.add(drawOne);
		selectPanel.add(drawThree);
		selectPanel.add(btnDeal);
		add(selectPanel);
		add(btnReset);
		
		/* set the starting state */
		setNewState(state.PREGAME, null);
	}
	
	public void dealGame(int type)
	{
		int i, j;
		Card c;
		deck.populate();
		deck.shuffle();
		for (i=0; i<7; i++)
		{
			column.elementAt(i).reset();
			for (j=0; j<i+1; j++)
			{
				c=deck.popTop();
				c.addActionListener(this);
				c.setActionCommand("column");
				column.elementAt(i).acceptDealtCard(c);
			}
			column.elementAt(i).flip();
		}
		numFullAces = 0;

		for (i=0; i<4; i++)
			aceColumn.elementAt(i).reset();
		
		deckColumn.reset();
		while (!deck.isEmpty())
		{
			c=deck.popTop();
			c.addActionListener(this);
			deckColumn.acceptDealtCard(c);
		}
		deckColumn.firstRun();
		deckColumn.setDrawNum(type);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		String cmd = e.getActionCommand(); //everything clickable has an action command
		Column col1, col2;
		AceColumn acecol;
		JButton btn;
		Card card;
		
		/* get the WhatClicked state info */
		if ("reset".equals(cmd))
		{
			setNewState(state.PREGAME, null);
			return;
		}
		else if ("column".equals(cmd))
			whatClicked = whatClicked.COLUMN;
		else if ("aceColumn".equals(cmd))
			whatClicked = whatClicked.ACECOLUMN;
		else if ("emptyColumn".equals(cmd))
			whatClicked = whatClicked.EMPTYCOLUMN;
		else if ("emptyAceColumn".equals(cmd))
			whatClicked = whatClicked.EMPTYACE;
		else if ("deckFaceUp".equals(cmd))
			whatClicked = whatClicked.DECKFACEUP;
		else if ("deckFaceDown".equals(cmd))
			whatClicked = whatClicked.DECKFACEDOWN;
		else
			whatClicked = whatClicked.EMPTYDECKCOLUMN;
			
		/* Nested switch statements based on the State and WhatClicked states. */
		switch (state)
		{
			case NOTHINGSELECTED: //nothing is selected
				switch (whatClicked)
				{
					case COLUMN: //if nothing is selected and user clicked a column
						card = (Card) e.getSource();
						if (card.getMode()!=3)
							setNewState(state.COLUMNSELECTED, card);
						else setNewState(state.NOTHINGSELECTED, null);
						break;
					case ACECOLUMN: //if nothing is selected and user clicked an acecolumn
						setNewState(state.ACESELECTED, (Card)e.getSource()); 
						break;
					case EMPTYDECKCOLUMN: //etc..
						deckColumn.resetDeck(0);
						setNewState(state.NOTHINGSELECTED, null);
						break;
					case DECKFACEDOWN:
						deckColumn.flip();
						setNewState(state.NOTHINGSELECTED, null);
						break;
					case DECKFACEUP:
						card = (Card) e.getSource();
						if (card.getMode()==0)
							setNewState(state.DECKSELECTED, (Card)e.getSource());
						else setNewState(state.NOTHINGSELECTED, null);
						break;
					default: 
						setNewState(state.NOTHINGSELECTED, null);
				}
				break;
			case COLUMNSELECTED: //cards in a column are selected
				switch (whatClicked)
				{
					case COLUMN:
						card = (Card) e.getSource();
						col1 = (Column)oldCard.getParent(); //source
						col2 = (Column)card.getParent(); //dest
						if (card.equals(oldCard) && col2.isLast(card))
						{
							if (moveAce(null, oldCard)) /* double click card - move to correct ace */
								col1.removeSelectedCards();
						}
						else
							move(col1, col2);
						break;
					case ACECOLUMN:
						card = (Card) e.getSource();
						col1 = (Column) oldCard.getParent();
						acecol = (AceColumn)card.getParent();
						if (moveAce(acecol, oldCard))
							col1.removeSelectedCards();
						break;
					case EMPTYCOLUMN:
						btn = (JButton) e.getSource();
						col1 = (Column) oldCard.getParent(); //source
						col2 = (Column) btn.getParent(); //dest
						move(col1, col2);
						break;
					case EMPTYACE:
						btn = (JButton) e.getSource();
						col1 = (Column)oldCard.getParent();
						acecol = (AceColumn)btn.getParent();
						if (moveAce(acecol, oldCard))
							col1.removeSelectedCards();
						break;
				}
				setNewState(state.NOTHINGSELECTED, null);
				break;
			case ACESELECTED: //card in an acecolumn is selected
				switch (whatClicked)
				{
					case COLUMN:
						card = (Card) e.getSource();
						col1 = (Column) card.getParent();
						acecol = (AceColumn) oldCard.getParent();
						moveFromAce(acecol, col1);
						break;
					case EMPTYCOLUMN:
						btn = (JButton) e.getSource();
						col1 = (Column) btn.getParent();
						acecol = (AceColumn) oldCard.getParent();
						moveFromAce(acecol, col1);
						break;
					case EMPTYACE:
						btn = (JButton) e.getSource();
						acecol = (AceColumn) btn.getParent();
						AceColumn source = (AceColumn) oldCard.getParent();
						moveFromAcetoAce(source, acecol);
						break;
				}
				setNewState(state.NOTHINGSELECTED, null);
				break;
			case DECKSELECTED: //card in the deck column is selected
				switch (whatClicked)
				{
					case COLUMN:
						card = (Card) e.getSource();
						col1 = (Column) card.getParent();
						if (move(oldCard, col1))
							deckColumn.removeSelected();
						break;
					case EMPTYCOLUMN:
						btn = (JButton) e.getSource();
						col1 = (Column) btn.getParent();
						if (move(oldCard, col1))
							deckColumn.removeSelected();
						break;
					case ACECOLUMN:
						card = (Card) e.getSource();
						acecol = (AceColumn) card.getParent();
						if (moveAce(acecol, oldCard))
							deckColumn.removeSelected();
						break;
					case EMPTYACE:
						btn = (JButton) e.getSource();
						acecol = (AceColumn) btn.getParent();
						if (moveAce(acecol, oldCard))
							deckColumn.removeSelected();
						break;
					case DECKFACEUP:
						if (moveAce(null, oldCard))
							deckColumn.removeSelected();
						break;
				}
				setNewState(state.NOTHINGSELECTED, null);
				break;
			case PREGAME:
				int k;
				if (drawOne.isSelected())
					k=1;
				else
					k=3;
				dealGame(k);
				showGame(true);
				repaint();
				setNewState(state.NOTHINGSELECTED, null);
				break;
		}
	}
	
	private void showGame(boolean flag)
	{
		int i;
		for (i=0; i<7; i++)
			column.elementAt(i).setVisible(flag);
		for (i=0; i<4; i++)
			aceColumn.elementAt(i).setVisible(flag);
		deckColumn.setVisible(flag);
		btnReset.setVisible(flag);
		selectPanel.setVisible(!flag);
	}
	
	/* sets the new state of the solitaire game */
	private void setNewState(State s, Card card)
	{
		state = s;
		Column col;
		AceColumn acecol;
		int i;
		switch (s)
		{
			case NOTHINGSELECTED:
				for (i=0; i<7; i++)
					column.elementAt(i).deselectAll();
				for (i=0; i<4; i++)
					aceColumn.elementAt(i).deselectAll(); 
				deckColumn.deselectAll();
				oldCard = null;
				if (numFullAces==4) //call YOU WIN function
					declareWinner();
				break;
			case COLUMNSELECTED:
				oldCard = card;
				col = (Column)card.getParent();
				if (col.needsFlipped())
				{
					col.flip();
					setNewState(state.NOTHINGSELECTED, null);
				}
				else col.selectCardsRelatedTo(card);
				break;
			case ACESELECTED:
				oldCard = card;
				acecol = (AceColumn)card.getParent();
				acecol.selectTop();
				break;
			case DECKSELECTED:
				oldCard = card;
				deckColumn.selectTop();
				break;
			case PREGAME:
				showGame(false);
				break;
		}
	}
	
	/* move cards from source column to destination column */
	private boolean move(Column source, Column dest)
	{
		return dest.acceptCardsFrom(source);
	}
	
	/* move one card (from the deck column) to a column */	
	private boolean move(Card sourceCard, Column dest)
	{
		return dest.acceptCard(sourceCard);
	}
	
	/* move card to ace. if acecol is null, move it to the correct one */
	private boolean moveAce(AceColumn acecol, Card card)
	{
		boolean flag = false;

		if (acecol==null) //try to put it in first one that accepts it
		{
			int i;
			for (i=0; i<4; i++)
			{
				flag = aceColumn.elementAt(i).acceptCard(card);
				if (flag) break;
			}
			if (i<4 && aceColumn.elementAt(i).isFull()) numFullAces++;
		}
		else //try to put it in the specified one
		{
			flag = acecol.acceptCard(card);
			if (acecol.isFull()) numFullAces++;
		}
		return flag;
	}
	
	/* move cards from the acecolumn to the column */
	private boolean moveFromAce(AceColumn acecol, Column col)
	{
		/* move card from ace to column */
		if (col.acceptCard(acecol.getTop()))
		{
			if (acecol.getTop().getRank()==Card.Rank.KING) //idiot pulled a king off
				numFullAces--;
			acecol.popTop();
			return true;
		}
		else return false;
	}
	
	/* move card (must be an ace!) from one acecolumn to another */
	private boolean moveFromAcetoAce(AceColumn source, AceColumn dest)
	{
		if (moveAce(dest, source.getTop()))
		{
			source.popTop();
			return true;
		}
		return false;
	}
	
	/* display a pop up box declaring the user the winner */
	private void declareWinner()
	{
		ActionListener btnPressed = new ActionListener()
		{
			JButton btn;
			JDialog win = new JDialog();
			public final boolean equals(Object o) /*overload equals just for kicks */
			{
				if (super.equals(o))
				{
					win.setBounds(100,100,100,100);
					win.setModal(true);
					win.setTitle("Winner");
					win.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
					win.setResizable(false);
					JLabel lbl = new JLabel("YOU WON!!!");
					btn = new JButton("Sweet!");
					btn.addActionListener(this);
					win.getContentPane().add(btn,"South",0);
					win.getContentPane().add(lbl,"North",0);
					win.setVisible(true);
				}
				return super.equals(o); //just in case its needed
			}
			public void actionPerformed(ActionEvent e)
			{
				win.dispose();
			}
		};
		btnPressed.equals(btnPressed);
		setNewState(state.PREGAME, null);
		
	}
}
