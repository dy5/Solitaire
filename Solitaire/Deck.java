package Solitaire;

import java.util.Vector;
import java.util.Random;
import java.util.Date;
import java.util.Collections;

/* A deck of 52 playing cards */
public class Deck extends Vector<Card> {
	
	/* Deck constructor */
	public Deck() {
		//reCreate();
	}

	/* Shuffle the cards in the deck */
	public void shuffle() {
		Random rand = new Random(new Date().getTime());
		Collections.shuffle(this, rand);
	}

	/* Return and remove the top card of the deck */
	public Card popTop() {
		if (!isEmpty()) return remove(0);
		else return null;
	}

	/* Delete all cards from the deck, and add the
	 * standard unshuffled 52 cards to it.
	 */
	public void populate() {
		removeAllElements();
		for (Card.Suit suit: Card.Suit.values())
			for (Card.Rank rank: Card.Rank.values())
				add(new Card(suit, rank));
	}	
}
