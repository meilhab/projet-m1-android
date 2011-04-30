package projet.moia.scs;

import java.util.*;


public class Modele {

	private ArrayList<Position> pionsJoueur1;
	private ArrayList<Position> pionsJoueur2;
	private Position anciennePositionDernierPion1;
	private Position dernierPionJoue1;
	private Position anciennePositionDernierPion2;
	private Position dernierPionJoue2;
	private int nbPionsRestantsMain1;
	private int nbPionsRestantsMain2;
	private int nbPionsMainMax;
	private int hauteur; // nombre de lignes (x)
	private int largeur; //nombre de colonnes (y)
	
    public static final int POSE = 0;
    public static final int DEPLACE = 1;
    public static final int PRISE = 2;
    public static final int NULLE = 3;
    public static final int GAGNE = 4;
    

	public Modele(int nbPionsMain) {
		pionsJoueur1 = new ArrayList<Position>();
		pionsJoueur2 = new ArrayList<Position>();
		anciennePositionDernierPion1 = null;
		dernierPionJoue1 = null;
		anciennePositionDernierPion2 = null;
		dernierPionJoue2 = null;
		nbPionsMainMax = nbPionsMain;
		nbPionsRestantsMain1 = nbPionsMainMax;
		nbPionsRestantsMain2 = nbPionsMainMax;
		hauteur = 5;
		largeur = 6;
	}
	
	public void prendre(int numJoueur, int x1, int y1, int x2, int y2, int x2emePion, int y2emePion) {
		
		deplacer(numJoueur, x1, y1, x2, y2);
		
		int xASuppr = x1;
		int yASuppr = y1;
		
		if(x1 > x2)
			xASuppr = x1-1;
		else if(x1 < x2)
			xASuppr = x1+1;
		
		if(y1 > y2)
			yASuppr = y1-1;
		else if(y1 < y2)
			yASuppr = y1+1;
		
		if(numJoueur == 2) {
			pionsJoueur1.remove(getPositionToJoueur(1, xASuppr, yASuppr));
			pionsJoueur1.remove(getPositionToJoueur(1, x2emePion, y2emePion));
			anciennePositionDernierPion2 = null;
			dernierPionJoue2 = null;
		}
		else {
			pionsJoueur2.remove(getPositionToJoueur(2, xASuppr, yASuppr));
			pionsJoueur2.remove(getPositionToJoueur(2, x2emePion, y2emePion));
			anciennePositionDernierPion1 = null;
			dernierPionJoue1 = null;
		}
		
	}
		
	public void poser(int numJoueur, int x, int y) {
		
		if(numJoueur == 2) {
			pionsJoueur2.add(new Position(x, y));
			anciennePositionDernierPion2 = null;
			dernierPionJoue2 = null;
			nbPionsRestantsMain2--;
		}
		else {
			pionsJoueur1.add(new Position(x, y));
			anciennePositionDernierPion1 = null;
			dernierPionJoue1 = null;
			nbPionsRestantsMain1--;
		}
		
	}
	
	public void deplacer(int numJoueur, int x1, int y1, int x2, int y2) {
		
		getPositionToJoueur(numJoueur, x1, y1).deplacer(x2, y2);
		if(numJoueur == 1) {
			anciennePositionDernierPion1 = new Position(x1, y1);
			dernierPionJoue1 = getPositionToJoueur(1, x2, y2);
		}
		else {
			anciennePositionDernierPion2 = new Position(x1, y1);
			dernierPionJoue2 = getPositionToJoueur(1, x2, y2);
		}
		
	}
	
	public void trierPions(int numJoueur) {
		if(numJoueur == 2)
			Collections.sort(pionsJoueur2, new ComparateurPosition());
		else
			Collections.sort(pionsJoueur1, new ComparateurPosition());
	}
	
	public void trierTousPions() {
		trierPions(1);
		trierPions(2);
	}
	
	public ArrayList<Position> getPionsJoueur1() {
		return pionsJoueur1;
	}

	public ArrayList<Position> getPionsJoueur2() {
		return pionsJoueur2;
	}
	
	public Position getPositionToJoueur(int numJoueur, int x, int y) {
		
		Position positionTemp = null;
		
		if(numJoueur == 2) {
			for(Position p : pionsJoueur2)
				if(p.getX() == x && p.getY() == y)
					positionTemp = p;
		}
		else {
			for(Position p : pionsJoueur1)
				if(p.getX() == x && p.getY() == y)
					positionTemp = p;
		}
		
		return positionTemp;
		
	}
	
	public String getPionsJoueurToString(int numJoueur) {
		String retourne = "[";
		
		if(numJoueur == 2) {
			for(Position p : pionsJoueur2)
				retourne += p.toString() + ",";
		}
		else {
			for(Position p : pionsJoueur1)
				retourne += p.toString() + ",";
		}
		if(retourne.length()>1)
			retourne = retourne.substring(0,retourne.length()-1);
		retourne += "]";
		
		return retourne;
	}
	
	public void jouer(int numJoueur, Coups coups) {
		switch (coups.getTypeCoups()) {
			case POSE:
				poser(numJoueur, coups.getX2(), coups.getY2());
				break;
			case DEPLACE:
				deplacer(numJoueur, coups.getX1(), coups.getY1(), coups.getX2(), coups.getY2());
				break;
			case PRISE:
				prendre(numJoueur, coups.getX1(), coups.getY1(), coups.getX2(), coups.getY2(), coups.getX2emePion(), coups.getY2emePion());
				break;
			default:
				break;
		}
		trierPions(numJoueur);
		
	}
	
	public int getNbPionMain(int numJoueur) {
		if(numJoueur == 2)
			return nbPionsRestantsMain2;
		else
			return nbPionsRestantsMain1;
	}
	
	public Position getDernierPionJoue(int numJoueur) {
		if(numJoueur == 2)
			return dernierPionJoue2;
		else
			return dernierPionJoue1;
	}

	public Position getAnciennePositionDernierPion(int numJoueur) {
		if(numJoueur == 2)
			return anciennePositionDernierPion2;
		else
			return anciennePositionDernierPion1;
	}
	
	public ArrayList<Position> getPionsJoueur(int numJoueur) {
		if(numJoueur == 2)
			return pionsJoueur2;
		else
			return pionsJoueur1;
	}
	
	public boolean aGagne(int numJoueur) {
		
		boolean aGagne = false;
		
		if(getNbPionMain(adversaire(numJoueur)) <= 0) {
			if(getPionsJoueur(adversaire(numJoueur)).size() <= 0)
				aGagne = true;
			else {
				aGagne = true;
				for(Position p : getPionsJoueur(adversaire(numJoueur)))
					if(pionPeutEtreDeplace(adversaire(numJoueur), p))
						aGagne = false;
			}
		}

		return aGagne;
		
	}
	
	public boolean pionPeutEtreDeplace(int numJoueur, Position pion) {
		
		boolean peutBouger = false;
		
		if(caseVideEtExiste(pion.getX()+1, pion.getY()) || caseVideEtExiste(pion.getX()-1, pion.getY()) || caseVideEtExiste(pion.getX(), pion.getY()+1) || caseVideEtExiste(pion.getX(), pion.getY()-1))
			peutBouger = true;
		else if(caseVideEtExiste(pion.getX()+2, pion.getY()) && getPositionToJoueur(adversaire(numJoueur), pion.getX()+1, pion.getY()) != null)
			peutBouger = true;
		else if(caseVideEtExiste(pion.getX()-2, pion.getY()) && getPositionToJoueur(adversaire(numJoueur), pion.getX()-1, pion.getY()) != null)
			peutBouger = true;
		else if(caseVideEtExiste(pion.getX(), pion.getY()+2) && getPositionToJoueur(adversaire(numJoueur), pion.getX(), pion.getY()+1) != null)
			peutBouger = true;
		else if(caseVideEtExiste(pion.getX(), pion.getY()-2) && getPositionToJoueur(adversaire(numJoueur), pion.getX(), pion.getY()-1) != null)
			peutBouger = true;
		
		return peutBouger;
		
	}
	
	public boolean valider(int numJoueur, Coups coups) {

		int diffX = Math.abs(coups.getX1()-coups.getX2());
		int diffY = Math.abs(coups.getY1()-coups.getY2());
		
		boolean estValide = true;
		
		if(coups.getTypeCoups()<0 || coups.getTypeCoups()>GAGNE || coups.getX1()<0 || coups.getX1()>hauteur || coups.getX2()<0 || coups.getX2()>hauteur || 
			coups.getX2emePion()<0 || coups.getX2emePion()>hauteur || coups.getY1()<0 || coups.getY1()>largeur || coups.getY2()<0 || 
			coups.getY2()>largeur || coups.getY2emePion()<0 || coups.getY2emePion()>largeur)
			estValide = false;
		else if(coups.getTypeCoups() == POSE && (getNbPionMain(numJoueur) <=0 || getPositionToJoueur(1, coups.getX2(), coups.getY2()) != null || getPositionToJoueur(2, coups.getX2(), coups.getY2()) != null))
			estValide = false;
		else if((coups.getTypeCoups() == DEPLACE || coups.getTypeCoups() == PRISE) && (getPositionToJoueur(numJoueur, coups.getX1(), coups.getY1()) == null || getPositionToJoueur(1, coups.getX2(), coups.getY2()) != null || getPositionToJoueur(2, coups.getX2(), coups.getY2()) != null))
			estValide = false;
		else if((coups.getTypeCoups() == PRISE) && getPositionToJoueur(adversaire(numJoueur), coups.getX2emePion(), coups.getY2emePion()) == null)
			estValide = false;
		else if((coups.getTypeCoups() == DEPLACE || coups.getTypeCoups() == PRISE) && getAnciennePositionDernierPion(numJoueur) != null && 
				getDernierPionJoue(numJoueur) != null && getDernierPionJoue(numJoueur).getX() == coups.getCaseDepart().getX() && 
				getDernierPionJoue(numJoueur).getY() == coups.getCaseDepart().getY() && getAnciennePositionDernierPion(numJoueur).getX() == coups.getCaseArrivee().getX() && 
				getAnciennePositionDernierPion(numJoueur).getY() == coups.getCaseArrivee().getY())
			estValide = false;
		else if(coups.getTypeCoups() == DEPLACE && !((diffX == 1 && diffY == 0) || (diffX == 0 && diffY == 1)) )
			estValide = false;
		else if(coups.getTypeCoups() == PRISE && !((diffX == 2 && diffY == 0) || (diffX == 0 && diffY == 2)) )
			estValide = false;
		else if(coups.getTypeCoups() == PRISE) {
			
			int xASuppr = coups.getX1();
			int yASuppr = coups.getY1();
			
			if(coups.getX1() > coups.getX2())
				xASuppr = coups.getX1()-1;
			else if(coups.getX1() < coups.getX2())
				xASuppr = coups.getX1()+1;
			
			if(coups.getY1() > coups.getY2())
				yASuppr = coups.getY1()-1;
			else if(coups.getY1() < coups.getY2())
				yASuppr = coups.getY1()+1;
			
			if(getPositionToJoueur(adversaire(numJoueur), xASuppr, yASuppr) == null)
				estValide = false;
	
		}
		
		return estValide;
	}
	
	public String plateauToString() {
		
		String plateau = "______";
		
		for(int i = 0;i < largeur; i++)
			plateau += "________";
		plateau += "\n_____|";
		for(int i = 0;i < largeur; i++)
			plateau += "___" + i + "___|";
		plateau += "\n";
		
		for(int i = 0;i < hauteur; i++) {
			plateau += "__" + i + "__|";
			for(int j = 0;j < largeur; j++) {
				if(getPositionToJoueur(1, i, j) != null)
					plateau += "   " + 1 + "   |";
				else if(getPositionToJoueur(2, i, j) != null)
					plateau += "   " + 2 + "   |";
				else
					plateau += "       |";
			}
			plateau += "\n";
		}
		
		return plateau;
		
	}
	
	public void restart() {
		pionsJoueur1 = new ArrayList<Position>();
		pionsJoueur2 = new ArrayList<Position>();
		anciennePositionDernierPion1 = null;
		dernierPionJoue1 = null;
		anciennePositionDernierPion2 = null;
		dernierPionJoue2 = null;
		nbPionsRestantsMain1 = nbPionsMainMax;
		nbPionsRestantsMain2 = nbPionsMainMax;
		hauteur = 5;
		largeur = 6;
	}
	
	public int adversaire(int numJoueur) {
		
		if(numJoueur == 2)
			return 1;
		else
			return 2;
		
	}
	
	public boolean caseVideEtExiste(int x, int y) {
		
		if(getPositionToJoueur(1, x, y) == null && getPositionToJoueur(2, x, y) == null && x >= 0 && y >=0 && x < hauteur && y < largeur)
			return true;
		else
			return false;
		
	}
	
	public void setNbPionsRestantsMain1(int nbPionsRestantsMain1) {
		this.nbPionsRestantsMain1 = nbPionsRestantsMain1;
	}

	public void setNbPionsRestantsMain2(int nbPionsRestantsMain2) {
		this.nbPionsRestantsMain2 = nbPionsRestantsMain2;
	}
	
}