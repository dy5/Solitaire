package Solitaire;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;
import javax.imageio.ImageIO;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Point;

/* One regular playing card from a 52 card deck */
public class Card extends JButton {
	/* Enum Rank for the ranking of Cards */	
	public static enum Rank 
	{
		ACE(1), DUECE(2), THREE(3), FOUR(4), 
		FIVE(5), SIX(6), SEVEN(7), 
		EIGHT(8), NINE(9), TEN(10), 
		JACK(11), QUEEN(12), KING(13);
			
		private final String s, fn;
		private final int v;
		Rank(int r)
		{
			switch (r)
			{
				case 1: s="Ace"; fn="a"; break;
				case 10: s="10"; fn="t"; break;
				case 11: s="Jack"; fn="j"; break;
				case 12: s="Queen"; fn="q"; break;
				case 13: s="King"; fn="k"; break;
				default: s=fn=String.valueOf(r);
			}
			v=r;
		}
		public String toString() { return s; }
		public int getValue() { return v; }
		private String getFilename() { return fn; }
	}
	
	/* Enum Suit for the suiting of cards */
	public static enum Suit
	{
		CLUBS(0), DIAMONDS(1), SPADES(2), HEARTS(3);
		private final String s, fn;
		private final int v;
		Suit (int val)
		{
			switch(val)
			{
				case 0: s="Clubs"; fn="c"; break;
				case 1: s="Diamonds"; fn="d"; break;
				case 2: s="Spades"; fn="s"; break;
				case 3: s="Hearts"; fn="h"; break;
				default: s=""; fn="";
			}
			v = val;
		}
		public String toString() { return s; }
		private String getFilename() { return fn; }
		public int getValue() { return v; }
	}
	
	/* internal card variables */
	private boolean selected = false;
	private Suit suit;
	private Rank rank;
	public static final int WIDTH = 73, HEIGHT = 97; /* normal card */
	public static final int HEIGHT_PART_FRONT = 30; /* part of top of face up card */
	public static final int HEIGHT_PART_BACK = 5; /* part of top of face down card */
	public static final int WIDTH_PART = 25; /* part of side of draw three card */
	private int theMode;

	
	/* selected and unselected border */
	private static final Border selectedBorder = 
		BorderFactory.createLineBorder(Color.YELLOW, 2);
	private static final Border unSelectedBorder = 
		BorderFactory.createEmptyBorder();
		
	/* front and back imageIcons
	 * the back is initialized once for all cards
	 */
	private ImageIcon fullFront, partFront, sideFront;
	private BufferedImage frontimg;
	private static final BufferedImage backimg = getImage("images/b.gif");
	private static final ImageIcon fullBack = 
		createImageIcon(backimg);
	private static final ImageIcon partBack = 
		createSubIcon(backimg, WIDTH, HEIGHT_PART_BACK);
	private static final BufferedImage allCards = getImage("images/allcards.png");
		

	/* Card constructor */
	public Card(Suit s, Rank r) {
		suit = s;
		rank = r;
		setRolloverEnabled(false);
		setFocusable(false);
		frontimg = getImage(); /* returns the one card from all the cards */
		fullFront = createImageIcon(frontimg);
		partFront = createSubIcon(frontimg, WIDTH, HEIGHT_PART_FRONT);
		sideFront = createSubIcon(frontimg, WIDTH_PART, HEIGHT);
		setMode(0);
		setBorder(unSelectedBorder);
	}

	/* If path is a path to an image, returns the image.
	 * Otherwise returns null
	 */
	private static BufferedImage getImage(String path)
	{
		BufferedImage bfi = null;
		try 
		{	
			URL imgURL = Card.class.getResource(path);
			if (imgURL != null)
				bfi = ImageIO.read(imgURL);
			else System.err.println("Couldn't find image in system: " + path);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			return bfi;
		}
	}
	
	private BufferedImage getImage()
	{
		return allCards.getSubimage(
		(rank.getValue()-1)*WIDTH,
			suit.getValue()*HEIGHT,
			WIDTH,
			HEIGHT);
	}
	
	/* creates an ImageIcon from an image */
	private static ImageIcon createImageIcon(BufferedImage bfi) {
		if (bfi==null) return null;
		return new ImageIcon(bfi);
	}
	
	/* creates an Image Icon from part of an image */
	private static ImageIcon createSubIcon(BufferedImage bfi, int x, int y)
	{
		if (bfi==null) return null;
		return new ImageIcon(bfi.getSubimage(0,0,x,y));
	}
	
	/* Returns true if the card is red (hearts or diamonds) */
	public boolean isRed()
	{
		if (suit==suit.HEARTS || suit==suit.DIAMONDS)
			return true;
		else return false;
	}
	
	/* Returns true if the card is selected */
	public boolean isSelected() {
		return selected;
	}

	/* Set or clear a card as selected */
	public void setSelected(boolean flag) {
		if (flag) setBorder(selectedBorder);
		else setBorder(unSelectedBorder);
		selected = flag;
	}
	
	/* Sets the size and image the card.
	 * 0 is a full size front
	 * 1 is the top part the front
	 * 2 is the full size back
	 * 3 is the top sliver of the back
	 * 4 is the left side of the front
	 */
	public void setMode(int mode) 
	{
		switch(mode)
		{
			case 0:
				setSize(WIDTH, HEIGHT);
				setIcon(fullFront);
				theMode = 0;
				break;
			case 1:
				setSize(WIDTH, HEIGHT_PART_FRONT);
				setIcon(partFront);
				theMode = 1;
				break;
			case 2:
				setSize(WIDTH, HEIGHT);
				setIcon(fullBack);
				theMode = 2;
				break;
			case 3:
				setSize(WIDTH, HEIGHT_PART_BACK);
				setIcon(partBack);
				theMode = 3;
				break;
			case 4:
				setSize(WIDTH_PART, HEIGHT);
				setIcon(sideFront);
				theMode = 4;
				break;
			default:
				System.out.println("Invalid card mode");
		}
	}
	
	/* Returns the mode of the card */
	public int getMode()
	{
		return theMode;
	}

	/* Returns a card represented as a string */
	public String toString()
	{
		return rank.toString()+" of "+suit.toString();
	}

	/* Returns the suit of the card */
	public Suit getSuit() {
		return suit;
	}

	/* Returns the rank of the card */
	public Rank getRank() {
		return rank;
	}	
}
