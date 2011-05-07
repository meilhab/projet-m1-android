import java.util.Comparator;

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Represente une case sur le plateau (avec position en X et en Y)
 */
public class Position {

	private int x;
	private int y;
	
	/**
	 * Constructeur
	 * @param x coordonnee en X de la case
	 * @param y coordonnee en Y de la case
	 */
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Permet de modifier les coordonnees de la case
	 * @param x nouvelle coordonnee en X de la case
	 * @param y nouvelle coordonnee en Y de la case
	 */
	public void deplacer(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
	
}

/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Compare deux cases (classe permettant de trier les cases)
 */
class ComparateurPosition implements Comparator<Position> {
	public int compare(Position p1, Position p2){
		if(p1.getX() > p2.getX())
			return 1;
		else if(p1.getX() < p2.getX())
			return -1;
		else {
			if(p1.getY() > p2.getY())
				return 1;
			else if(p1.getY() < p2.getY())
				return -1;
			else
				return 0;
		}
	}      
}
