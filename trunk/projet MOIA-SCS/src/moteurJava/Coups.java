/**
 * @author Guillaume MONTAVON & Benoit MEILHAC (Master 1 Informatique)
 * Gestion d'un coup joue soit par notre joueur, soit par l'adversaire
 */
public class Coups {
	
	private int typeCoups;
	private Position caseDepart;
	private Position caseArrivee;
	private Position piecePrise2;

	/**
	 * Constructeur
	 * @param typeCoups type de coup (defini dans la classe modele)
	 * @param x1 coordonnee en X de la case de depart
	 * @param y1 coordonnee en Y de la case de depart
	 * @param x2 coordonnee en X de la case d'arrivee
	 * @param y2 coordonnee en Y de la case d'arrivee
	 * @param x2emePion coordonnee en X du deuxieme pion pris
	 * @param y2emePion coordonnee en Y du deuxieme pion pris
	 */
	public Coups(int typeCoups, int x1, int y1, int x2, int y2, int x2emePion, int y2emePion) {
		this.typeCoups = typeCoups;
		caseDepart = new Position(x1, y1);
		caseArrivee = new Position(x2, y2);
		piecePrise2 = new Position(x2emePion, y2emePion);
	}
	
	/**
	 * Deuxieme constructeur
	 * @param rep coup sous forme de chaine de caracteres qui sera donc converti en un objet de ce type
	 */
	public Coups(String rep) {
		
		try {
		
		String[] tabReq = rep.split("-");
		typeCoups = Integer.valueOf(tabReq[0]);
		
		String[] tabPosition;
		if(tabReq.length>1) {
			tabPosition = tabReq[1].replace("[", "").replace("]", "").split(",");
			caseDepart = new Position(Integer.valueOf(tabPosition[0]), Integer.valueOf(tabPosition[1]));
		}
		else
			caseDepart = new Position(0, 0);
		
		if(tabReq.length>2) {
			tabPosition = tabReq[2].replace("[", "").replace("]", "").split(",");
			caseArrivee = new Position(Integer.valueOf(tabPosition[0]), Integer.valueOf(tabPosition[1]));
		}
		else
			caseArrivee = new Position(0, 0);
		
		if(tabReq.length>3) {
			tabPosition = tabReq[3].replace("[", "").replace("]", "").split(",");
			piecePrise2 = new Position(Integer.valueOf(tabPosition[0]), Integer.valueOf(tabPosition[1]));
		}
		else
			piecePrise2 = new Position(0, 0);
		}
		catch (Exception e) {
			typeCoups = -1;
			caseDepart = new Position(-1, -1);
			caseArrivee = new Position(-1, -1);
			piecePrise2 = new Position(-1, -1);
			System.out.println("JAVA : erreur, le message recu n'a pas le bon format");
		}
	}

	public int getTypeCoups() {
		return typeCoups;
	}

	public int getX1() {
		return caseDepart.getX();
	}

	public int getY1() {
		return caseDepart.getY();
	}

	public int getX2() {
		return caseArrivee.getX();
	}

	public int getY2() {
		return caseArrivee.getY();
	}

	public int getX2emePion() {
		return piecePrise2.getX();
	}

	public int getY2emePion() {
		return piecePrise2.getY();
	}
	
	public Position getCaseDepart() {
		return caseDepart;
	}

	public Position getCaseArrivee() {
		return caseArrivee;
	}

	public Position getPiecePrise2() {
		return piecePrise2;
	}
	
	public void setTypeCoups(int typeCoups) {
		this.typeCoups = typeCoups;
	}
	
	/**
	 * Converti un coup en une chaine de caracteres qui contient les informations, chacune separee par des tirets.
	 * Ce dernier pourra etre envoye au joueur afin qu'il le convertisse a nouveau pour l'envoyer au serveur
	 * @return coup converti en une chaine de caracteres
	 */
	public String getReq() {
		return typeCoups + "-" + caseDepart.toString() + "-" + caseArrivee.toString() + "-" + piecePrise2.toString();
	}
	
}
