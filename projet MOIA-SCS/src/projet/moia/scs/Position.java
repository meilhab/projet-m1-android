package projet.moia.scs;
import java.util.Comparator;


public class Position {

	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
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

	@Override
	public String toString() {
		return "[" + x + "," + y + "]";
	}
	
}

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
